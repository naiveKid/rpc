package com.rpc.framework.config.proxy;

import com.rpc.framework.config.proxy.consumer.ReferenceAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author hxz
 */
public class RpcReferenceConfigRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
										BeanNameGenerator importBeanNameGenerator) {
		Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
		registerReferenceAnnotationBeanPostProcessor(registry, packagesToScan);
	}

	private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(EnableRpcScan.class.getName()));
		String[] basePackages = attributes.getStringArray("value");
		Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
		String[] value = attributes.getStringArray("basePackages");
		// Appends value array attributes
		Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
		packagesToScan.addAll(Arrays.asList(basePackages));
		for (Class<?> basePackageClass : basePackageClasses) {
			packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
		}
		if (packagesToScan.isEmpty()) {
			return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
		}
		return packagesToScan;
	}

	private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry, Set<String> packagesToScan) {
		// Register @Reference Annotation Bean Processor
		ReferenceAnnotationBeanPostProcessor.basePackages = packagesToScan;
		registerInfrastructureBean(registry, "referenceAnnotationBeanPostProcessor",
				ReferenceAnnotationBeanPostProcessor.class);
	}

	private void registerInfrastructureBean(BeanDefinitionRegistry beanDefinitionRegistry, String beanName, Class<?> beanType) {
		if (!beanDefinitionRegistry.containsBeanDefinition(beanName)) {
			RootBeanDefinition beanDefinition = new RootBeanDefinition(beanType);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
		}
	}
}
