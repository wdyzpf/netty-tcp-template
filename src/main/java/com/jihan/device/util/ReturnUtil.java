package com.jihan.device.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @Author jihan
 * @Time 2018年8月22日 下午3:28:37
 * @Version 1.0
 * Description:JSON统一返回数据格式
 *
 */
public class ReturnUtil {

	//*******************http controller响应报文格式*******************
	
	public static ModelMap success() {
        ModelMap mp = new ModelMap();
        mp.put("code", "0000");
        mp.put("msg", "success");
        return mp;
	}
	 
    public static ModelMap success(Object data) {
        ModelMap mp = new ModelMap();
        mp.put("code", "0000");
        mp.put("msg", "success");
        mp.put("data", data);
        return mp;
    }

    public static ModelMap error(String msg) {
        msg = StringUtils.isEmpty(msg) || StringUtils.isBlank(msg) ? "请求异常" : msg;
        ModelMap mp = new ModelMap();
        mp.put("code", "9999");
        mp.put("msg", msg);
        return mp;
    }
    
    public static ModelMap error(String code, String msg) {
        msg = StringUtils.isEmpty(msg) || StringUtils.isBlank(msg) ? "请求异常" : msg;
        ModelMap mp = new ModelMap();
        mp.put("code", code);
        mp.put("msg", msg);
        return mp;
    }
    
    // ******************netty tcp消息响应格式*******************
    
    public static JSONObject successfulResponse() {
    	JSONObject restObj = new JSONObject();
		restObj.put("res_code", "0000");
		restObj.put("res_desc", "success");
        return restObj;
    }
    
    public static JSONObject errorResponse(String msg) {
    	msg = StringUtils.isEmpty(msg) || StringUtils.isBlank(msg) ? "异常" : msg;
    	JSONObject restObj = new JSONObject();
		restObj.put("res_code", "9999");
		restObj.put("res_desc", msg);
        return restObj;
    }
    
}
