package com.jihan.device.controller;
import java.util.Map;
import javax.validation.Valid;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.jihan.device.core.NettyChannelMap;
import com.jihan.device.core.error.AppException;
import com.jihan.device.pojo.VaildDeviceReboot;
import com.jihan.device.util.ReturnUtil;
import io.netty.channel.Channel;
 
/**
 * 
 * @Author jihan
 * @Time 2018年9月29日 上午8:54:22
 * @Version 1.0
 * Description:
 *
 */
@RestController
@RequestMapping("/api/device")
public class DeviceCommandController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 下发指令——设备重启
	 * @param vpsc
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reboot", method = RequestMethod.POST)
    public ModelMap reboot(@RequestBody @Valid VaildDeviceReboot vdr, BindingResult bindingResult) throws Exception {
		String posId = vdr.getDeviceId();
		//用于设置一个设备一个日志文件
    	MDC.put("deviceId", posId);
    	logger.info("业务服务器-->netty 重启指令:" + JSONObject.toJSON(vdr));
    	MDC.clear();
    	
    	if (bindingResult.hasErrors()) {
			throw new AppException(1404, bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
    	
    	Map<String, Channel> channelMap = NettyChannelMap.getChanelMap();
		if(channelMap.containsKey(posId)) {
			//封装报文
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("message_type", 5);
			//下发指令
			channelMap.get(posId).writeAndFlush(jsonObj);
			return ReturnUtil.success();
		}else {
			return ReturnUtil.error("1001", "离网");
		}
    }
    
	/**
	 * 控制器作用域异常处理
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ModelMap userExceptionHandler(Exception e) {
		ModelMap map = new ModelMap();
		if(e instanceof AppException) {
			AppException ee = (AppException) e;
			map.put("code", ee.getCode());
			map.put("msg", ee.getMsg());
		}else {
			map.put("code", 500);
			map.put("msg", e.getMessage());
		}
		logger.error("API 异常：" + map.get("msg"), e);
		return map;
	}
    
}
