package com.rpc.framework.config.proxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author hxz
 */
//声明应用在接口、类、枚举上
@Target({ElementType.TYPE})
//运行期生效
@Retention(RetentionPolicy.RUNTIME)
//该注解将被包含在javadoc中
@Documented
@Configuration
@Import(RpcConfigRegistrar.class)
public @interface EnableRpc {
}
