package com.rpc.framework.remoting.transport.socket;

import com.rpc.framework.remoting.dto.RpcRequest;
import com.rpc.framework.remoting.dto.RpcResponse;
import com.rpc.framework.handler.RpcRequestHandler;
import com.rpc.framework.factory.SingletonFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author hxz
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {
    private Socket socket;
    private RpcRequestHandler rpcRequestHandler;


    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }

}
