package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.sun.tools.javac.util.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.cmdHandler.*;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/4.
 * 接受消息的Handler
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {


    /**
     * 客户端链接连上，加入信道
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    /**
     *     用户离场或者刷新客户端，清除记录
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)throws Exception{
        super.handlerRemoved(ctx);
        Broadcaster.removeChannel(ctx.channel());
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId){
            return;
        }
        UserManager.removeUserByUserId(userId);
        GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
        builder.setQuitUserId(userId);
        GameMsgProtocol.UserQuitResult res = builder.build();
        Broadcaster.broadcast(res);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 防御式编程
        Assert.checkNonNull(ctx,"ctx 为空");
        Assert.checkNonNull(msg,"msg 为空");
        System.out.println("收到客户端消息,myClazz =   " + msg.getClass().getName()+" , msg = "+msg);
        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
        if (null != cmdHandler){
            cmdHandler.handle(ctx,cast(msg));
        }
    }

    /**
     * 范型参数转型小技巧 绕过编译器
     * @param msg
     * @param <TCmd>
     * @return
     */
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg){
        if (null == msg) {
            return null;
        }else{
            return (TCmd) msg;
        }
    }

}
