package study;

public class RPCService implements IRPCService{

  public void testrpc(String s,Integer num){
    System.out.println(s+"调用了rpc"+num+"次");
  }
}
