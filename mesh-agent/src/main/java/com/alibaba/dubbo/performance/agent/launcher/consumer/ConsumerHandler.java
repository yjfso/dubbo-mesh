package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Created by yinjianfeng on 18/5/27.
 */
@ChannelHandler.Sharable
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ConsumerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Endpoint endpoint = AgentClient.INSTANCE.getConnectManager().getEndpoint();
        if (endpoint==null){
            log.error("lack endpoint");
        } else {
            log.info("route to " + endpoint);
            ChannelFuture channelFuture = endpoint.getChannelFuture(ctx);
            AgentRequest agentRequest = AgentRequest.getAgentRequest();
            agentRequest.setEndpoint(endpoint);

            Consumer.executorService.submit(
                    ()->{
                        try {
                            if (msg instanceof FullHttpRequest) {
                                FullHttpRequest req = (FullHttpRequest) msg;
                                HttpMethod httpMethod = req.method();
                                if (HttpMethod.POST.equals(httpMethod) ) {
                                    boolean keepAlive = HttpUtil.isKeepAlive(req);
                                    agentRequest.setByteBufHolder(req);
                                    agentRequest.setCtx(ctx);
                                    agentRequest.setKeepAlive(keepAlive);
                                    AgentClient.INSTANCE.invoke(agentRequest, channelFuture);
                                    return;
                                }
                            }
                        } catch (Exception e){
                            log.error("consumer message handle error", e);
                        }
                        try{
                            agentRequest.done(new DefaultFullHttpResponse(HTTP_1_1, OK));
                        } catch (Exception e){
                            log.error("agent done error");
                        }
                    }
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        agentClient.getConnectManager().removeChannel(ctx.channel());
    }

}
