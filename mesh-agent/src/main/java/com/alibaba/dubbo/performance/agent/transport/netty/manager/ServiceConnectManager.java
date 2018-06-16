package com.alibaba.dubbo.performance.agent.transport.netty.manager;


import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class ServiceConnectManager extends AbstractConnectManager {

    private final static Logger log = LoggerFactory.getLogger(ServiceConnectManager.class);
    private ChannelHandler handler;

    public ServiceConnectManager(ChannelHandler handler) throws Exception{
        this.handler = handler;
        this.start();
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = Const.EVENT_LOOP_GROUP.newInstance();
        EventLoopGroup workerGroup = Const.EVENT_LOOP_GROUP.newInstance();
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(Const.SERVER_SOCKET_CHANNEL)
                    .childHandler(handler)
//                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .childOption(ChannelOption.TCP_NODELAY, false)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("start service error",e );
        }
         finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
