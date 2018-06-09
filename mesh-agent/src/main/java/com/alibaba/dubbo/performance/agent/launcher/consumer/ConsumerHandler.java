package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.agent.transport.netty.http.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ConsumerHandler.class);

    private Consumer consumer;

    ConsumerHandler(Consumer consumer){
        this.consumer = consumer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        consumer.executorService.submit(()->{
            try{
                if (msg instanceof FullHttpRequest) {
                    FullHttpRequest req = (FullHttpRequest) msg;
                    HttpMethod httpMethod = req.method();
                    if (HttpMethod.POST.equals(httpMethod) ) {
                        boolean keepAlive = HttpUtil.isKeepAlive(req);
                        Map<String, String> paramters = HttpUtils.mapPostData(req);
                        AgentRequest agentRequest = AgentRequest.fromMap(paramters);
                        agentRequest.setCtx(ctx);
                        agentRequest.setKeepAlive(keepAlive);
                        if (!(agentRequest.isValid() && AgentClient.INSTANCE.invoke(agentRequest))) {
                            agentRequest.done(new DefaultFullHttpResponse(HTTP_1_1, OK));
                        }
                    }
                }
            } catch (Exception e){
                log.error("consumer server catch error", e);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
