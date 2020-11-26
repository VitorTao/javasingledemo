package study;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
  private static ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

  public static void main(String[] args) {
    try {
      SocketChannel socketChannel = SocketChannel.open();
      socketChannel.connect(new InetSocketAddress(8081));
      byteBuffer.clear();
      byte[] b = new byte[1024];
      byteBuffer.put("client send 111".getBytes());
      byteBuffer.flip();
      socketChannel.write(byteBuffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
