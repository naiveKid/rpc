package com.rpc.framework.config.proxy;

import com.rpc.framework.config.proxy.provider.Service;
import com.rpc.framework.config.proxy.provider.ServiceBeanDefinitionScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxz
 */
public class RpcConfigRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
										BeanNameGenerator importBeanNameGenerator) {
		AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRpcScan.class.getName()));
		if (mapperScanAttrs != null) {
			this.registerBeanDefinitions(mapperScanAttrs, registry);
		}
	}

	private void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry) {
		ServiceBeanDefinitionScanner scanner = new ServiceBeanDefinitionScanner(registry, false);

		Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
		if (!BeanNameGenerator.class.equals(generatorClass)) {
			scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
		}
		scanner.setResourceLoader(resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

		List<String> basePackages = new ArrayList<>();
		basePackages.addAll((Collection) Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
		basePackages.addAll((Collection) Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
		basePackages.addAll((Collection) Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
		scanner.registerFilters();
		scanner.doScan(StringUtils.toStringArray(basePackages));
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
