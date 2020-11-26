package com.fr.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class omtest {

  private static class CardInfo{
    BigDecimal price = new BigDecimal(0.0);
    String name = "张三";
    int age = 5;
    Date birth = new Date();

    public void m(){}
  }

  private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50,new ThreadPoolExecutor.DiscardOldestPolicy());

  public static void main(String[] args) throws InterruptedException {
    executor.setMaximumPoolSize(50);
    for (;;){
      modelfit();
      Thread.sleep(1000);
    }
  }

  private static void modelfit(){
    List<CardInfo> tasklist = getAllCardInfo();
    tasklist.forEach(info -> {
      executor.scheduleWithFixedDelay(() ->{
        info.m();
      },2,3, TimeUnit.SECONDS);

    });
  }

  private static List<CardInfo> getAllCardInfo(){
    List<CardInfo> tasklist = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      CardInfo ci = new CardInfo();
      tasklist.add(ci);
    }
    return tasklist;
  }
}
