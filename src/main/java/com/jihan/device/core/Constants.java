package com.jihan.device.core;

import java.util.HashMap;
import java.util.Map;

import com.jihan.device.core.annotation.InboundHandlerAction;

/**
 * 
 * @Author jihan
 * @Time 2018年8月22日 下午3:29:49
 * @Version 1.0
 * Description:常量
 *
 */
public class Constants {
    /**签名算法HmacSha256*/
    public static final String HMAC_SHA256 = "HmacSHA256";
    /**编码UTF-8*/
    public static final String ENCODING = "UTF-8";
    /**
     * 设备认证列表
     */
    public final static Map<String, String > DEVICE_AUTH_MAP = new HashMap<String, String>();

    static {
    	DEVICE_AUTH_MAP.put("dev", "dev123");
    	DEVICE_AUTH_MAP.put("test", "test123");
    }
    
    /**
     * 消息类型和Class、Method 对应关系，反射缓存
     */
    public static Map<Integer, InboundHandlerAction> INBOUND_HANDLER_MAP = new HashMap<Integer, InboundHandlerAction>();
}
