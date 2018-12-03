package com.jihan.device.core.error;
/**
 * @Author jihan
 * @Time 2018年3月14日 下午2:57:45
 * @Version 1.0
 * Description: API中产生的业务异常类
 **/
public class AppException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String msg;
	
	public AppException(String msg) {
		this.code = 400;
		this.msg = msg;
	}
	public AppException(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
