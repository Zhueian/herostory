package org.tinygame.herostory.cmdHandler;

import com.sun.tools.javac.util.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/12.
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        Assert.checkNonNull(ctx);
        Assert.checkNonNull(cmd);
        // 可以强转int 但是null无法强转 基础数值类型
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId) {
            return;
        }
        //获取移动用户
        User moveuUser = UserManager.getUserById(userId);
        if (null == moveuUser){
            return;
        }

        //获取移动状态 === start =====
        MoveState mState = moveuUser.moveState;
        mState.fromPosX = cmd.getMoveFromPosX();
        mState.fromPosY = cmd.getMoveFromPosY();
        mState.toPosX = cmd.getMoveToPosX();
        mState.toPosY = cmd.getMoveToPosY();
        mState.startTime = System.nanoTime();

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder =
                GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveToPosX(mState.toPosX);
        resultBuilder.setMoveToPosY(mState.toPosY);
        resultBuilder.setMoveFromPosX(mState.fromPosX);
        resultBuilder.setMoveFromPosY(mState.fromPosY);
        resultBuilder.setMoveStartTime(mState.startTime);
        //获取移动状态 === end =====
        GameMsgProtocol.UserMoveToResult newReult = resultBuilder.build();
        // 广播群发
        Broadcaster.broadcast(newReult);
    }
}
