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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

//注明该类是一个Java配置类
@Configuration
//读取默认配置
@EnableConfigurationProperties({ServiceProperties.class, ClientProperties.class})
public class InitAutoConfiguration {
	@Value("${rpc.zookeeper.url:127.0.0.1:2181}")
	private String zookeeperUrl;

	private ServiceProperties serviceProperties;
	private ClientProperties clientProperties;

	//在application.properties中进行配置，将使用其创建好的配置
	public InitAutoConfiguration(ServiceProperties serviceProperties,ClientProperties clientProperties){
		this.serviceProperties = serviceProperties;
		this.clientProperties = clientProperties;
	}

	/**
	 * PostConstruct 也是初始化操作,不过初始化过程不能依赖其他的bean.因为其他的bean很可能没有创建好
	 */
	@PostConstruct
	public void initialOperate(){
		if(!StringUtils.isEmpty(zookeeperUrl)){
			CuratorUtils.setConnectString(zookeeperUrl);
		}
	}

	@Bean
	//当不存在创建好的实例，将使用该方法进行创建
	@ConditionalOnMissingBean(TransportServer.class)
	public TransportServer transportServer(ServiceProperties serviceProperties) {
		if (TransportTypeEnum.NETTY.getCode().equals(serviceProperties.getType().toLowerCase())){
			return new NettyServer(serviceProperties.getIp(), serviceProperties.getPort());
		}else if (TransportTypeEnum.SOCKET.getCode().equals(serviceProperties.getType().toLowerCase())){
			return new SocketRpcServer(serviceProperties.getIp(), serviceProperties.getPort());
		}
		return new NettyServer(serviceProperties.getIp(), serviceProperties.getPort());
	}

	@Bean
	//当不存在创建好的实例，将使用该方法进行创建
	@ConditionalOnMissingBean(RpcClientProxy.class)
	public RpcClientProxy rpcClientProxy(ClientProperties clientProperties) {
		ClientTransport clientTransport=null;
		if (TransportTypeEnum.NETTY.getCode().equals(clientProperties.getType().toLowerCase())){
			 clientTransport = new NettyClientTransport();
		}else if (TransportTypeEnum.SOCKET.getCode().equals(clientProperties.getType().toLowerCase())){
			 clientTransport = new SocketRpcClient();
		}
		return new RpcClientProxy(clientTransport);
	}
}
