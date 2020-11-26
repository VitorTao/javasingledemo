package study;

import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class MyProxy implements InvocationHandler{

  private Object o;
  public MyProxy(Object o){
    this.o = o;
  }

  public  Object getService(){
    Object o = Proxy.newProxyInstance(this.o.getClass().getClassLoader(), this.o.getClass().getInterfaces(), this);
    return o;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("开始");
    Socket s = new Socket("localhost", 8888);
    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
    RpcInfo rpcInfo = new RpcInfo();
    rpcInfo.setPackageName("study");
    rpcInfo.setClassName(o.getClass().getSimpleName());
//    rpcInfo.setClassName("RPCService");
    rpcInfo.setMethodName(method.getName());
    rpcInfo.setParams(args);
    out.writeObject(rpcInfo);
    out.flush();
    out.close();
//    method.invoke(o,args);
    return null;
  }
}
