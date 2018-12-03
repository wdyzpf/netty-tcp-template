package com.jihan.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jihan.device.service.NettyServerService;

/**
 * @Author jihan
 * @Time 2018年3月15日 上午10:52:42
 * @Version 1.0
 * Description:容器启动后执行的一些操作
 **/
@Component
public class AppRunner implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NettyServerService nettyServerService;
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("device-Proxy启动。。。");
		nettyServerService.startNettyAsync();
	}
}
