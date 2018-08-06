package Netty_zz2.server;


import Netty_zz2.message.AssignTaskMsg;
import Netty_zz2.message.StatusMsg;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * Created by lujiafeng on 2018/7/28.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final TaskAssign task = new TaskAssign();

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        task.setStatusMsg((StatusMsg) msg);
        AssignTaskMsg assignTaskMsg = task.assignTask(false,ctx.channel().remoteAddress().toString());
        ctx.write(assignTaskMsg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    //读操作时捕获到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Exception!!!!!");
        task.assignTask(true,ctx.channel().remoteAddress().toString());
    }
}
