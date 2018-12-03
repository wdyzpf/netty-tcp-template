package com.jihan.device.core.annotation;

import java.lang.reflect.Method;

/**
 * @Author jihan
 * @Time 2018年11月6日 下午5:48:21
 * @Version 1.0
 * Description:
 **/
public class InboundHandlerAction {
	
	private Method method;
	
	private Object object;
 
	public Method getMethod() {
		return method;
	}
 
	public void setMethod(Method method) {
		this.method = method;
	}
 
	public Object getObject() {
		return object;
	}
 
	public void setObject(Object object) {
		this.object = object;
	}
}
