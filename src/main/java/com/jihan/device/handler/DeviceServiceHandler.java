package com.jihan.device.handler;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.alibaba.fastjson.JSONObject;
import com.jihan.device.core.Constants;
import com.jihan.device.core.annotation.InboundHandlerAction;
import com.jihan.device.pojo.BaseMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @Author jihan
 * @Time 2018年9月30日 上午9:42:01
 * @Version 1.0
 * Description: 通过反射，消息路由
 *
 */
public class DeviceServiceHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
				// InboundHandlerController.java
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

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();
	}

	/**
	 * 用于向下游业务发送离线事件
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		//TODO:
	}
}
