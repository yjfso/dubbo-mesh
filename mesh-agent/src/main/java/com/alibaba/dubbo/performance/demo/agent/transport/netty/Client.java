package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.transport.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.model.Request;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Client {

    private ConnectManager connectManager;

    public Client() throws Exception {
        this.connectManager = new ClientConnectManager(
                new ClientInitializer()
        ).setEndPoints(
                EtcdRegistry.registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService")
        );
    }

    public Object invoke(Request request) throws Exception {
        Endpoint endpoint = connectManager.getEndpoint();

        RpcFuture future = new RpcFuture();
        RequestHolder.put(String.valueOf(request.getId()),future);

        endpoint.request();
        endpoint.getChannel().writeAndFlush(request);
        Object result = null;
        try {
            result = future.get();
            endpoint.response();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
