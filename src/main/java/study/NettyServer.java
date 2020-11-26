package study;//package study;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
//public class NettyServer {
//
//  public static void main(String[] args) throws InterruptedException {
//    NioEventLoopGroup bossgroup = null;
//    NioEventLoopGroup workgroup = null;
//    try {
//      bossgroup = new NioEventLoopGroup(1);
//      workgroup = new NioEventLoopGroup(3);
//      ServerBootstrap serverBootstrap = new ServerBootstrap();
//      serverBootstrap.group(bossgroup, workgroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
//              .childHandler(new ChannelInitializer<NioSocketChannel>() {
//                @Override
//                protected void initChannel(NioSocketChannel ch) throws Exception {
//                  ChannelPipeline pipeline = ch.pipeline();
//                  // 字符串解码器
//                  pipeline.addLast(new StringDecoder()).addLast(new SimpleChannelInboundHandler<String>() {
//
//                    @Override
//                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//                      System.out.println(msg);
//                      ByteBuf responbytebuf = Unpooled.copiedBuffer("from  server ".getBytes());
////                      ctx.writeAndFlush(responbytebuf);
//                      ctx.write(responbytebuf);
//                    }
//
//                    @Override
//                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//                      super.channelRegistered(ctx);
//                      System.out.println("有新客户端连接");
//                    }
//                    @Override
//                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//                      /**flush：将消息发送队列中的消息写入到 SocketChannel 中发送给对方，为了频繁的唤醒 Selector 进行消息发送
//                       * Netty 的 write 方法并不直接将消息写如 SocketChannel 中，调用 write 只是把待发送的消息放到发送缓存数组中，再通过调用 flush
//                       * 方法，将发送缓冲区的消息全部写入到 SocketChannel 中
//                       * */
//                      ctx.flush();
//                    }
//
//                  });
//                  // 自定义的handler，用来打印接收到的消息
//                }
//              });
//      ChannelFuture channelFuture = serverBootstrap.bind(9090).sync();
//      channelFuture.channel().closeFuture().sync();
//
//    }finally {
//      // 优雅的关闭EventLoopGroup，释放所有的资源
//      bossgroup.shutdownGracefully();
//      workgroup.shutdownGracefully();
//    }
//  }
//}
