package com.jihan.device.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import io.netty.channel.Channel;

/**
 * @Author jihan
 * @Time 2018年9月29日 上午9:02:52
 * @Version 1.0
 * Description: 存放device_id 和 channel的关系
 **/
public class NettyChannelMap {
	/**
	 * <设备序列号，channel>
	 */
	private static Map<String,Channel> chanelMap = new ConcurrentHashMap<>();

	public static Map<String, Channel> getChanelMap() {
		return chanelMap;
	}
	
	public static void putDevieChanel(String deviceId, Channel channel) {
		chanelMap.put(deviceId, channel);
	}
	
	public static boolean containsDeviceChanel(String deviceId) {
		return chanelMap.containsKey(deviceId);
	}

	public static void setChanelMap(Map<String, Channel> value) {
		chanelMap = value;
	}

	/**
	 * 通过device id 返回channel
	 * @param deviceId
	 * @return
	 */
	public static Channel getChanelByDeviceId(String deviceId) {
		if(StringUtils.isEmpty(deviceId)) {
			return null;
		}
		if(chanelMap.containsKey(deviceId)) {
			return chanelMap.get(deviceId);
		}else {
			return null;
		}
	}
}
