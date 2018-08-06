package Netty_zz.client;

import Netty_zz.message.AssignTaskMsg;
import Netty_zz.message.StatusMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


import java.util.Date;

/**
 * Created by lujiafeng on 2018/7/28.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{

    private StatusMsg statusMsg = new StatusMsg(5);
    /**
     * 服务器建立连接时调用
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        System.out.println("ClientHandler ChannelActive. time: " + new Date());
    }

    /**
     * 服务器断开时调用
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println("ClientHandler ChannelInactive. time: " + new Date());
    }

    /**
     * 超时调用，向通道中写
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(statusMsg);
                System.out.println("发送数据:"+statusMsg);
            }
        }
    }

    /**
     * 读取通道
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        AssignTaskMsg assignTaskMsg = (AssignTaskMsg) msg;
        int assignTaskNum = assignTaskMsg.getAssignTaskNum();
        if(0 < assignTaskNum)
            statusMsg.addNowTaskSet(assignTaskMsg.getAssignTaskSet());
        else if (0 > assignTaskNum)
            statusMsg.removeNowTaskSet(assignTaskMsg.getAssignTaskSet());
        System.out.println(assignTaskMsg.toString());
    }

    /**
     * 异常时调用
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Exception!!!");
    }
}
