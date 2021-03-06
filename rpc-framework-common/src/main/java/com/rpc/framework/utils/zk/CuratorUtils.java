package com.rpc.framework.utils.zk;

import com.rpc.framework.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxz
 */
@Slf4j
public final class CuratorUtils {
	private static final int BASE_SLEEP_TIME = 1000;
	private static final int MAX_RETRIES = 3;
	private static String CONNECT_STRING;
	public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
	private static Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
	private static Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();

	public static void setConnectString(String connectString) {
		CONNECT_STRING = connectString;
	}

	private CuratorUtils() {
	}

	private static CuratorFramework getInstance() {
		return Holder.zkClient;
	}

	//静态内部类实现创建单例对象
	private static class Holder {
		private static CuratorFramework zkClient = getZkClient();
	}

	private static CuratorFramework getZkClient() {
		// 重试策略。重试3次，并且会增加重试之间的睡眠时间。
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
		CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
				//要连接的服务器(可以是服务器列表)
				.connectString(CONNECT_STRING)
				.retryPolicy(retryPolicy)
				.build();
		log.info("连接zookeeper中，{}...",CONNECT_STRING);
		curatorFramework.start();
		return curatorFramework;
	}

	/**
	 * 创建临时节点. 若为持久化节点,则不会因为客户端断开连接而被删除
	 *
	 * @param path 节点路径
	 */
	public static void createEphemeralNode(String path) {
		try {
			if (registeredPathSet.contains(path) || getInstance().checkExists().forPath(path) != null) {
				log.info("节点已经存在，节点为:[{}]", path);
			} else {
				//eg: /my-rpc/github.javaguide.HelloService/127.0.0.1:9999
				getInstance().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
				log.info("节点创建成功，节点为:[{}]", path);
			}
			registeredPathSet.add(path);
		} catch (Exception e) {
			throw new RpcException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 获取某个字节下的子节点,也就是获取所有提供服务的生产者的地址
	 *
	 * @param serviceName 服务对象接口名 eg:github.javaguide.HelloService
	 * @return 指定字节下的所有子节点
	 */
	public static List<String> getChildrenNodes(String serviceName) {
		if (serviceAddressMap.containsKey(serviceName)) {
			return serviceAddressMap.get(serviceName);
		}
		List<String> result;
		String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
		try {
			result = getInstance().getChildren().forPath(servicePath);
			serviceAddressMap.put(serviceName, result);
			registerWatcher(getInstance(), serviceName);
		} catch (Exception e) {
			throw new RpcException(e.getMessage(), e.getCause());
		}
		return result;
	}

	/**
	 * 清空注册中心的数据
	 */
	public static void clearRegistry() {
		registeredPathSet.stream().parallel().forEach(p -> {
			try {
				getInstance().delete().forPath(p);
			} catch (Exception e) {
				throw new RpcException(e.getMessage(), e.getCause());
			}
		});
		log.info("服务端（Provider）所有注册的服务都被清空:[{}]", registeredPathSet.toString());
	}

	/**
	 * 注册监听指定节点。
	 *
	 * @param serviceName 服务对象接口名 eg:github.javaguide.HelloService
	 */
	private static void registerWatcher(CuratorFramework zkClient, String serviceName) {
		String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
		PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
		PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
			List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
			serviceAddressMap.put(serviceName, serviceAddresses);
		};
		pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
		try {
			pathChildrenCache.start();
		} catch (Exception e) {
			throw new RpcException(e.getMessage(), e.getCause());
		}
	}
}
