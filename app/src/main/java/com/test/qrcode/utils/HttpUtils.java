//package com.test.qrcode.utils;
//
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * Created by rongzezhang-i on 6/10/2017.
// */
//
//public class HttpUtils {
//
//
//    public static String submitPostData(URL url, List<Map<String, String>> params, String encode) throws IOException {
//
//        String boundary= UUID.randomUUID().toString();
//        String request = getRequestData(params, encode);
//
//        System.out.println("ByteArray:"+request);
//        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setConnectTimeout(5000);
//        conn.setDoOutput(true);
//        conn.setDoInput(true);
//        conn.setUseCaches(false);
//        conn.setInstanceFollowRedirects(true);
//        conn.setRequestProperty("Content-Length", String.valueOf(request.length()));
//
//        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
//        outputStream.writeBytes(request);
//        outputStream.flush();
//        outputStream.close();
//
//        int response = conn.getResponseCode();
//        if(response == HttpURLConnection.HTTP_OK) {
//            InputStream inptStream = conn.getInputStream();
//            String result = StreamUtils.readStream(inptStream);
//            inptStream.close();
//            conn.disconnect();
//            return result;
//        }else {
//            conn.disconnect();
//            return "Server error";
//        }
//    }
//
//    public static String submitGetData(URL url) throws IOException {
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(2000);
//            conn.setRequestProperty("Accept","application/json");
//            conn.setDoInput(true);
//            conn.connect();
//
//            int code = conn.getResponseCode();
//            if(code == HttpURLConnection.HTTP_OK){
//                InputStream is = conn.getInputStream();
//                String results = StreamUtils.readStream(is);
//                return results;
//            }else if(code == HttpURLConnection.HTTP_NOT_FOUND) {
//                return "Server error";
//            }else {
//                return "Sorry couldn't connect";
//            }
//    }
//
//    public static String getRequestData(List<Map<String, String>> params, String encode) {
//        StringBuffer stringBuffer = new StringBuffer();
//        try {
//            for(int i=0;i<params.size();i++){
//                Map<String, String> item = params.get(i);
//                for(String key : item.keySet()) {
//                    stringBuffer.append(key)
//                            .append("=")
//                            .append(URLEncoder.encode(item.get(key), encode))
//                            .append("&");
//                }
//            }
//            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(stringBuffer);
//        return stringBuffer.toString();
//    }
//}
