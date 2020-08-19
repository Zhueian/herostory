package org.tinygame.herostory.cmdHandler;

import com.sun.tools.javac.util.Assert;
import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/12.
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        Assert.checkNonNull(ctx);
        Assert.checkNonNull(cmd);
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder =
                GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for(User currUser : UserManager.listUser()){
            if (null == currUser){
                continue;
            }
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.userId);
            userInfoBuilder.setHeroAvatar(currUser.heroAvator);
            // 获取移动状态
            MoveState mvState = currUser.moveState;
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(mvState.fromPosX);
            mvStateBuilder.setFromPosY(mvState.fromPosY);
            mvStateBuilder.setToPosX(mvState.toPosX);
            mvStateBuilder.setToPosX(mvState.toPosX);
            mvStateBuilder.setStartTime(mvState.startTime);
            // 将移动状态设置到用户信息里
            userInfoBuilder.setMoveState(mvStateBuilder);
            //将用户信息添加到结果消息
            resultBuilder.addUserInfo(userInfoBuilder);
        }
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        //谁用给谁发,不用用_channelGroup这个全局广播发了
        ctx.writeAndFlush(newResult);
    }
}
