package study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NIOMultiSelectServer {

  private static ServerSocketChannel serverSocketChannel;
  private static Selector selector1;
  private static Selector selector2;
  private static Selector selector3;


  public static void init() throws IOException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(8081));
    selector1 = Selector.open();
    selector2 = Selector.open();
    selector3 = Selector.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.register(selector1, SelectionKey.OP_ACCEPT);
  }

  public static void main(String[] args) {
    try {
      init();
      NIOThread n1 = new NIOThread(selector1,2);
      NIOThread n2 = new NIOThread(selector2);
      NIOThread n3 = new NIOThread(selector3);
      new Thread(n1).start();
      Thread.sleep(200);
      new Thread(n2).start();
      new Thread(n3).start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
