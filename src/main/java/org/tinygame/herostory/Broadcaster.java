package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by qiucy on 2020/8/12.
 * 广播源
 */
public final class Broadcaster {
    private Broadcaster(){}
    /**
     * 客户端信道数组，一定要static，否则无法实现群发广播。
     *  原因：每次有客户端连进来都会new GameMsgHandler 要共用一个channelGroup
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 添加信道
     * @param channel
     */
    static public void addChannel(Channel channel){
        _channelGroup.add(channel);
    }

    /**
     * 移除信道
     * @param channel
     */
    static public void removeChannel(Channel channel){
        _channelGroup.remove(channel);
    }

    /**
     * 广播消息
     * @param msg
     */
    static public void broadcast(Object msg){
        if (null == msg) return;
        _channelGroup.writeAndFlush(msg);
    }
}
