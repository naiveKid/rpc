package com.rpc.framework.config.transport;

import com.rpc.framework.config.transport.client.ClientProperties;
import com.rpc.framework.config.transport.service.ServiceProperties;
import com.rpc.framework.enumeration.TransportTypeEnum;
import com.rpc.framework.proxy.RpcClientProxy;
import com.rpc.framework.remoting.transport.ClientTransport;
import com.rpc.framework.remoting.transport.TransportServer;
import com.rpc.framework.remoting.transport.netty.client.NettyClientTransport;
import com.rpc.framework.remoting.transport.netty.server.NettyServer;
import com.rpc.framework.remoting.transport.socket.SocketRpcClient;
import com.rpc.framework.remoting.transport.socket.SocketRpcServer;
import com.rpc.framework.utils.zk.CuratorUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//注明该类是一个Java配置类
@Configuration
//读取默认配置
@EnableConfigurationProperties({ServiceProperties.class, ClientProperties.class})
public class InitAutoConfiguration {
	private ServiceProperties serviceProperties;
	private ClientProperties clientProperties;

	//在application.properties中进行配置，将使用其创建好的配置
	public InitAutoConfiguration(ServiceProperties serviceProperties,ClientProperties clientProperties){
		this.serviceProperties = serviceProperties;
		this.clientProperties = clientProperties;
	}

	@Bean
	//当不存在创建好的实例，将使用该方法进行创建
	@ConditionalOnMissingBean(TransportServer.class)
	public TransportServer transportServer(ServiceProperties serviceProperties) {
		CuratorUtils.setConnectString(serviceProperties.getZookeeperUrl());
		if (TransportTypeEnum.NETTY.getCode().equals(serviceProperties.getTransportType().toLowerCase())) {
			return new NettyServer(serviceProperties.getTransportIp(), serviceProperties.getTransportPort());
		} else if (TransportTypeEnum.SOCKET.getCode().equals(serviceProperties.getTransportType().toLowerCase())) {
			return new SocketRpcServer(serviceProperties.getTransportIp(), serviceProperties.getTransportPort());
		}
		return new NettyServer(serviceProperties.getTransportIp(), serviceProperties.getTransportPort());
	}

	@Bean
	//当不存在创建好的实例，将使用该方法进行创建
	@ConditionalOnMissingBean(RpcClientProxy.class)
	public RpcClientProxy rpcClientProxy(ClientProperties clientProperties) {
		ClientTransport clientTransport=null;
		if (TransportTypeEnum.NETTY.getCode().equals(clientProperties.getTransportType().toLowerCase())) {
			 clientTransport = new NettyClientTransport();
		} else if (TransportTypeEnum.SOCKET.getCode().equals(clientProperties.getTransportType().toLowerCase())) {
			 clientTransport = new SocketRpcClient();
		}
		return new RpcClientProxy(clientTransport);
	}
}
