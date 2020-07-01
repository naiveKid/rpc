package com.rpc.framework;

import com.rpc.framework.req.Hello;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestConfig {
	@Autowired
	private HelloService helloService;

	@Autowired
	private HelloService1 helloService1;

	public void test() {
		String hello = helloService.hello(new Hello("111", "222"));
		log.info("远程调用结果:{}", hello);
		String hello1 = helloService1.hello(new Hello("333", "444"));
		log.info("远程调用结果:{}", hello1);
	}
}
