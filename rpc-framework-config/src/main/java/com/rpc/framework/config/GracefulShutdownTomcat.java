package com.rpc.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 优雅关闭 Spring Boot tomcat
 *
 * @author hxz
 */
@Slf4j
@Component
public class GracefulShutdownTomcat implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
	private final int waitTime = 30;
	private volatile Connector connector;

	@Override
	public void customize(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		CustomShutdownHook.getCustomShutdownHook().clearAll();
		this.connector.pause();
		Executor executor = this.connector.getProtocolHandler().getExecutor();
		if (executor instanceof ThreadPoolExecutor) {
			try {
				ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
				threadPoolExecutor.shutdown();
				if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
					log.warn("Tomcat thread pool did not shut down gracefully within " + waitTime + " seconds. Proceeding with forceful shutdown");
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
