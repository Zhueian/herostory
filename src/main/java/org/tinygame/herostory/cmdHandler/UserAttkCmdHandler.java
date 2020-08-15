package org.tinygame.herostory.cmdHandler;

import com.sun.tools.javac.util.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/15.
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd userAttkCmd) {
        // 防御式编程
        Assert.checkNonNull(ctx,"ctx 为空");
        Assert.checkNonNull(userAttkCmd,"userAttkCmd 为空");
        //获取攻击者userId
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId){
            return;
        }
        //获取被攻击者userId
        int targetUserId = userAttkCmd.getTargetUserId();
        //构建打与被打消息
        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        //构建调血消息、
        GameMsgProtocol.UserSubtractHpResult.Builder userSubtractHpBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        userSubtractHpBuilder.setTargetUserId(targetUserId);
        //TODO 这个HP 拓展可以根据武器属性，暴击随机数的拓展
        userSubtractHpBuilder.setSubtractHp(10);
        Broadcaster.broadcast(userSubtractHpBuilder.build());
    }
}
