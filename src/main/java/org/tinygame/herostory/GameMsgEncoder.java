package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;


/**
 * Created by qiucy on 2020/8/11.
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        super.write(ctx, msg, promise);
        if (null == msg || !(msg instanceof GeneratedMessageV3)){
            //走原来逻辑 return完事
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = -1;
        if (msg instanceof GameMsgProtocol.UserEntryResult){
            msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
        }else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult){
            msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
        }else if (msg instanceof GameMsgProtocol.UserMoveToResult){
            msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
        } else if (msg instanceof GameMsgProtocol.UserQuitResult){
            msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
        }else{
            LOGGER.error("无法识别的消息类型，msgClazz=",msg.getClass().getName());
            return;
        }

        byte[] bytes = ((GeneratedMessageV3) msg).toByteArray();
        ByteBuf byteBuf = ctx.alloc().buffer();
        // 格式对齐 进出一致
        byteBuf.writeShort((short)0);
        byteBuf.writeShort((short)msgCode);
        // 开始写消息体
        byteBuf.writeBytes(bytes);

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx,frame,promise);

    }
}
