package Netty_zz2.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import Netty_zz2.coder.MarshallingCodeCFactory;
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

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class Client {

    private int port;
    private String host;
    private String backupHost;
    private int backupPort;

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
                            ch.pipeline().addLast(new IdleStateHandler(0,2,0, TimeUnit.SECONDS))
                                    .addLast("encoder", MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast("decoder", MarshallingCodeCFactory.buildMarshallingDecoder())
                                    .addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 配置client的心跳服务器和备用心跳服务器，配置文件是Server.ini
     * @param path
     * @return
     */
    private boolean readConfigFile(String path){
        boolean modify = true;

        String lineString = null;
        String[] splitString = new String[2];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
        }catch (FileNotFoundException e){
            System.err.println("找不到配置文件");
            System.exit(-1);
            modify = false;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            lineString = bufferedReader.readLine();
            splitString = lineString.split("=");
            host = splitString[1].trim();

            lineString = bufferedReader.readLine();
            splitString = lineString.split("=");
            port = Integer.valueOf(splitString[1].trim());

            lineString = bufferedReader.readLine();
            splitString = lineString.split("=");
            backupHost = splitString[1].trim();

            lineString = bufferedReader.readLine();
            splitString = lineString.split("=");
            backupPort = Integer.valueOf(splitString[1].trim());
        }catch (Exception e){
            System.err.println("读取配置文件失败,请检查配置文件");
            modify = false;
        }
        return modify;
    }

    public static void main(String[] args) {

        Client client = new Client();
        String path = "Server.ini";

        if(!client.readConfigFile(path)){
            client.port = 65432;
            client.host = "127.0.0.1";
            client.backupHost = "127.0.0.1";
            client.backupPort = 65431;
        }

        while(true) {
            try {
                new Client().connect(client.host, client.port);
            }catch (Exception e) {
                try {
                    new Client().connect(client.backupHost, client.backupPort);
                }catch (Exception e1){

                }
            }
        }
    }
}
