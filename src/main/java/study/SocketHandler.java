package study;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketHandler implements Runnable{

  private Socket socket;
  public SocketHandler(Socket socket){
    this.socket = socket;
  }
  @Override
  public void run() {
    try {
      ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
      RpcInfo rpcInfo = (RpcInfo) ois.readObject();
      Class clazz = Class.forName(rpcInfo.getPackageName()+"."+rpcInfo.getClassName());
      Class[] classes = new Class[rpcInfo.getParams().length];
      for (int i = 0; i < rpcInfo.getParams().length; i++) {
        classes[i] = rpcInfo.getParams()[i].getClass();
      }
      Method method = clazz.getMethod(rpcInfo.getMethodName(), classes);
      method.invoke(clazz.newInstance(),rpcInfo.getParams());
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
