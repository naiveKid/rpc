package com.rpc.framework.config.transport.service;

import com.rpc.framework.enumeration.TransportTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

//在application.properties中进行配置，将覆盖下方的默认值
@ConfigurationProperties(prefix = "rpc.service")
public class ServiceProperties {
	private String transportType = TransportTypeEnum.NETTY.getCode();

	private String transportIp = "127.0.0.1";

	private int transportPort = 4399;

	private String zookeeperUrl = "127.0.0.1:2181";

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public String getTransportIp() {
		return transportIp;
	}

	public void setTransportIp(String transportIp) {
		this.transportIp = transportIp;
	}

	public int getTransportPort() {
		return transportPort;
	}

	public void setTransportPort(int transportPort) {
		this.transportPort = transportPort;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}
}
