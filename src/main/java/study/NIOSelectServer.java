package study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOSelectServer {

  private static ServerSocketChannel serverSocketChannel;
  private static Selector selector;
  private static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

  public static void init() throws IOException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(8081));
    System.out.println(serverSocketChannel.socket().getInetAddress());
    System.out.println(serverSocketChannel.socket().getLocalSocketAddress());
    selector = Selector.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  public static void main(String[] args) {
    try {
      init();
      while (selector.select() > 0) {
        System.out.println("有事件就绪进来了");
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();
          if (key.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            System.out.println(socketChannel.socket().getPort() + "请求链接");
            socketChannel.register(selector, SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            byteBuffer.clear();
            int read = socketChannel.read(byteBuffer);
            if (read > 0) {
              byteBuffer.flip();
              byte[] b = new byte[byteBuffer.limit()];
              byteBuffer.get(b);
              System.out.println("收到" + socketChannel.socket().getPort() + "发来数据" + new String(b));
              byteBuffer.clear();
            }
          }

        }


      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
