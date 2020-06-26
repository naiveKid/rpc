package com.rpc.framework.config.transport.client;

import com.rpc.framework.enumeration.TransportTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

//在application.properties中进行配置，将覆盖下方的默认值
@ConfigurationProperties(prefix = "rpc.client")
public class ClientProperties {
	private String type = TransportTypeEnum.NETTY.getCode();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
