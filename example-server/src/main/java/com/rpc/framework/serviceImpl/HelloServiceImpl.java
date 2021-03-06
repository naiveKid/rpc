package com.rpc.framework.serviceImpl;

import com.rpc.framework.HelloService;
import com.rpc.framework.config.proxy.provider.Service;
import com.rpc.framework.req.Hello;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hxz
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}