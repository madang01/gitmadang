package io.netty.example.echo;
 
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
 
public class ServerHandler extends ChannelInboundHandlerAdapter {
 
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ctx.writeAndFlush(msg);  // loop message back
  }
}