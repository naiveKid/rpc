package com.rpc.framework.loadbalance;

import java.util.List;

/**
 * @author hxz
 */
public interface LoadBalance {
    /**
     * 在已有服务提供地址列表中选择一个
     *
     * @param serviceAddresses 服务地址列表
     * @return 目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
