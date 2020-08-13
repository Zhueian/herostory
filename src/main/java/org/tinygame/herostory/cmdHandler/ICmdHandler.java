package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by qiucy on 2020/8/12.
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理指令
     * @param ctx
     * @param cmd
     * 方法不直接用 GeneratedMessageV3 做图参，因为不严谨，到具体方法里还得转型
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
