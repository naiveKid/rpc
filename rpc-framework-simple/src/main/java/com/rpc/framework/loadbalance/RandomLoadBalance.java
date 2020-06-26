package com.rpc.framework.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author hxz
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
