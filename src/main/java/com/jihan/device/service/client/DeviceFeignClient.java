package com.jihan.device.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author jihan
 * 	name：指定FeignClient的名称，如果项目使用了Ribbon，name属性会作为微服务的名称，用于服务发现
 *	url: url一般用于调试，可以手动指定@FeignClient调用的地址
 *	decode404:当发生http 404错误时，如果该字段位true，会调用decoder进行解码，否则抛出FeignException,Feign对于2XX和404 ，都不会走Fallback了
 *	configuration: Feign配置类，可以自定义Feign的Encoder、Decoder、LogLevel、Contract
 *	fallback: 定义容错的处理类，当调用远程接口失败或超时时，会调用对应接口的容错逻辑，fallback指定的类必须实现@FeignClient标记的接口
 *	fallbackFactory: 工厂类，用于生成fallback类示例，通过这个属性我们可以实现每个接口通用的容错逻辑，减少重复的代码
 *	path: 定义当前FeignClient的统一前缀
 */
@FeignClient(name = "device-server", url = "http://${business.platform.host}:${business.platform.port}", path="api" ,decode404 = true, fallback = DeviceFeignClientImpl.class)
public interface DeviceFeignClient {
	/**
	 * 系统升级响应——发送给后端业务系统
	 * @param reqObj
	 * @return
	 * @throws Exception
	 */
    @PostMapping("/device/update")
    JSONObject requestUpdateResponse(JSONObject reqObj) throws Exception;
    
    /**
     * 设备配置信息——发送给后端业务系统
     * @param reqObj
     * @return
     * @throws Exception
     */
    @PostMapping("/device/config")
    JSONObject requestDeviceInfo(JSONObject reqObj) throws Exception;
    
    /**
     * 重启响应——发送给后端业务系统
     * @param reqObj
     * @return
     * @throws Exception
     */
    @PostMapping("/device/reboot")
    JSONObject requestRebootResponse(JSONObject reqObj) throws Exception;
    
}
