package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ConsumerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//        consumer.executorService.submit(()->{
            try{
                log.info("consumer got msg");
                if (msg instanceof FullHttpRequest) {
                    FullHttpRequest req = (FullHttpRequest) msg;
                    HttpMethod httpMethod = req.method();
                    if (HttpMethod.POST.equals(httpMethod) ) {
                        boolean keepAlive = HttpUtil.isKeepAlive(req);
                        AgentRequest agentRequest = AgentRequest.getAgentRequest(); //AgentRequest.fromMap(paramters);
                        agentRequest.setByteBufHolder(req);
                        agentRequest.setCtx(ctx);
                        agentRequest.setKeepAlive(keepAlive);
                        AgentClient.INSTANCE.invoke(agentRequest);
//                            agentRequest.done(new DefaultFullHttpResponse(HTTP_1_1, OK));
                    }
                }
            } catch (Exception e){
                log.error("consumer server catch error", e);
            } finally {
//                ReferenceCountUtil.release(msg);
            }
//        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
