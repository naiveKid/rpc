package com.rpc.framework;

import com.rpc.framework.config.proxy.EnableRpcScan;
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
public class ServerApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(ServerApplication.class, args);
		TestConfig testConfig = (TestConfig) cxt.getBean("testConfig");
		testConfig.test();
	}
}