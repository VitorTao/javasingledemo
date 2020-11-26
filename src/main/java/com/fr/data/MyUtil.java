package com.fr.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MyUtil {

  public static void main(String[] args) {
    String s = "[['1589243040000','全部登录用户',20.0,11.0],['1590714268000','全部登录用户',30.0,11.0],['1599541636000','全部访问用户',40.0,44.0],['1586300584000','全部访问用户',50.0,44.0]]\n";
    JSONArray array = JSONObject.parseArray(s);
    System.out.println(array.toString());
    System.out.println(array.stream().filter(f -> "全部登录用户".equals(((JSONArray) f).get(1).toString())).mapToInt(e -> Float.valueOf(((JSONArray) e).get(3).toString()).intValue()).sum());
    System.out.println(array.stream().mapToInt(e -> Float.valueOf(((JSONArray)e).get(2).toString()).intValue() ).sum());
    System.out.println(array.stream().mapToInt(e -> Float.valueOf(((JSONArray)e).get(3).toString()).intValue() ).sum());
  }
}
