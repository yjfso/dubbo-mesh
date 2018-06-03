package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.channel.Channel;

import java.util.Iterator;

public class ChannelRing implements Iterable<Channel> {

    private final Object lock = new Object();
    private transient Node now;

    private class Node{
        Channel channel;
        Node next;

        Node(Channel channel){
            this.channel = channel;
        }
    }

    void put(Channel channel){
        synchronized(lock){
            Node newNode = new Node(channel);
            if (now == null){
                now = newNode;
                now.next = newNode;
            } else{
                newNode.next = now.next;
                now.next = newNode;
            }
        }
    }

    void remove(Channel channel){
        synchronized(lock){
            if (now == now.next && now.channel == channel){
                now = null;
            } else {
                Node start = now;
                Node check = now;
                while(true){
                    if(check.next.channel == channel){
                        check.next = check.next.next;
                        now = check.next;
                        return;
                    } else {
                        check = check.next;
                        if(start == check){
                            return;
                        }
                    }
                }
            }
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
