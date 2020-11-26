package study;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RPClient {

  public static void main(String[] args) throws IOException {
//    Socket s = new Socket("localhost", 8888);
//    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//    RpcInfo rpcInfo = new RpcInfo();
//    Object[] objects = new Object[]{"vito",3};
//    rpcInfo.setPackageName("study");
//    rpcInfo.setClassName("RPCService");
//    rpcInfo.setMethodName("testrpc");
//    rpcInfo.setParams(objects);
//    out.writeObject(rpcInfo);
//    out.flush();
//    out.close();
    IRPCService irpcService = new RPCService();
    MyProxy myProxy= new MyProxy(irpcService);
//    IRPCService service = (IRPCService) Proxy.newProxyInstance(irpcService.getClass().getClassLoader(), irpcService.getClass()
//            .getInterfaces(), myProxy);
    IRPCService service = (IRPCService) myProxy.getService();
    service.testrpc("nihao",8);
  }
}
