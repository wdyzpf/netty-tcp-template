package com.jihan.device.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author jihan
 * @Time 2018年9月29日 上午11:49:53
 * @Version 1.0 Description:
 **/
public class VaildDeviceReboot {
	@JsonProperty(value = "device_id")
	@NotNull(message = "device id is null")
	private String deviceId;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}

