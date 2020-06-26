package com.rpc.framework.registry;

import com.rpc.framework.loadbalance.LoadBalance;
import com.rpc.framework.loadbalance.RandomLoadBalance;
import com.rpc.framework.utils.zk.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于 zookeeper 实现服务发现
 *
 * @author hxz
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this.loadBalance = new RandomLoadBalance();
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        // 这里直接去了第一个找到的服务地址,eg:127.0.0.1:9999
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(serviceName);
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList);
        log.info("成功找到服务地址:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
