package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;

/**
 * Created by qiucy on 2020/6/8.
 */
public class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);
    /**
     * 应用主函数
     * @param argArray
     */
    static public void main(String[] argArray) {
        CmdHandlerFactory.init();
        // TODO pipeline：管道
        // 故事中的美女
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 故事中的小二
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup);
        // 服务端信道的处理方式
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        // http服务器编解码器
                        new HttpServerCodec(),
                        // 内容长度
                        new HttpObjectAggregator(65535),
                        // websocket 协议处理器，在这里处理握手 ping pong 等消息
                        new WebSocketServerProtocolHandler("/websocket"),
                        // 自定义消息解码器
                        new GameMsgDecoder(),
                        //自定义消息编码器
                        new GameMsgEncoder(),
                        new GameMsgHandler()
                );
            }
        });

        try{
            // 绑定 12345 端口
            // 注意 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture sync = b.bind(12345).sync();
            if (sync.isSuccess()){
                LOGGER.info("服务器启动成功");
//                System.out.println("服务器启动成功");
            }
            // 等待服务器信道关闭
            // 也就是不要退出应用程序
            // 让应用程序可以一直提供服务
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
