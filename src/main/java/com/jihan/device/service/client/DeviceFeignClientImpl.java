package com.jihan.device.service.client;

import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @Author jihan
 * @Time 2018年11月13日 下午2:37:45
 * @Version 1.0
 * Description: 服务降级处理
 *
 */
@Component
public class DeviceFeignClientImpl implements DeviceFeignClient{

	@Override
	public JSONObject requestUpdateResponse(JSONObject reqObj) {
		JSONObject respJsonObj = new JSONObject();
		respJsonObj.put("code", "7777");
		respJsonObj.put("msg", "升级响应——服务降级响应！");
		respJsonObj.put("reqObj", reqObj);
		return respJsonObj;
	}

	@Override
	public JSONObject requestDeviceInfo(JSONObject reqObj) {
		JSONObject respJsonObj = new JSONObject();
		respJsonObj.put("code", "7777");
		respJsonObj.put("msg", "设备配置响应——服务降级响应！");
		respJsonObj.put("reqObj", reqObj);
		return respJsonObj;
	}

	@Override
	public JSONObject requestRebootResponse(JSONObject reqObj) {
		JSONObject respJsonObj = new JSONObject();
		respJsonObj.put("code", "7777");
		respJsonObj.put("msg", "重启响应——服务降级响应！");
		respJsonObj.put("reqObj", reqObj);
		return respJsonObj;
	}
}
