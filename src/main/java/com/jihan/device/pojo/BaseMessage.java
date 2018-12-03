package com.jihan.device.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Author jihan
 * @Time 2018年9月20日 上午9:34:15
 * @Version 1.0
 * Description:
 **/
public class BaseMessage {
	/**
	 * 消息id,每个消息都有一个唯一的id
	 */
	@JSONField(name="message_id")
	private String messageId;
	
	/**
	 * 设备id
	 */
	@JSONField(name="device_id")
	private String deviceId;
	
	/**
	 * 报文类型
	 */
	@JSONField(name="message_type")
	private Integer messageType;
 
	/**
	 * 报文发送时间
	 */
	@JSONField(name="req_time")
	private String reqTime;
	
	private String sign;
	
	private JSONObject data;
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public Integer getMessageType() {
		return messageType;
	}
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}
	public String getReqTime() {
		return reqTime;
	}
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}

	
}
