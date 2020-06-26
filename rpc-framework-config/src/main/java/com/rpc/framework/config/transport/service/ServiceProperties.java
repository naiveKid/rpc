package com.rpc.framework.config.transport.service;

import com.rpc.framework.enumeration.TransportTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

//在application.properties中进行配置，将覆盖下方的默认值
@ConfigurationProperties(prefix = "rpc.service")
public class ServiceProperties {
	private String type = TransportTypeEnum.NETTY.getCode();

	private String ip = "127.0.0.1";

	private int port = 4399;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
