package com.jihan.device.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jihan.device.service.client.DeviceFeignClient;

/**
 * @Author jihan
 * @Time 2018年11月7日 下午1:30:52
 * @Version 1.0
 * Description: 处理阻塞耗时操作
 **/

@Service
public class SendToBusinessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DeviceFeignClient deviceFeignClient;
	
	/**
	 * 重点是@Async
	 * 
	 * @Async("deviceTaskExecutor") 要和配置TaskExecutorConfig中的bean name一致，才能使用线程池
	 */
	@Async("deviceTaskExecutor")
	public void requestUpdateResponse(String deviceId, JSONObject reqObj) {
		MDC.put("deviceId", deviceId);
		try {
			JSONObject response = deviceFeignClient.requestUpdateResponse(reqObj);
			logger.info("proxy-->bussiness 升级报文响应:" + response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("升级报文响应", e);
		}finally{
			MDC.clear();
		}
	}
	
	@Async("deviceTaskExecutor")
	public void requestDeviceInfo(String deviceId, JSONObject reqObj) {
		MDC.put("deviceId", deviceId);
		try {
			JSONObject response = deviceFeignClient.requestDeviceInfo(reqObj);
			logger.info("proxy-->bussiness 设备配置信息：" + response.toJSONString());
		} catch (Exception e) {
			logger.error("设备信息", e);
		}finally{
			MDC.clear();
		}
	}

	@Async("deviceTaskExecutor")
	public void requestSystemShutdownRestartResponse(String deviceId, JSONObject reqObj) {
		MDC.put("deviceId", deviceId);
		try {
			JSONObject response = deviceFeignClient.requestRebootResponse(reqObj);
			logger.info("proxy-->bussiness 重启响应：" + response.toJSONString());
		} catch (Exception e) {
			logger.error("关机、重启响应", e);
		}finally{
			MDC.clear();
		}
	}
}
