package com.rpc.framework.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum TransportTypeEnum {
	NETTY("netty", "netty作为传输实现"),
	SOCKET("socket", "socket调用作为传输实现");

	private final String code;
	private final String message;
}
