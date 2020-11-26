package server;

import java.io.IOException;

public class RPClient {

  public static void main(String[] args) throws IOException {
//    Socket s = new Socket("localhost", 8888);
//    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
//    RpcInfo rpcInfo = new RpcInfo();
//    Object[] objects = new Object[]{"vito"};
//    rpcInfo.setPackageName("com.its.study");
//    rpcInfo.setClassName("RPCService");
//    rpcInfo.setMethodName("testrpc");
//    rpcInfo.setParamTypes(objects);
//    out.writeObject(rpcInfo);
//    out.flush();
//    out.close();
    System.out.println(Runtime.getRuntime().availableProcessors());
  }
}
