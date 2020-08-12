package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Created by qiucy on 2020/8/11.
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame)){
            return;
        }
        //websocket 二进制消息会通过 httpServerCodec 解码成binaryWebSocketFrame对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;
        ByteBuf byteBuf = frame.content();

        //读取消息长度 读过前两个short整数
        short i = byteBuf.readShort();
        //读取消息编号 再读过两个short整数
        short msgCode = byteBuf.readShort();

        // 拿到真实的字节数组打印 消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        GeneratedMessageV3 cmd = null;


        switch (msgCode){
            //入场
            case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                //实际是个protocolBuf反序列化
                cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                break;
            //还有谁在场
            case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                break;
            default:;;

        }

        if (null != cmd){
            ctx.fireChannelRead(cmd);
        }
    }
}
