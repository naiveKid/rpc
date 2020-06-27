package com.rpc.framework;

import com.rpc.framework.config.proxy.EnableRpc;
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
@EnableRpc
@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(ClientApplication.class, args);
		RpcClientProxy rpcClientProxy = (RpcClientProxy) cxt.getBean("rpcClientProxy");
		HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
		String hello = helloService.hello(new Hello("111", "222"));
		log.info("远程调用结果:{}",hello);
	}
}
