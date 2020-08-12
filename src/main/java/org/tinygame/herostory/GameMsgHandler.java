package org.tinygame.herostory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiucy on 2020/8/4.
 * 接受消息的Handler
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 客户端信道数组，一定要static，否则无法实现群发广播。
     *  原因：每次有客户端连进来都会new GameMsgHandler 要共用一个channelGroup
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户字典
     */
    static private final Map<Integer,User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息  "+msg);

        //入场
        if (msg instanceof GameMsgProtocol.UserEntryCmd){
            //从指令对象中获取用户id和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
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
            _userMap.put(user.userId,user);

            //将用户id附着到 channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
            //构建结果并且群发（广播）
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);

        }
        //还有谁在场
        else if(msg instanceof GameMsgProtocol.WhoElseIsHereCmd){
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            for(User currUser : _userMap.values()){
                if (null == currUser){
                    continue;
                }
                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
                        GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvator);
                resultBuilder.addUserInfo(userInfoBuilder);
            }
            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            //谁用给谁发,不用用_channelGroup这个全局广播发了
            ctx.writeAndFlush(newResult);
        }
        // 相互看到人物移动
        else if(msg instanceof GameMsgProtocol.UserMoveToCmd){
            // 可以强转int 但是null无法强转 基础数值类型
           Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (null == userId){
                return;
            }
            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;
            GameMsgProtocol.UserMoveToResult.Builder resultBuilder =
                    GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
            GameMsgProtocol.UserMoveToResult newReult = resultBuilder.build();
            // 广播群发
            _channelGroup.writeAndFlush(newReult);
        }
//
//        //websocket 二进制消息会通过 httpServerCodec 解码成binaryWebSocketFrame对象
//        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;
//        ByteBuf byteBuf = frame.content();
//
//        // 拿到真实的字节数组打印
//        byte[] bytes = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(bytes);
//        System.out.print("收到的字节 = ");
//
//        for (byte b:bytes){
//            System.out.print(b);
//            System.out.print(", ");
//        }
//        System.out.println();

    }
}
