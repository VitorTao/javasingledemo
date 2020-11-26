package com.fr.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fr.json.JSONObjectWriter;
import com.fr.json.JSONParser;
import com.fr.stable.ParameterProvider;
import com.fr.third.org.hsqldb.lib.StringUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class GIOUtil extends AbstractTableData {
  private static final String DEFAULT_CHARSET = "UTF-8";
  private static Map result = new ConcurrentHashMap<>();
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  private String[] columnNames;
  private Object[][] rowData;
  private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9,9,3L, TimeUnit.SECONDS, new LinkedBlockingQueue(9),Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());
  private static CountDownLatch countDownLatch = new CountDownLatch(9);
  enum gioChart {

    IELTS_SITE_UV_PV_LOGINUSERNUM("bR7BNJr9"),
    IELTS_PC_UV_PV_LOGINUSERNUM("GR4axYDP"),
    IELTS_APP_UV_PV_LOGINUSERNUM("AovYK1jP"),
    IELTS_IOS_UV_PV_LOGINUSERNUM("lPQgz2v9"),
    IELTS_ANDROID_UV_PV_LOGINUSERNUM("1P6YazzR"),
    IELTS_SITE_INTENSIVE("AovYKZYP"),
    IELTS_APP_INTENSIVE("AovYKZYP"),
    IELTS_IOS_INTENSIVE("4PYeExxP"),
    IELTS_ANDROID_INTENSIVE("1P6YabAR");
    private String chart;
    gioChart(String chart){
      this.chart = chart;
    }

    public String getChart() {
      return chart;
    }

  }

  public GIOUtil() {
    String[] columnNames = new String[]{"site_user_lognum", "site_pv", "site_uv","pc_user_lognum", "pc_pv", "pc_uv","app_user_lognum", "app_pv", "app_uv","ios_user_lognum", "ios_pv", "ios_uv","android_user_lognum", "android_pv", "android_uv",
            "site_intensive_users","site_intensive_times","app_intensive_users","app_intensive_times","ios_intensive_users","ios_intensive_times","android_intensive_users","android_intensive_times"};
    this.columnNames = columnNames;
  }

  public void init() {

    long t1 = System.currentTimeMillis();
    try {
      String startTime = null;
      String endTime = null;
      startTime = ((ParameterProvider) (parameters.get().toArray())[0]).getValue().toString();
      endTime = ((ParameterProvider) (parameters.get().toArray())[1]).getValue().toString();
      startTime = String.valueOf(sdf.parse(startTime).getTime());
      endTime = String.valueOf(sdf.parse(endTime).getTime());
      System.out.println(System.currentTimeMillis());
      HashMap header = new HashMap();
      header.put("Authorization", "AKU0zaNMuEN48L5r6jqnMml4Yllf9ZogPoD91k46Ni6tRMVxEj7byRnvo5a7oHqJ");
//      精听人数次数
      getIntensiveUserCountAndTimes(startTime,endTime,header,result);
      System.out.println(" 等待");
      countDownLatch.await();
      System.out.println(" 放行");
      Object[][] data = new Object[1][columnNames.length];
      for (int i = 0; i < columnNames.length; i++) {
        data[0][i] = result.get(columnNames[i]);
      }
      long t2 = System.currentTimeMillis();
      System.out.println((t2-t1)/1000+"耗时");
      this.rowData = data;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void getIntensiveUserCountAndTimes(String startTime, String endTime, HashMap header, Map result) throws Exception {
    HashMap p = new HashMap();
    String url = "https://www.growingio.com/v3/exporter/projects/xo0zAERp/charts/";
    String gioCharts = "nP2Aw8zR";
    String export_url = url + gioCharts ;
    p.put("interval", String.valueOf(86400000 * 365));
    p.put("startTime", startTime != null ? startTime : "1585378000000");
    p.put("endTime", endTime != null ? endTime : String.valueOf(sdf.parse(sdf.format(new Date())).getTime()));
    for (int i = 0; i < gioChart.values().length; i++) {
      gioCharts = gioChart.values()[i].getChart();
      export_url = url + gioCharts ;
      System.out.println(export_url);
      executorService.submit(new ParseDataTask(export_url, p,header,result,gioChart.values()[i]));
//      parsedata(export_url, p,header,list,gioChart.values()[i]);
    }
  }

  private boolean validateRes(String url, HashMap p, HashMap header) throws IOException {
    String res = doGet(url, p,header);
    JSONObject jsonObject = JSONObject.parseObject(res);
    if( jsonObject.get("status") != null && "doing".equals(jsonObject.get("status").toString())){
      return false;
    }
    return true;
  }



  private void setUvPvLoginnumDataTolist(Map result, JSONArray arr,String chartName) {
    //查出来各项正好合并成一条了
    if(arr.size() == 2){
      JSONArray arr0 = (JSONArray) arr.get(0);
      JSONArray arr1 = (JSONArray) arr.get(1);
      result.put(chartName.split("_")[1].toLowerCase()+"_user_lognum",arr0.get(3).toString());
      result.put(chartName.split("_")[1].toLowerCase()+"_pv",arr1.get(2).toString());
      result.put(chartName.split("_")[1].toLowerCase()+"_uv",arr1.get(3).toString());
      // 查出来多条
    }else if(arr.size() > 2){
      result.put(chartName.split("_")[1].toLowerCase()+"_user_lognum",String.valueOf(arr.stream().filter( f -> "全部登录用户".equals(((JSONArray)f).get(1).toString()) ).mapToInt(e -> Float.valueOf(((JSONArray)e).get(3).toString()).intValue() ).sum()));
      result.put(chartName.split("_")[1].toLowerCase()+"_pv",String.valueOf(arr.stream().filter( f -> "全部访问用户".equals(((JSONArray)f).get(1).toString()) ).mapToInt(e -> Float.valueOf(((JSONArray)e).get(2).toString()).intValue() ).sum()));
      result.put(chartName.split("_")[1].toLowerCase()+"_uv",String.valueOf(arr.stream().filter( f -> "全部访问用户".equals(((JSONArray)f).get(1).toString()) ).mapToInt(e -> Float.valueOf(((JSONArray)e).get(3).toString()).intValue() ).sum()));
    }else{
      result.put(chartName.split("_")[1].toLowerCase()+"_user_lognum",0);
      result.put(chartName.split("_")[1].toLowerCase()+"_pv",0);
      result.put(chartName.split("_")[1].toLowerCase()+"_uv",0);
    }
  }

  //实现ArrayTableData的其他四个方法，因为AbstractTableData已经实现了hasRow方法
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  @Override
  public int getRowCount() {
    init();
    return rowData.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    init();
    return rowData[rowIndex][columnIndex];
  }

  private static class DefaultTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  }

  private static HttpURLConnection getConnection(final URL url, final String method, final String ctype,final Map<String, String> header) throws IOException {
    HttpURLConnection conn = null;
    if ("https".equals(url.getProtocol())) {
      SSLContext ctx = null;
      try {
        ctx = SSLContext.getInstance("TLS");
        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
      } catch (Exception e) {
        throw new IOException(e);
      }
      final HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
      connHttps.setSSLSocketFactory(ctx.getSocketFactory());
      connHttps.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
          return true;
        }
      });
      conn = connHttps;
    } else {
      conn = (HttpURLConnection) url.openConnection();
    }
    conn.setRequestMethod(method);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestProperty("User-Agent", "quantangle- apiclient-java");
    conn.setRequestProperty("Content-Type", ctype);
    conn.setRequestProperty("Connection", "Keep-Alive");
    for (Map.Entry  map:header.entrySet()) {
      conn.setRequestProperty(map.getKey().toString(), map.getValue().toString());
    }
    return conn;
  }

  public static String doGet(final String url, final Map<String, String> params,final Map<String, String> header) throws IOException {
    return doGet(url, params,header, "UTF-8");
  }

  public static String doGet(String url, final Map<String, String> params,final Map<String, String> header, final String charset) throws IOException {
    if (StringUtil.isEmpty(url) || params == null) {
      return null;
    }
    String response = "";
    url = url + "?" + buildQuery(params, charset);
    HttpURLConnection conn = null;
    final String ctype = "application/x-www-form-urlencoded;charset=" + charset;
    conn = getConnection(new URL(url), "GET", ctype,header);
    response = getResponseAsString(conn);
    return response;
  }

  public static String doPost(final String url, final Map<String, String> params) throws IOException {
    return doPost(url, params, 5000, 5000);
  }

  public static String doPost(final String url, final Map<String, String> params, final int connectTimeOut, final int readTimeOut) throws IOException {
    return doPost(url, params, "UTF-8", connectTimeOut, readTimeOut);
  }

  public static String doPost(final String url, final Map<String, String> params, final String charset, final int connectTimeOut, final int readTimeOut) throws IOException {
    HttpURLConnection conn = null;
    String response = "";
    final String ctype = "application/x-www-form- urlencoded;charset=" + charset;
    conn = getConnection(new URL(url), "POST", ctype,null);
    conn.setConnectTimeout(connectTimeOut);
    conn.setReadTimeout(readTimeOut);
    conn.getOutputStream().write(buildQuery(params, charset).getBytes(charset));
    response = getResponseAsString(conn);
    return response;
  }

  public static String buildQuery(final Map<String, String> params, final String charset) {
    if (params == null || params.isEmpty()) {
      return null;
    }
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (final Map.Entry<String, String> entry : params.entrySet()) {
      if (first) {
        first = false;
      } else {
        sb.append("&");
      }
      final String key = entry.getKey();
      final String value = entry.getValue();
      if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)) {
        try {
          sb.append(key).append("=").append(URLEncoder.encode(value, charset));
        } catch (UnsupportedEncodingException ex) {
        }
      }
    }
    return sb.toString();
  }

  private static String getResponseAsString(final HttpURLConnection conn) throws IOException {
    final String charset = getResponseCharset(conn.getContentType());
    final InputStream es = conn.getErrorStream();
    if (es == null) {
      return getStreamAsString(conn.getInputStream(), charset);
    }
    final String msg = getStreamAsString(es, charset);
    if (StringUtil.isEmpty(msg)) {
      throw new IOException("{\"" + conn.getResponseCode() + "\":\"" + conn.getResponseMessage() + "\"}");
    }
    throw new IOException(msg);
  }

  private static String getStreamAsString(final InputStream input, final String charset) throws IOException {
    final StringBuilder sb = new StringBuilder();
    BufferedReader bf = null;
    try {
      bf = new BufferedReader(new InputStreamReader(input, charset));
      String str;
      while ((str = bf.readLine()) != null) {
        sb.append(str);
      }
      return sb.toString();
    } finally {
      if (bf != null) {
        bf.close();
        bf = null;
      }
    }
  }

  private static String getResponseCharset(final String ctype) {
    String charset = "UTF-8";
    if (!StringUtil.isEmpty(ctype)) {
      final String[] split;
      final String[] params = split = ctype.split("\\;");
      for (String param : split) {
        param = param.trim();
        if (param.startsWith("charset")) {
          final String[] pair = param.split("\\=");
          if (pair.length == 2) {
            charset = pair[1].trim();
          }
        }
      }
    }
    return charset;
  }


  class ParseDataTask implements Runnable{

    private String url;
    private HashMap p;
    private HashMap header;
    private Map result;
    private gioChart chartName;
    ParseDataTask(String url, HashMap p, HashMap header, Map result, gioChart chartName){
      this.url = url;
      this.p = p;
      this.header = header;
      this.result = result;
      this.chartName = chartName;
    }
    @Override
    public void run() {
      String res = null;
      try {
        while(!validateRes(url, p,header)){
//      Thread.sleep(1000);
        }
        res = doGet(url, p,header);
      } catch (IOException e) {
        e.printStackTrace();
      }
      JSONObject jsonObject = JSONObject.parseObject(res);
      if(jsonObject.get("data") == null)return;
      JSONArray arr = (JSONArray) jsonObject.get("data");
      System.out.println(jsonObject.get("data").toString());
      switch(chartName){
        case IELTS_SITE_UV_PV_LOGINUSERNUM:
        case IELTS_PC_UV_PV_LOGINUSERNUM:
        case IELTS_APP_UV_PV_LOGINUSERNUM:
        case IELTS_IOS_UV_PV_LOGINUSERNUM:
        case IELTS_ANDROID_UV_PV_LOGINUSERNUM:
          setUvPvLoginnumDataTolist(result, arr,chartName.toString());
          break;
        case IELTS_SITE_INTENSIVE:
        case IELTS_APP_INTENSIVE:
        case IELTS_IOS_INTENSIVE:
        case IELTS_ANDROID_INTENSIVE:
          setIntensiveData(result,arr,chartName.toString());
          break;
        default:
          System.out.println("没有匹配");
      }
      countDownLatch.countDown();
    }
  }

  private void setIntensiveData(Map result, JSONArray arr,String chartName) {
    // 查出来各项正好合并成一条了 全部登录用户  全部访问用户
    if(arr.size() == 1){
      JSONArray arr0 = (JSONArray) arr.get(0);
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_users",arr0.get(2).toString());
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_times",arr0.get(3).toString());
      // 查出来多条
    }else if(arr.size() > 1){
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_users",String.valueOf(arr.stream().mapToInt(e -> Float.valueOf(((JSONArray)e).get(2).toString()).intValue() ).sum()));
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_times",String.valueOf(arr.stream().mapToInt(e -> Float.valueOf(((JSONArray)e).get(3).toString()).intValue() ).sum()));
    }else{
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_users",0);
      result.put(chartName.split("_")[1].toLowerCase()+"_intensive_times",0);
    }
  }

  public static void main(String[] args) throws Exception {
    GIOUtil h = new GIOUtil();
    h.init();
//    System.out.println(sdf.parse(sdf.format(new Date())).getTime());
  }

}
