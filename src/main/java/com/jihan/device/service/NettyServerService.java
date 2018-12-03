package com.jihan.device.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jihan.device.handler.DeviceBaseHandler;
import com.jihan.device.handler.DeviceServiceHandler;
import com.jihan.device.handler.encoder.DeviceMessageEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * @Author jihan
 * @Time 2018年10月15日 上午11:09:32
 * @Version 1.0 Description:
 **/

@Service
public class NettyServerService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${netty.server.port}")
	private Integer nettyPort;

	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	/**
	 * 启动netty server
	 * 
	 * @throws InterruptedException
	 */
	@Async
	public void startNettyAsync() throws InterruptedException {
		try {
			// Configure the server.
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							// 处理心跳,心跳周期15分钟
							p.addLast(new IdleStateHandler(16, 0, 0, TimeUnit.MINUTES));
							p.addLast(new LoggingHandler("io.netty.handler.logging"));
							p.addLast(new LineBasedFrameDecoder(1024));
							// p.addLast(new DelimiterBasedFrameDecoder());
							p.addLast(new StringDecoder(CharsetUtil.UTF_8));
							p.addLast(new StringEncoder(CharsetUtil.UTF_8));
							p.addLast(new DeviceMessageEncoder());
							p.addLast(new DeviceBaseHandler());
							p.addLast(new DeviceServiceHandler());
						}
					});
			ChannelFuture f = b.bind(nettyPort).sync();
			f.channel().closeFuture().sync();
		} finally {
			logger.warn("netty server shutdown");
			if (!bossGroup.isShutdown()) {
				bossGroup.shutdownGracefully();
			}
			if (!workerGroup.isShutdown()) {
				workerGroup.shutdownGracefully();
			}
		}
	}

	/**
	 * 关闭netty server
	 */
	public void shutdownNettyServer() {
		logger.error("调用netty server shutdown");
		if (!bossGroup.isShutdown()) {
			bossGroup.shutdownGracefully();
		}
		if (!workerGroup.isShutdown()) {
			workerGroup.shutdownGracefully();
		}
	}
}
