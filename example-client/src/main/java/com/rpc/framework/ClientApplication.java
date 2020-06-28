package com.rpc.framework;

import com.rpc.framework.config.proxy.EnableRpcScan;
import com.rpc.framework.enumeration.BeanNameConstant;
import com.rpc.framework.proxy.RpcClientProxy;
import com.rpc.framework.req.Hello;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author hxz
 */
@Slf4j
@EnableRpcScan(value = "com.rpc")
@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(ClientApplication.class, args);
		RpcClientProxy rpcClientProxy = (RpcClientProxy) cxt.getBean(BeanNameConstant.RpcClientProxy);
		HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
		String hello = helloService.hello(new Hello("111", "222"));
		log.info("远程调用结果:{}",hello);

		HelloService1 helloService1 = rpcClientProxy.getProxy(HelloService1.class);
		String hello1 = helloService1.hello(new Hello("1111", "2222"));
		log.info("远程调用结果:{}", hello1);
	}
}
