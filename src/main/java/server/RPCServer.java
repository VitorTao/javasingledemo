package server;

import study.SocketHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RPCServer {

  private static ExecutorService threadPool =  new ThreadPoolExecutor(3,3,1L, TimeUnit.SECONDS,new LinkedBlockingDeque<>(100), Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());

  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket = new ServerSocket(8888);
    while (true) {
      System.out.println("conn wait");
      Socket socket = serverSocket.accept();
      System.out.println("conn success "+socket.getPort());
      threadPool.execute(new SocketHandler(socket));
    }
  }


}
