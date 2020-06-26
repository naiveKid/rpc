package com.rpc.framework.remoting.transport.netty.client;

import com.rpc.framework.factory.SingletonFactory;
import com.rpc.framework.registry.ServiceDiscovery;
import com.rpc.framework.registry.ZkServiceDiscovery;
import com.rpc.framework.remoting.dto.RpcRequest;
import com.rpc.framework.remoting.dto.RpcResponse;
import com.rpc.framework.remoting.transport.ClientTransport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * 基于 Netty 传输 RpcRequest。
 *
 * @author hxz
 */
@Slf4j
public class NettyClientTransport implements ClientTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientTransport() {
        this.serviceDiscovery = new ZkServiceDiscovery();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress);
        if (channel != null && channel.isActive()) {
            // 放入未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

}


