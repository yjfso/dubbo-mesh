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
package com.alibaba.dubbo.performance.agent.model.dubbo;

import java.io.Serializable;


public class RpcInvocation implements Serializable {

    private static final long serialVersionUID = -4355285085441097045L;

    private byte[] methodName;

    private byte[] parameterTypes;

    private byte[] arguments;

    private byte[] interfaceName;


    public RpcInvocation() {
    }

    public byte[] getMethodName() {
        return methodName;
    }

    public void setMethodName(byte[] methodName) {
        this.methodName = methodName;
    }

    public byte[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(byte[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public byte[] getArguments() {
        return arguments;
    }

    public void setArguments(byte[] arguments) {
        this.arguments = arguments;
    }


    public byte[] getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(byte[] interfaceName) {
        this.interfaceName = interfaceName;
    }
}