package com.jihan.device.pojo;

/**
 * @Author jihan
 * @Time 2018年10月11日 下午1:31:20
 * @Version 1.0
 * Description:
 **/
public class ChannelAttribute {
	/**
	 * 设备id
	 */
	private String deviceId;
	
	/**
	 * 是否认证通过,认证通过后server端才会接受后续的报文
	 */
	private Boolean auth = false;
	
	/**
	 * 最后响应时间
	 */
	private Long lasttime = System.currentTimeMillis();
	
	/**
	 * 下发指令发送序号,发送序号从1开始
	 */
	private Long sendNumber =  0L;
 
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public Boolean getAuth() {
		return auth;
	}
	public void setAuth(Boolean auth) {
		this.auth = auth;
	}
	public Long getLasttime() {
		return lasttime;
	}
	public void setLasttime(Long lasttime) {
		this.lasttime = lasttime;
	}
	public Long getSendNumber() {
		return sendNumber;
	}
	public void setSendNumber(Long sendNumber) {
		this.sendNumber = sendNumber;
	}
}
