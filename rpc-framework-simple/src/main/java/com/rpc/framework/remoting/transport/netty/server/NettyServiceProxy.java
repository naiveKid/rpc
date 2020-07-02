package com.rpc.framework.remoting.transport.netty.server;

import com.rpc.framework.provider.ServiceProvider;
import com.rpc.framework.provider.ServiceProviderImpl;
import com.rpc.framework.registry.ServiceRegistry;
import com.rpc.framework.registry.ZkServiceRegistry;
import com.rpc.framework.remoting.dto.RpcRequest;
import com.rpc.framework.remoting.dto.RpcResponse;
import com.rpc.framework.remoting.transport.TransportServiceProxy;
import com.rpc.framework.remoting.transport.netty.codec.kyro.NettyKryoDecoder;
import com.rpc.framework.remoting.transport.netty.codec.kyro.NettyKryoEncoder;
import com.rpc.framework.serialize.kyro.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端。接收客户端消息，并且根据客户端的消息调用相应的方法，然后返回结果给客户端。
 *
 * @author hxz
 */
@Slf4j
public class NettyServiceProxy implements TransportServiceProxy {
	private String host;
	private int port;
	private KryoSerializer kryoSerializer;
	private ServiceRegistry serviceRegistry;
	private ServiceProvider serviceProvider;
	private AtomicInteger publishNum = new AtomicInteger(0);

	public NettyServiceProxy(String host, int port) {
		this.host = host;
		this.port = port;
		kryoSerializer = new KryoSerializer();
		serviceRegistry = new ZkServiceRegistry();
		serviceProvider = new ServiceProviderImpl();
	}

	@Override
	public <T> void publishService(T service, Class<T> serviceClass) {
		log.info("发布:{}", service.getClass());
		serviceProvider.addServiceProvider(service, serviceClass);
		serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
		if (publishNum.incrementAndGet() == 1) {
			log.info("启动netty....");
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
					0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1),
					r -> new Thread(r, "t_pool_" + r.hashCode()));
			threadPoolExecutor.execute(this::start);
		}
	}

	private void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					// TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
					.childOption(ChannelOption.TCP_NODELAY, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					// 当客户端第一次进行请求的时候才会进行初始化
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							// 30 秒之内没有收到客户端请求的话就关闭连接
							ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
							ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
							ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
							ch.pipeline().addLast(new NettyServerHandler());
						}
					})
					// TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
					.childOption(ChannelOption.TCP_NODELAY, true)
					// 是否开启 TCP 底层心跳机制
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					//表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
					.option(ChannelOption.SO_BACKLOG, 128);

			// 绑定端口，同步等待绑定成功
			ChannelFuture f = b.bind(host, port).sync();
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error("occur exception when start server:", e);
		} finally {
			log.error("shutdown bossGroup and workerGroup");
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
