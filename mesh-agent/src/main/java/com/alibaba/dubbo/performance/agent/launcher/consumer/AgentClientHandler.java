package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(AgentClientHandler.class);

    private AgentClient agentClient;
    public AgentClientHandler(AgentClient agentClient){
        this.agentClient = agentClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//        Consumer.INSTANCE.executorService.submit(
//                ()->{
                    try{
                        byte[] bytes = (byte[]) msg;
                        int id = Bytes.bytes2int(bytes, 0);//agentResponse.getRequestId();
                        AgentRequest agentRequest = AgentRequest.requests[id];
                        if(null != agentRequest){
                            agentRequest.done(bytes);
                        }
                    } catch (Exception e){
                        log.error("consumer client response error", e);
                    }
//                }
//        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        agentClient.getConnectManager().removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
