package com.jihan.device.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.jihan.device.core.annotation.InboundHandlerMapping;
import com.jihan.device.core.annotation.NettyController;
import com.jihan.device.pojo.BaseMessage;
import com.jihan.device.service.SendToBusinessService;
import com.jihan.device.util.ReturnUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author jihan
 * @Time 2018年11月7日 上午9:34:01
 * @Version 1.0
 * Description: 下面的所有方法都是在netty的work线程中处理，不要有阻塞的操作！！！
 **/

@Component
@NettyController
public class InboundHandlerController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SendToBusinessService sendToDeviceService;
	/**
	 * 升级响应处理
	 * @param str
	 * @return
	 */
	@InboundHandlerMapping(2)
	public void updateResponseDeal(ChannelHandlerContext ctx, BaseMessage bm) {
		String deviceId = bm.getDeviceId();
		JSONObject dataJson = bm.getData();
		logger.info("device-->proxy 升级报文响应:" + dataJson);
		
		JSONObject objJson = new JSONObject();
		objJson.put("device_id", deviceId);
		objJson.put("data", dataJson);
		//异步调用
		sendToDeviceService.requestUpdateResponse(deviceId, objJson);
	}
	
	/**
	 * 经纬度坐标处理
	 * @param ctx
	 * @param bm
	 */
	@InboundHandlerMapping(3)
	public void locationDeal(ChannelHandlerContext ctx, BaseMessage bm) {
		JSONObject addrJson = bm.getData();
		logger.info("device-->proxy 经纬度坐标:" + addrJson);
		ctx.channel().write(JSONObject.toJSON(bm));
	}
	
	/**
	 * 设备配置详情
	 * @param ctx
	 * @param bm
	 */
	@InboundHandlerMapping(4)
	public void deviceInfoDeal(ChannelHandlerContext ctx, BaseMessage bm) {
		String deviceId = bm.getDeviceId();
		JSONObject dataJson = bm.getData();
		logger.info("device-->proxy 设备配置:" + dataJson);
		
		dataJson.put("device_id", deviceId);
		//异步调用
		sendToDeviceService.requestDeviceInfo(deviceId, dataJson);
		bm.setData(ReturnUtil.successfulResponse());
		ctx.channel().write(JSONObject.toJSON(bm));
	}
	
	/**
	 * 关机、重启响应处理
	 * @param ctx
	 * @param bm
	 */
	@InboundHandlerMapping(5)
	public void systemShutdownRestartResponseDeal(ChannelHandlerContext ctx, BaseMessage bm) {
		String deviceId = bm.getDeviceId();
		JSONObject dataJson = bm.getData();
		logger.info("device-->proxy 关机、重启响应:" + dataJson);

		JSONObject objJson = new JSONObject();
		objJson.put("device_id", deviceId);
		objJson.put("data", dataJson);
		//异步调用
		sendToDeviceService.requestSystemShutdownRestartResponse(deviceId, objJson);
	}
	
	// ......
}
