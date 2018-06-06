package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
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
            FullHttpResponse response = new DefaultFullHttpResponse( HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[]{1}));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//            FullHttpRequest req = (FullHttpRequest) msg;
//            req.release();
//            if (msg instanceof FullHttpRequest) {
//                FullHttpRequest req = (FullHttpRequest) msg;
//
//                HttpMethod httpMethod = req.method();
//                if (HttpMethod.POST.equals(httpMethod) ) {
//                    try{
//                        boolean keepAlive = HttpUtil.isKeepAlive(req);
//                        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
//                        decoder.offer(req);
//                        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
//                        Map<String, String> paramters = new HashMap<>();
//                        for (InterfaceHttpData parm : parmList) {
//                            Attribute data = (Attribute) parm;
//                            paramters.put(data.getName(), data.getValue());
//                        }
//
//                        String interfaceName = paramters.get("interface");
//                        String method = paramters.get("method");
//                        String parameterTypesString = paramters.get("parameterTypesString");
//                        String parameter = paramters.get("parameter");
//                        AgentRequest agentRequest = (new AgentRequest().initRequest())
//                                .initData(interfaceName, method, parameterTypesString, parameter);
//
//                        ByteBuf byteBuf = null;
//                        if (agentRequest.isValid()) {
//                            byte[] bytes = (byte[]) AgentClient.INSTANCE.invoke(agentRequest);
//                            byteBuf = Unpooled.wrappedBuffer(bytes, 8, bytes.length-8);
//                        } else {
//                            byteBuf = ERROR_BUF;
//                        }
//
//                        FullHttpResponse response = new DefaultFullHttpResponse( HTTP_1_1, OK, byteBuf);
//                        response.headers().set(CONTENT_TYPE, "text/plain");
//                        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
//
//                        if (!keepAlive) {
//                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//                        } else {
//                            response.headers().set(CONNECTION, CONNECTION);
//                            ctx.writeAndFlush(response);
//                        }
//                    }
//                    catch (Exception e){
//                        log.error("consumer server catch error", e);
//                    }
//                }
//            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
