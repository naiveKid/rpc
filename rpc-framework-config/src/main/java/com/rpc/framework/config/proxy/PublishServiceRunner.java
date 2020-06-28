package com.rpc.framework.config.proxy;

import com.rpc.framework.config.proxy.provider.Service;
import com.rpc.framework.enumeration.BeanNameConstant;
import com.rpc.framework.remoting.transport.TransportServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hxz
 */
@Component
@Order(value = 1)
public class PublishServiceRunner implements CommandLineRunner {
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void run(String... arg0) {
		TransportServiceProxy transportServiceProxy = (TransportServiceProxy) applicationContext.getBean(BeanNameConstant.TransportServiceProxy);
		String[] beanNamesForAnnotation = applicationContext.getBeanNamesForAnnotation(Service.class);
		for (String beanName : beanNamesForAnnotation) {
			Class c = applicationContext.getType(beanName);
			Class[] interfaces = c.getInterfaces();
			if (interfaces.length > 0) {
				transportServiceProxy.publishService(applicationContext.getBean(beanName), interfaces[0]);
			}
		}
	}
}
