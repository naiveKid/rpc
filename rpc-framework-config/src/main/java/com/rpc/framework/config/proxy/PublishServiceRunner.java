package com.rpc.framework.config.proxy;

import com.rpc.framework.config.proxy.provider.Service;
import com.rpc.framework.enumeration.BeanNameConstant;
import com.rpc.framework.remoting.transport.TransportServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class PublishServiceRunner implements ApplicationRunner {
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void run(ApplicationArguments args) throws Exception {
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
