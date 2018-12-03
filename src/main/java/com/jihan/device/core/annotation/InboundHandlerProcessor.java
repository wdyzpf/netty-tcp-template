package com.jihan.device.core.annotation;

import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.jihan.device.core.Constants;

/**
 * @Author jihan
 * @Time 2018年11月6日 下午5:52:35
 * @Version 1.0
 * Description: 注解处理器，程序启动时执行
 **/
@Component
public class InboundHandlerProcessor implements BeanPostProcessor {

	/**
	 * bean初始化方法调用前被调用 第一个为 bean 实例，第二个为这个 bean 的名称。
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * bean初始化方法调用后被调用 第一个为 bean 实例，第二个为这个 bean 的名称。
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
}
