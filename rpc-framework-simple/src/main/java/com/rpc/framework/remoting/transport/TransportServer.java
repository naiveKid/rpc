package com.rpc.framework.remoting.transport;


public interface TransportServer {
	<T> void publishService(T service, Class<T> serviceClass);
}
