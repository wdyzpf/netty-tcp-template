package com.jihan.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jihan.device.service.NettyServerService;

/**
 * @Author jihan
 * @Time 2018年7月11日 上午15:52:42
 * @Version 1.0
 * Description:容器关闭的钩子——开发中热部署端口不释放
 **/
@Component
public class AppDisposable implements DisposableBean {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NettyServerService nettyServerService;

	@Override
	public void destroy() throws Exception {
		nettyServerService.shutdownNettyServer();
		logger.error("应用程序关闭");
	}
}
