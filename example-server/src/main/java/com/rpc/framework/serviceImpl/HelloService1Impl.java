package com.rpc.framework.serviceImpl;

import com.rpc.framework.HelloService1;
import com.rpc.framework.config.proxy.provider.Service;
import com.rpc.framework.req.Hello;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hxz
 */
@Slf4j
@Service
public class HelloService1Impl implements HelloService1 {

	@Override
	public String hello(Hello hello) {
		log.info("HelloService1Impl收到: {}.", hello.getMessage());
		String result = "Hello description is " + hello.getDescription();
		log.info("HelloService1Impl返回: {}.", result);
		return result;
	}
}