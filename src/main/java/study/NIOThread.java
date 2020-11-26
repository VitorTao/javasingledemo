package study;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class NIOThread implements Runnable {

  private Selector selector;
  private static int selectors;
  private int id = -1;
  private static AtomicInteger atomicInteger = new AtomicInteger();
  private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
  private volatile static BlockingQueue<SocketChannel>[] q;

  public NIOThread(Selector selector, int selectors) {
    this.selector = selector;
    this.selectors = selectors;
    q = new LinkedBlockingQueue[selectors];
    for (int i = 0; i < selectors; i++) {
      q[i] = new LinkedBlockingQueue<>();
    }
  }

  public NIOThread(Selector selector) {
    this.selector = selector;
    id = atomicInteger.getAndIncrement() % selectors;
  }

  @Override
  public void run() {
    while (true) {
      try {
        if (!(selector.select(3000) > 0)) break;
        System.out.println("有事件就绪进来了");
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();
          if (key.isAcceptable()) {
            accepthandle(key);
          } else if (key.isReadable()) {
            readhandle(key);
          } else if (key.isWritable()) {
            writehandle(key);
          }
        }
        if (id != -1 && !q[id].isEmpty()) {
          SocketChannel socketChannel = q[id].take();
          socketChannel.register(selector, SelectionKey.OP_READ);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void writehandle(SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    byteBuffer.clear();
    byteBuffer.put("nihao".getBytes());
    byteBuffer.flip();
    System.out.println("server发送数据" + "你好");
    socketChannel.write(byteBuffer);
    byteBuffer.clear();
  }

  private void readhandle(SelectionKey key) throws IOException {
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

  private void accepthandle(SelectionKey key) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel socketChannel = serverSocketChannel.accept();
    socketChannel.configureBlocking(false);
    System.out.println(socketChannel.socket().getPort() + "请求链接");
    q[atomicInteger.getAndIncrement() % selectors].add(socketChannel);
  }
}
