/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.performance.demo.agent.transport.model;


/**
 * CodecUtils.
 */

public class Bytes {

    private Bytes() {
    }

    public static String[] splitByteToStringsByLength(byte[] bytes, int stringNum){
        return splitByteToStringsByLength(bytes, stringNum, 0);
    }
    public static String[] splitByteToStringsByLength(byte[] bytes, int stringNum, int pos){
        String[] strings = new String[stringNum];
        for (int i = 0; i < stringNum; i++){
            int length = bytes2int(bytes, pos);
            pos += 4;
            strings[i] = new String(bytes, pos, length);
            pos += length;
        }
        return strings;
    }

    public static byte[] mergeByteWithLength(byte[]... bytes){
        int length = 0;
        for (byte[] bytes1: bytes){
            length += (4 + bytes1.length);
        }
        byte[] result = new byte[length];
        int destPos = 0;
        for (byte[] bytes1: bytes){
            length = bytes1.length;
            System.arraycopy(int2bytes(length), 0, result, destPos, 4);
            destPos += 4;
            System.arraycopy(bytes1, 0, result, destPos, length );
            destPos += length;
        }
        return result;
    }

    public static int bytes2int(byte[] b, int off)
    {
        return ((b[off + 3] & 0xFF) << 0) +
                ((b[off + 2] & 0xFF) << 8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off + 0]) << 24);
    }

    public static byte[] int2bytes(int v) {
        byte[] b = new byte[4];
        b[3] = (byte) v;
        b[2] = (byte) (v >>> 8);
        b[1] = (byte) (v >>> 16);
        b[0] = (byte) (v >>> 24);
        return b;
    }

    public static byte[] long2bytes(long v) {
        byte[] b = new byte[8];
        b[7] = (byte) v;
        b[6] = (byte) (v >>> 8);
        b[5] = (byte) (v >>> 16);
        b[4] = (byte) (v >>> 24);
        b[3] = (byte) (v >>> 32);
        b[2] = (byte) (v >>> 40);
        b[1] = (byte) (v >>> 48);
        b[0] = (byte) (v >>> 56);
        return b;
    }

    /**
     * to long.
     *
     * @param b   byte array.
     * @return long.
     */
    public static long bytes2long(byte[] b) {
        return ((b[7] & 0xFFL) << 0) +
                ((b[6] & 0xFFL) << 8) +
                ((b[5] & 0xFFL) << 16) +
                ((b[4] & 0xFFL) << 24) +
                ((b[3] & 0xFFL) << 32) +
                ((b[2] & 0xFFL) << 40) +
                ((b[1] & 0xFFL) << 48) +
                (((long) b[0]) << 56);
    }

    
}