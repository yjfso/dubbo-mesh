#!/bin/bash

ETCD_HOST=127.0.0.1
ETCD_PORT=2379
ETCD_URL=http://$ETCD_HOST:$ETCD_PORT

echo ETCD_URL = $ETCD_URL

if [[ "$1" == "consumer" ]]; then
  echo "Starting consumer agent..."
  java -jar \
       -Xms1536M \
       -Xmx1536M \
       -XX:+PrintGCDetails \
       -Dio.netty.leakDetectionLevel=paranoid \
       -Dtype=consumer \
       -Dserver.port=20000 \
       -Detcd.url=http://127.0.0.1:2379 \
       /Users/yinjianfeng/Documents/application/develop/agent-demo/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-small" ]]; then
  echo "Starting small provider agent..."
  java -jar \
       -Xms512M \
       -Xmx512M \
       -Dtype=provider \
       -XX:+PrintGCDetails \
       -Dio.netty.leakDetectionLevel=paranoid \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dserver.weight=1 \
       -Detcd.url=http://127.0.0.1:2379 \
       /Users/yinjianfeng/Documents/application/develop/agent-demo/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-medium" ]]; then
  echo "Starting medium provider agent..."
  java -jar \
       -Xms1536M \
       -Xmx1536M \
       -XX:+PrintGCDetails \
       -Dio.netty.leakDetectionLevel=paranoid \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dserver.weight=2 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=/root/logs \
       /Users/yinjianfeng/Documents/application/develop/agent-demo/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar
elif [[ "$1" == "provider-large" ]]; then
  echo "Starting large provider agent..."
  java -jar \
       -Xms2560M \
       -Xmx2560M \
       -XX:+PrintGCDetails \
       -Dio.netty.leakDetectionLevel=paranoid \
       -Dtype=provider \
       -Ddubbo.protocol.port=20880 \
       -Dserver.port=30000 \
       -Dserver.weight=3 \
       -Detcd.url=$ETCD_URL \
       -Dlogs.dir=/root/logs \
       /Users/yinjianfeng/Documents/application/develop/agent-demo/mesh-agent/target/mesh-agent-1.0-SNAPSHOT.jar
else
  echo "Unrecognized arguments, exit."
  exit 1
fi
