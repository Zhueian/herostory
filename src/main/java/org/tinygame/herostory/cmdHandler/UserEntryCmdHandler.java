package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/12.
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        //从指令对象中获取用户id和英雄形象
        GameMsgProtocol.UserEntryCmd cmd = msg;
        int userId = cmd.getUserId();
        String herpAvator = cmd.getHeroAvatar();
        GameMsgProtocol.UserEntryResult.Builder resultBuilder =
                GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(herpAvator);

        //加入用户字典
        User user = new User();
        user.userId = userId;
        user.heroAvator = herpAvator;
        UserManager.addUser(user);

        //将用户id附着到 channel
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
        //构建结果并且群发（广播）
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
