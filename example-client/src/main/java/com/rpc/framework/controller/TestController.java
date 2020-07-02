package com.rpc.framework.controller;

import com.rpc.framework.HelloService;
import com.rpc.framework.config.proxy.consumer.Reference;
import com.rpc.framework.req.Hello;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class TestController {
	@Reference
	private HelloService helloService;

	@ResponseBody
	@RequestMapping("/hello")
	public String handleRequest(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse) throws Exception {
		String hello = helloService.hello(new Hello("111", "222"));
		log.info("远程调用结果:{}", hello);
		return hello;
	}
}
