package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.channel.Channel;

import java.util.Iterator;

public class ChannelRing implements Iterable<Channel> {

    transient Node now;

    class Node{
        Channel channel;
        Node next;

        Node(Channel channel){
            this.channel = channel;
        }
    }

    void put(Channel channel){
        Node newNode = new Node(channel);
        if (now == null){
            now = newNode;
            now.next = newNode;
        } else{
            newNode.next = now.next;
            now.next = newNode;
        }
    }

    private class ChannelRingIterator implements Iterator<Channel>{

        @Override
        public boolean hasNext() {
            return now != null;
        }

        @Override
        public Channel next() {
            now = now.next;
            return now.channel;
        }
    }
    @Override
    public Iterator<Channel> iterator() {
        return new ChannelRingIterator();
    }
}
