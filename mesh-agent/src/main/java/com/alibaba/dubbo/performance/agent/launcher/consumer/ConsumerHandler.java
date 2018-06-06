package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import com.alibaba.dubbo.performance.agent.transport.netty.http.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ConsumerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ConsumerHandler.class);
    private final static ByteBuf ERROR_BUF = Unpooled.wrappedBuffer(new byte[]{1, 2, 8,32,42,2});

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

                        Map<String, String> paramters = HttpUtils.mapPostData(req);
                        AgentRequest agentRequest = AgentRequest.fromMap(paramters);

                        FullHttpResponse rep;
                        ByteBuf byteBuf = null;
                        if (agentRequest.isValid()) {
                            byte[] bytes = (byte[]) AgentClient.INSTANCE.invoke(agentRequest);
                            byteBuf = Unpooled.wrappedBuffer(bytes, 8, bytes.length-8);
                            rep = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
                        } else {
                            rep = new DefaultFullHttpResponse(HTTP_1_1, OK);
                        }
                        HttpUtils.response(ctx, req, rep);

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
