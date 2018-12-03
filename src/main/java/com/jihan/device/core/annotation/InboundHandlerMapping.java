package com.jihan.device.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author jihan
 * @Time 2018年11月6日 下午5:37:13
 * @Version 1.0
 * Description:
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface InboundHandlerMapping {
	
	int value();
}
