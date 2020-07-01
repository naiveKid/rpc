package com.rpc.framework.config.proxy.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author hxz
 */
//声明应用在字段上
@Target({ElementType.FIELD})
//运行期生效
@Retention(RetentionPolicy.RUNTIME)
//该注解将被包含在javadoc中
@Documented
@Inherited
@Component
@Autowired
public @interface Reference {
}
