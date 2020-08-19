package org.tinygame.herostory;

import com.google.protobuf.Message;
import com.sun.tools.javac.util.Assert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by qiucy on 2020/8/11.
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter{

    static private Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 防御式编程
        Assert.checkNonNull(ctx,"ctx 为空");
        Assert.checkNonNull(msg,"msg 为空");
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

        //获取消息构建者
        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        // 这一步别往下挪，会浪费 下面那行的io
        if (null == msgBuilder)
        {
            LOGGER.error("无法识别的消息，msgCode = {}",msgCode);
            return;
        }

        // 拿到真实的字节数组打印 消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);
        //构建消息
        Message newMsg = msgBuilder.build();

        if (null != newMsg){
            ctx.fireChannelRead(newMsg);
        }
    }
}
