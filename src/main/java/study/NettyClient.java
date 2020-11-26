package study;//package study;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.string.StringEncoder;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class NettyClient {
//  private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//  public static void main(String[] args) {
//    // 客户端只需要一个线程组即可
//    NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
//    try {
//      // 采用Bootstrap而不是ServerBootstrap
//      Bootstrap bootstrap = new Bootstrap();
//      bootstrap.group(nioEventLoopGroup)
//              // 设置客户端的SocketChannel
//              .channel(NioSocketChannel.class)
//              .handler(new ChannelInitializer<NioSocketChannel>() {
//                @Override
//                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
//                  ChannelPipeline pipeline = nioSocketChannel.pipeline();
//                  // 添加一个字符串编码器
//                  pipeline.addLast(new StringEncoder());
//                  pipeline.addLast(new SimpleChannelInboundHandler<String>() {
//
//                    @Override
//                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//                      System.out.println(msg);
////                      ByteBuf buf = (ByteBuf) msg;
////                      byte[] req = new byte[buf.readableBytes()];
////                      buf.readBytes(req);
////                      String body = new String(req, "UTF-8");
////                      System.out.println(Thread.currentThread().getName() + ",Server return Message：" + body);
////                      ctx.close();
//                    }
//
//                  });
//                }
//              });
//      ChannelFuture channelFuture = bootstrap.connect("", 9090).sync();
//
//      Channel channel = channelFuture.channel();
//
//      executorService.scheduleAtFixedRate(()->{
//        String message = channel.localAddress().toString() + " Hello World";
//        channel.writeAndFlush(message);
//      },0,3, TimeUnit.SECONDS);
//
//      channel.closeFuture().sync();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } finally {
//      nioEventLoopGroup.shutdownGracefully();
//    }
//
//  }
//}
