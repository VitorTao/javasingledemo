package study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class NIOLOOPServer {

  private static ServerSocketChannel serverSocketChannel;
  private static Selector selector;
  private static LinkedList<SocketChannel> sockets = new LinkedList();
  private static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

  public static void init() throws IOException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(8081));
//    selector = Selector.open();
    serverSocketChannel.configureBlocking(false);
//    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  public static void main(String[] args) {
    try {
      init();
      while (true) {
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
          socketChannel.configureBlocking(false);
//          socketChannel.register(selector, SelectionKey.OP_READ);
          sockets.add(socketChannel);
        }
        for (SocketChannel sock : sockets) {
          byteBuffer.clear();
          int read = sock.read(byteBuffer);// >0 -1 0
          if (read > 0) {
            byteBuffer.flip();
            byte[] b = new byte[byteBuffer.remaining()];
            byteBuffer.get(b);
            System.out.println("server 收到" + sock.socket().getPort() + "" + new String(b));
            byteBuffer.clear();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
