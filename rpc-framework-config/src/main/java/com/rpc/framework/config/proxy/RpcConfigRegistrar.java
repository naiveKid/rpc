package com.rpc.framework.config.proxy;

import com.rpc.framework.config.proxy.provider.ReferenceBeanDefinitionScanner;
import com.rpc.framework.config.proxy.provider.Service;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author hxz
 */
public class RpcConfigRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		ReferenceBeanDefinitionScanner scanner = new ReferenceBeanDefinitionScanner(registry, false);
		scanner.setResourceLoader(resourceLoader);
		scanner.registerFilters();
		scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
		scanner.doScan("com.rpc");
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
