package Netty5.Server;

import Netty5.message.AssignTaskMsg;
import Netty5.message.Configuration;
import Netty5.message.StatusMsg;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Task task = new Task();


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerHandler ChannelInactive. time: " + new Date());
        //task.assignTask(true, ctx.channel().remoteAddress().toString());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("5秒没有接收到客户端的信息了");
                System.out.println("关闭这个不活跃的客户端，回收任务");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    //每个信息入站都会调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead..");
        //System.out.println(ctx.channel().remoteAddress() + " received message: " + msg.toString());
        StatusMsg statusMsg = (StatusMsg) msg;
        System.out.println("received message: " + statusMsg.toString());
        task.setStatusMsg(statusMsg);
        AssignTaskMsg assignTaskMsg = task.assignTask(false, ctx.channel().remoteAddress().toString());
        //AssignTaskMsg assignTaskMsg1 = new AssignTaskMsg(5);

        Configuration cc = new Configuration(1, "127.0.0.1");

        ctx.channel().writeAndFlush(cc);
        System.out.println("---------------------------------------------------------------------");
    }

    /*
     //读完成
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx, Object msg) {

    }
    */

    //读操作时捕获到异常时调用（判断情况为客户端离线）
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exceptions..");
        //task.assignTask(true, ctx.channel().remoteAddress().toString());
        cause.printStackTrace();
        ctx.close();
    }
}
