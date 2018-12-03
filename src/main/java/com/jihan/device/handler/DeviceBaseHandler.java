package com.jihan.device.handler;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.jihan.device.core.Constants;
import com.jihan.device.core.NettyChannelMap;
import com.jihan.device.pojo.BaseMessage;
import com.jihan.device.pojo.ChannelAttribute;
import com.jihan.device.pojo.LoginMessage;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * 认证、心跳处理 
 * 
 * @Author jihan
 * @Time 2018年9月28日 上午10:06:48
 * @Version 1.0 Description:
 *
 */
public class DeviceBaseHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 将deviceId,是否认证通过等属性放到channel的Attribute中
	 */
	private AttributeKey<ChannelAttribute> channelAttr = AttributeKey.valueOf("netty.channel.attr");
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 初始化ChannelAttribute
		ctx.channel().attr(channelAttr).set(new ChannelAttribute());
		super.channelActive(ctx);
	}

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

	/**
	 * 读超时处理
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				ctx.channel().close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	/**
	 * channel 注销清理服务端的缓存
	 */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		String id = ctx.channel().attr(channelAttr).get().getDeviceId();
		if (StringUtils.isNotBlank(id)) {
			String channelId = ctx.channel().id().asShortText();
			String oldChanelId = NettyChannelMap.getChanelByDeviceId(id).id().asShortText();
			if (channelId.equals(oldChanelId)) {
				NettyChannelMap.getChanelMap().remove(id);
				MDC.put("deviceId", id);
				logger.info("清理device的服务端缓存：" + id);
				MDC.clear();
			} else {
				logger.error("device 短时间内建立了多个链接：" + id);
			}
		}
		super.channelUnregistered(ctx);
	}

	/**
	 *  异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}

	/**
	 * 认证设备
	 * 
	 * @param bm
	 * @return
	 */
	private Object deviceAuthentication(BaseMessage bm, ChannelHandlerContext ctx, ChannelAttribute attr) {
		String deviceId = bm.getDeviceId();
		JSONObject jsonObj = new JSONObject();
		boolean containsdevice = NettyChannelMap.containsDeviceChanel(deviceId);
		if (!containsdevice) {
			LoginMessage loginMsg = bm.getData().toJavaObject(LoginMessage.class);
			String password = Constants.DEVICE_AUTH_MAP.get(loginMsg.getUsername());
			if((null != password) && password.equals(loginMsg.getPassword())) {
				// 标识这个channel验证通过
				attr.setAuth(true);
				attr.setDeviceId(deviceId);
				// deviceId 和 channel的对应关系
				NettyChannelMap.putDevieChanel(deviceId, ctx.channel());
				jsonObj.put("res_code", "0000");
				jsonObj.put("res_desc", "success");
			}else {
				jsonObj.put("res_code", "1001");
				jsonObj.put("res_desc", "password error");
			}
		} else {
			//客户端多次重复连接
			jsonObj.put("res_code", "9999");
			jsonObj.put("res_desc", " There are multiple socket");
		}

		bm.setData(jsonObj);
		return JSONObject.toJSON(bm);
	}

	/**
	 * 没有认证的设备处理
	 * 
	 * @param bm
	 * @param ctx
	 */
	private void noAuthDeal(BaseMessage bm, ChannelHandlerContext ctx) {
		bm.setMessageType(1);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("res_code", "9999");
		jsonObj.put("res_desc", "Not certified");
		bm.setData(jsonObj);
		// write后关闭链接
		ChannelFuture future = ctx.channel().write(JSONObject.toJSON(bm));
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isDone()) {
					ctx.channel().close();
				}
			}
		});
	}
}
