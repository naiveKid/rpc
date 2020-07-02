package com.rpc.framework.remoting.transport.socket;

import com.rpc.framework.provider.ServiceProvider;
import com.rpc.framework.provider.ServiceProviderImpl;
import com.rpc.framework.registry.ServiceRegistry;
import com.rpc.framework.registry.ZkServiceRegistry;
import com.rpc.framework.remoting.transport.TransportServiceProxy;
import com.rpc.framework.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @author hxz
 */
@Slf4j
public class SocketRpcServiceProxy implements TransportServiceProxy {

	private final ExecutorService threadPool;
	private final String host;
	private final int port;
	private final ServiceRegistry serviceRegistry;
	private final ServiceProvider serviceProvider;


	public SocketRpcServiceProxy(String host, int port) {
		this.host = host;
		this.port = port;
		threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
		serviceRegistry = new ZkServiceRegistry();
		serviceProvider = new ServiceProviderImpl();
	}

	@Override
	public <T> void publishService(T service, Class<T> serviceClass) {
		serviceProvider.addServiceProvider(service, serviceClass);
		serviceRegistry.registerService(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
		start();
	}

	private void start() {
		try (ServerSocket server = new ServerSocket()) {
			server.bind(new InetSocketAddress(host, port));
			Socket socket;
			while ((socket = server.accept()) != null) {
				log.info("client connected [{}]", socket.getInetAddress());
				threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
			}
			threadPool.shutdown();
		} catch (IOException e) {
			log.error("occur IOException:", e);
		}
	}

}
