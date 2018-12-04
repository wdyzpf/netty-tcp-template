


如何优雅的处理Netty TCP私有报文

你是否还记得当年写过的烂代码......一个让你看了恐惧，队友看了想砍你的代码。本文就是为了铭记当年自己的一段烂代码而生，也希望对大家有所帮助。
依稀记得那年刚毕业，用switch case 写下了无数个条件判断。在企业私有报文中一般用type来区分此条报文的类型，并通过switch case判断做相应的业务处理。这样当type增加到几十个甚至上百个时就难以维护，而且多人开发时冲突不断。

下面给大家介绍一个优雅的处理办法：
用到的的技术栈：
1.SpringBoot 2.0
2.Netty4.1
3.SpringCloud中的Feign组件
4.spring线程池、java注解、反射

具体思路：
1.定义@NettyController和@InboundHandlerMapping(3)注解，在具体的业务处理类和方法上分别添加上面两个注解
2.通过实现BeanPostProcessor添加一个注解处理器，在程序启动时扫描上面两个注解，并建立报文类型和处理方法的映射关系
3.在继承 ChannelInboundHandlerAdapter的channelRead方法中通过报文type找到具体的处理方法，然后通过反射调用method
4.为了防止Netty的worker线程被阻塞，对于耗时的IO操作我们单独定义一个线程池处理（比如写数据库或向后端发送请求）
5.最后一步，如何将设备上传的消息发送给后端业务？这里我采用的是Feign，Feign是spring cloud中服务消费端的调用框架,通常与ribbon,hystrix等组合使用，实现了客户端负载均衡和断路器。当然这里不局限使用HTTP也可以用MQ消息队列。

核心代码
1.心跳和认证处理Handler

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

	ChannelAttribute attr = ctx.channel().attr(channelAttr).get();
	
	// 每次请求都更新最后read时间
	attr.setLasttime(System.currentTimeMillis());

	String str = (String) msg;
	BaseMessage bm = JSONObject.parseObject(str, BaseMessage.class);
	int messageType = bm.getMessageType();
	if (messageType == 0) {
		// 心跳的type=0
		JSONObject heart = new JSONObject();
		heart.put("message_type", 0);
		ctx.channel().write(heart);
	} else if (messageType == 1) {
		// 认证type=1
		Object resqObj = deviceAuthentication(bm, ctx, attr);
		ctx.channel().writeAndFlush(resqObj);
	} else {
		if (attr.getAuth()) {
			// 认证后的报文传送到下一个handler
			ctx.fireChannelRead(msg);
		} else {
			noAuthDeal(bm, ctx);
		}
	}
}

 
ChannelAttribute 用来保存与channel相关联的信息，比如device_id、是否认证通过。比如在channelInactive 方法中我们可以直接得到device_id，并产生相应的离线事件。

2.注解处理类

@Override
public Object postProcessAfterInitialization(Object bean, String beanName){

	// 处理@NettyController
	Boolean annoBool = bean.getClass().isAnnotationPresent(NettyController.class);
	if (!annoBool) {
		return bean;
	}
	Method[] methods = bean.getClass().getMethods();
	for (Method method : methods) {
		//处理@InboundHandlerMapping
		InboundHandlerMapping actionMap = method.getAnnotation(InboundHandlerMapping.class);
		if (actionMap != null) {
			InboundHandlerAction ihp = new InboundHandlerAction();
			ihp.setMethod(method);
			ihp.setObject(bean);
			//缓存method和报文type映射关系
			Constants.INBOUND_HANDLER_MAP.put(actionMap.value(), ihp);
		}
	}
	return bean;
}

通过实现BeanPostProcessor类，在程序启动的时候将type和method的关系放到Map中（注解缓存），方便后面反射调用使用。
3.消息路由Handler

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg){

	String str = (String) msg;
	BaseMessage bm = JSONObject.parseObject(str, BaseMessage.class);
	String deviceId = bm.getDeviceId();

	InboundHandlerAction ihp = Constants.INBOUND_HANDLER_MAP.get(bm.getMessageType());
	if (ihp != null) {
		Method method = ihp.getMethod();
		try {
			MDC.put("deviceId", deviceId);
			// 处理类InboundHandlerController.java
			method.invoke(ihp.getObject(), ctx, bm);
		} catch (Exception e) {
			logger.error("反射处理异常", e);
		}finally {
			MDC.clear();
		}
	}else {
		logger.warn("映射失败，无法解析！" + str);
	}
}

在我们自定义的Handler中通过报文type找到对应Method,然后通过invoke 调用实现方法。
4.线程池配置
为什么会使用线程池？因为线程池可以帮助我们解耦后端业务和Netty处理逻辑而且可以TaskExecutor 中的任务积压后不会导致使用Netty Worker线程的Handler被阻塞。线程池自带一个queue在任务量突然增加的时候也可以起到缓冲作用。这里使用的是spring线程池（主要是使用方便），当然也可以使用java提供的其他线程池。

@Bean
public TaskExecutor deviceTaskExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	// 设置核心线程数
	executor.setCorePoolSize(5);
	// 设置最大线程数
	executor.setMaxPoolSize(100);
	// 设置队列容量
	executor.setQueueCapacity(1000);
	// 设置线程活跃时间（秒）
	executor.setKeepAliveSeconds(60);
	// 设置默认线程名称
	executor.setThreadNamePrefix("device-task-");
	// 设置拒绝策略-抛出异常
	executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
	// 等待所有任务结束后再关闭线程池
	executor.setWaitForTasksToCompleteOnShutdown(true);
	return executor;
}

拒绝策略是一个比较重要的配置，可以分别配置为：
AbortPolicy() 当线程池中的数量等于最大线程数时、直接抛出抛出java.util.concurrent.RejectedExecutionException异常
CallerRunsPolicy() 当线程池中的数量等于最大线程数时、重试执行当前的任务，交由调用者线程来执行任务
DiscardOldestPolicy() 当线程池中的数量等于最大线程数时、抛弃线程池中最后一个要执行的任务，并执行新传入的任务
DiscardPolicy() 当线程池中的数量等于最大线程数时，不做任何动作

5.Feign 配置
使用Feign可以天然具备服务熔断和服务降级功能，因为在Feign 中集成了Hystrix。
fallback 定义了服务降级返回数据

@FeignClient(name = "device-server", url = "http://${business.platform.host}:${business.platform.port}", path="api" ,decode404 = true, fallback = DeviceFeignClientImpl.class)
public interface DeviceFeignClient {
	/**
	 * 系统升级响应——发送给后端业务系统
	 */
    @PostMapping("/device/update")
    JSONObject requestUpdateResponse(JSONObject reqObj) throws Exception;
    
    /**
     * 设备配置信息——发送给后端业务系统
     */
    @PostMapping("/device/config")
    JSONObject requestDeviceInfo(JSONObject reqObj) throws Exception;
    
    /**
     * 重启响应——发送给后端业务系统
     */
    @PostMapping("/device/reboot")
    JSONObject requestRebootResponse(JSONObject reqObj) throws Exception;
    
}

6.日志配置
日志配置不再贴出具体代码，可以通过下面的Github地址下载项目。
日志使用的logback，可以方便的配置为一个设备一个日志文件，并通过MDC传入device id。

END：

