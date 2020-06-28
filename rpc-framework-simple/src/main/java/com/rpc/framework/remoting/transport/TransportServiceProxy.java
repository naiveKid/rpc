package com.rpc.framework.remoting.transport;


public interface TransportServiceProxy {
	<T> void publishService(T service, Class<T> serviceClass);
}
