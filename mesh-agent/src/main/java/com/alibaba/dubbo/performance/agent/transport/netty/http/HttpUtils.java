package com.alibaba.dubbo.performance.agent.transport.netty.http;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * Created by yinjianfeng on 18/6/6.
 */
public class HttpUtils {

    public static Map<String, String> mapPostData(FullHttpRequest req) throws IOException{
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
        decoder.offer(req);
        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
        Map<String, String> paramters = new HashMap<>();
        for (InterfaceHttpData parm : parmList) {
            Attribute data = (Attribute) parm;
            paramters.put(data.getName(), data.getValue());
        }
        return paramters;
    }

    public static void response(ChannelHandlerContext ctx, boolean keepAlive,  FullHttpResponse response){
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        if(keepAlive){
            response.headers().set(CONNECTION, CONNECTION);
            ctx.writeAndFlush(response);
        } else{
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
