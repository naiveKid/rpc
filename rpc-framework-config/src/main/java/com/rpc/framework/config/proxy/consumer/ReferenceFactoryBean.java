package com.rpc.framework.config.proxy.consumer;

import com.rpc.framework.proxy.RpcClientProxy;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author hxz
 */
public class ReferenceFactoryBean<T> implements FactoryBean<T> {

	private Class<T> interfaceType;

	private RpcClientProxy rpcClientProxy;

	public void setInterfaceType(Class<T> interfaceType) {
		this.interfaceType = interfaceType;
	}

	public void setRpcClientProxy(RpcClientProxy rpcClientProxy) {
		this.rpcClientProxy = rpcClientProxy;
	}

	@Override
	public T getObject() throws Exception {
		return rpcClientProxy.getProxy(interfaceType);
	}

	@Override
	public Class<T> getObjectType() {
		return interfaceType;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
