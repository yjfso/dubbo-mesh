package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    private Provider provider;

    ProviderHandler(Provider provider){
        this.provider = provider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        byte[] bytes = (byte[]) msg;
        provider.providerExecutor.submit(()->{
            try{
                AgentRequest request = new AgentRequest().fromBytes(bytes);
                Object result = Provider.dubboClient.invoke(request.getInterfaceName(),
                        request.getMethod(), request.getParameterTypesString() ,request.getParameter());

                AgentResponse response = new AgentResponse();
                response.setRequestId(request.getId());
                response.setBytes((byte[]) result);
                ctx.writeAndFlush(response);
            } catch (Exception e){

            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
}
