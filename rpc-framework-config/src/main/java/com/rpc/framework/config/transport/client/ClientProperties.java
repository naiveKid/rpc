package com.rpc.framework.config.transport.client;

import com.rpc.framework.enumeration.TransportTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

//在application.properties中进行配置，将覆盖下方的默认值
@ConfigurationProperties(prefix = "rpc.client")
public class ClientProperties {
	private String transportType = TransportTypeEnum.NETTY.getCode();

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
}
