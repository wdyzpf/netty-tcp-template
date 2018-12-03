package com.jihan.device.handler.encoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.alibaba.fastjson.JSONObject;
import com.jihan.device.pojo.ChannelAttribute;
import com.jihan.device.util.SignUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AttributeKey;

/**
 * 心跳处理 handler
 * 
 * @Author jihan
 * @Time 2018年9月28日 上午10:06:48
 * @Version 1.0 
 *
 */
public class DeviceMessageEncoder extends  MessageToMessageEncoder<JSONObject>  {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private AttributeKey<ChannelAttribute> channelAttr = AttributeKey.valueOf("netty.channel.attr");
	
	@Override
	protected void encode(ChannelHandlerContext ctx, JSONObject msg, List<Object> out) throws Exception {
		if(!msg.containsKey("message_type")) {
			return;
		}
		Integer messageType = msg.getInteger("message_type");
		//非心跳报文统一添加公共部分
		if(messageType != 0) {
			ChannelAttribute deviceAttr = ctx.channel().attr(channelAttr).get();
			String deviceId = deviceAttr.getDeviceId();
			//下发指令的发送序号
			if(!msg.containsKey("message_id")) {
				Long senNum = deviceAttr.getSendNumber() + 1;
				deviceAttr.setSendNumber(senNum);
				msg.put("message_id", senNum);
			}else {
				msg.put("message_id", (msg.getLong("message_id") + 1));
			}
			msg.put("device_id", deviceId);
			msg.put("req_time", System.currentTimeMillis());
			// sign不参与签名算法
			msg.remove("sign");
			msg.put("sign", SignUtil.sign("1234567890", msg.toJSONString()));
			
			MDC.put("deviceId", deviceId);
			logger.info("proxy-->device " + msg.toJSONString());
			MDC.clear();
		}
		//添加报文分隔符
		out.add(msg.toJSONString() + '\n');
		
	}
}
