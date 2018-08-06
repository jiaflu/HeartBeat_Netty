package Netty5.Client;

import Netty5.message.AssignTaskMsg;
import Netty5.message.Configuration;
import Netty5.message.StatusMsg;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private StatusMsg statusMsg = new StatusMsg(5);

    //服务器的连接建立后被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("CilentHandler ChannelActive. time: " + new Date());
        //System.out.println("向服务器发送初始化时本机的状态：" + statusMsg.toString());
        //ctx.channel().writeAndFlush(statusMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClinetHandler ChannelInactive. time: " + new Date());
    }

    //超时调用
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                //Thread.sleep(1000);
                ctx.channel().writeAndFlush(statusMsg);
                System.out.println("发送状态信息：" + statusMsg.toString());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Configuration cc = (Configuration) msg;
        System.out.println("接收到信息.." + cc.toString());
        /*
        AssignTaskMsg assignTaskMsg = (AssignTaskMsg) msg;
        int assignTaskNum = assignTaskMsg.getAssignTaskNum();
        //System.out.println(assignTaskMsg.getAssignTaskSet().size());

        if(0 < assignTaskNum)
            statusMsg.addNowTaskSet(assignTaskMsg.getAssignTaskSet());
        else if (0 > assignTaskNum)
            statusMsg.removeNowTaskSet(assignTaskMsg.getAssignTaskSet());

        System.out.println("Message received from Server: " + assignTaskMsg.toString());
        */
    }

    //出现异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //cause.printStackTrace();
        //ctx.close();
    }
}
