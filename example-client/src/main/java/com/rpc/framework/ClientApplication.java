package com.rpc.framework;

import com.rpc.framework.config.proxy.EnableRpcScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hxz
 */
@Slf4j
@EnableRpcScan(value = "com.rpc")
@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
}
