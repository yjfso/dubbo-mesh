package com.alibaba.dubbo.performance.agent.util;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yinjianfeng on 18/6/15.
 */
public class ByteBufUtil {

    public static String getBufString(ByteBuf byteBuf){
        byteBuf.markReaderIndex();
        int able = byteBuf.readableBytes();
        byte[] bytes = new byte[able];
        byteBuf.readBytes(bytes);
        byteBuf.resetReaderIndex();

        StringBuffer sb = new StringBuffer();
        sb.append(new String(bytes)).append("|");
        for (byte aByte : bytes) {
            sb.append(aByte + "," );
        }
        return sb.toString();
    }
}
