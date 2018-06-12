package com.alibaba.dubbo.performance.agent.model.dubbo;

import com.alibaba.dubbo.performance.agent.common.Const;
import com.google.common.base.Ascii;
import io.netty.util.AsciiString;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinjianfeng on 18/6/12.
 */
public class HTTPDecoder {

    public static Map<String, byte[]> decode(byte[] bytes, int start){
        Map<String, byte[]> result = new HashMap<>();
        int length = bytes.length;
        int startPos = start;
        String nowKey = null;
        for (int i = start; i < length; i++) {
            if (bytes[i] == 38){
                if (nowKey!=null){
                    byte[] value = decodeValue(nowKey, bytes, startPos, i-startPos);
                    result.put(nowKey, value);
                    nowKey = null;
                    startPos = i + 1;
                }
            } else if(bytes[i] == 61) {
                nowKey = new String(bytes, startPos, i-startPos);
                startPos = i + 1;
            }
        }
        if (nowKey!=null){
            byte[] value = decodeValue(nowKey, bytes, startPos, length-startPos);
            result.put(nowKey, value);
        }
        return result;
    }

    private static byte[] decodeValue(String key, byte[] source, int start, int length){
        byte[] result;
        switch (key){
            case "parameterTypesString":
                result = decodeUrlCodeValue(source, start, length);
                break;
            default:
                result = new byte[length];
                System.arraycopy(source, start, result, 0, length);
                break;
        }
        return result;
    }

    private static byte[] decodeUrlCodeValue(byte[] source, int start, int length){
        int index = 0;
        int i = start;
        int end = i + length;
        byte[] all = new byte[length];
        while (i<end){
            if (source[i] == Const.PERCENT){
                all[index] = (byte)Integer.parseInt(new String(source, i+1, 2), 16);
                i+=2;
            } else {
                all[index] = source[i];
            }
            i++;
            index++;
        }
        if (i != index){
            byte[] result = new byte[index];
            System.arraycopy(all, 0, result, 0, index);
            return result;
        }
        return source;
    }
}
