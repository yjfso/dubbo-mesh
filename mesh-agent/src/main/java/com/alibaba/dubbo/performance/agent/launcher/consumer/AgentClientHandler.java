package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.util.ByteBufUtil;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.collection.IntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class AgentClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(AgentClientHandler.class);

    public final static AgentClientHandler INSTANCE = new AgentClientHandler();

    private AgentClientHandler(){

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try{
            ByteBuf byteBuf = (ByteBuf) msg;

            int id = byteBuf.readInt();
            AgentRequest agentRequest = AgentRequest.getPool().get(id);//[id];
            if(null != agentRequest){
                if(agentRequest.isAvailable()){
                    log.error("client got a response without source Request");
                } else {
                    agentRequest.done(byteBuf);
                }
            }
        } catch (Exception e){
            log.error("consumer client response error", e);
        }
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
