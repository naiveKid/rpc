package com.rpc.framework.config.proxy.provider;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * @author hxz
 */
public class ServiceBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	public ServiceBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		super(registry, useDefaultFilters);
	}

	public void registerFilters() {
		addIncludeFilter(new AnnotationTypeFilter(Service.class));
	}

	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		return super.doScan(basePackages);
	}
}
