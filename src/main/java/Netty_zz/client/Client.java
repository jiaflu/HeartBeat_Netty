package Netty_zz.client;

import Netty_zz.coder.MarshallingCodeCFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class Client {

    public void connect(String host,int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch){
                            //检测连接有效性（心跳）,此处功能：5秒内write()未被调用则触发一次useEventTrigger()方法
                            ch.pipeline().addLast(new IdleStateHandler(0,2,0, TimeUnit.SECONDS));
                            ch.pipeline().addLast("encoder", MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast("decoder", MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 6543;
        String host = "127.0.0.1";
        String backupHost = "127.0.0.1";
        int backupPort = 65431;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        while(true) {
            try {
                new Client().connect(host, port);
            }catch (Exception e) {
                try {
                    new Client().connect(backupHost, backupPort);
                }catch (Exception e1){

                }
            }
        }
    }
}
