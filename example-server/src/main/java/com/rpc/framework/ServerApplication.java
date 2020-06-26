package com.rpc.framework;

import com.rpc.framework.remoting.transport.TransportServer;
import com.rpc.framework.serviceImpl.HelloServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author hxz
 */
@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(ServerApplication.class, args);
		HelloService helloService = new HelloServiceImpl();
		TransportServer transportServer = (TransportServer) cxt.getBean("transportServer");
		transportServer.publishService(helloService, HelloService.class);
	}
}
