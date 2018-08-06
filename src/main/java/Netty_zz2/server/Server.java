package Netty_zz2.server;


import Netty_zz2.coder.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class Server {

    private int port;

    public Server(int port) { this.port = port; }

    public void start() {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boosGroup,workerGroup)     //非阻塞方式
                    .channel(NioServerSocketChannel.class) //指定需要使用的管道类
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //检测连接有效性（心跳）,此处功能：3秒内read()未被调用则触发一次useEventTrigger()方法
                            ch.pipeline().addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS))

                                    //JBossMarshalling外部依赖，进行编解码
                                    .addLast("encoder", MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast("decoder", MarshallingCodeCFactory.buildMarshallingDecoder())
                                    .addLast(new ServerHandler());  //ServerHandler实现了业务逻辑
                        }
                    })
                    //服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                    // 可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定端口，开始接收进来的连接
            ChannelFuture future = sb.bind(port).sync(); //绑定服务器，等待绑定完成，调用sync()的原因是当前线程阻塞

            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();  //关闭channel和块，直到它被关闭
        } catch (Exception e) {
            boosGroup.shutdownGracefully();   //关闭EventLoopGroup，释放所有资源（包括所有创建的线程）
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        int port;
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }catch (NumberFormatException e){
                port = 65432;
            }
        } else {
            port = 65432;
        }
        new Server(port).start();
    }
}
