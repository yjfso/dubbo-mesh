package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelUtil {

    public static void writeAndFlush(ChannelFuture channelFuture, Object object){
        if(channelFuture.isDone()){
            channelFuture.channel().writeAndFlush(object);
        } else{
            channelFuture.addListener(
                    new ChannelFutureListener(){
                        @Override
                        public void operationComplete(ChannelFuture cf) throws Exception {
                            cf.channel().writeAndFlush(object);
                            channelFuture.removeListener(this);
                        }
                    }
            );
        }
    }
}