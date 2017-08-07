package com.ecochain.ledger.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Http操作辅助工具
 */
public class HttpTool {
    /**
     * 发送请求数据
     *
     * @param url  url地址
     * @param data 发送内容 key=value形式
     * @return 返回结果
     * @throws Exception
     */
    public static boolean sendData(String url, NameValuePair[] data) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        //client.getParams().setContentCharset("GBK");      
        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");
        method.setRequestBody(data);
        try {
            client.executeMethod(method);

            String SubmitResult = method.getResponseBodyAsString();

            //System.out.println(SubmitResult);

            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();


            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");


            System.out.println(code);
            System.out.println(msg);
            System.out.println(smsid);

            if ("2".equals(code)) {
                System.out.println("短信提交成功");
                return true;
            } else {
                return false;
            }

        } catch (HttpException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (DocumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String doGet(String url) throws Exception {
        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    public static String doPost(String url, String parm) throws Exception {
        String parameterData = parm;

        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
        //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(parameterData.toString());
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> paramentMap = new HashMap<String, String>();
        /*paramentMap.put("address","f1gCgjsEhW8KyKcB18bPQzvJjeAVEgmBN6ubzdcR1njE");
        paramentMap.put("data","test");
        paramentMap.put("sign","AN1rKvtaZqkg1NiKBzCPRrXpyxzmpuJ57sJaZZNXqjYwBG1X51Uc5auqhKCNTagNqJUGkRzqk94mDzBUjMsWm9E3mcVaQsQzD");
        String result =doPost("http://192.168.10.47:8332/send_data_to_sys",com.alibaba.fastjson.JSON.toJSONString(paramentMap));
        System.out.println("send_data_to_sys --------->"+result);
        Map m =(Map)com.alibaba.fastjson.JSON.parse(result);
        if(StringUtil.isNotEmpty(m.get("result").toString())){
            StringBuffer stringBuffer =new StringBuffer().append("\"").append(m.get("result").toString()).append("\"");
            String getBlockInfo =doPost("http://192.168.10.47:8332/get_data_from_sys",stringBuffer.toString());
            System.out.println("get_data_from_sys -------------->"+getBlockInfo);
        }*/

        //StringBuffer stringBuffer = new StringBuffer().append("\"").append("e3230f30c2b53f8d6b6a44dd2eb5a5eef255b1234dfcbcd5899396f77ac5def1").append("\"");
        String uuid ="738475384578345";
        String bussType="insertShopOrder";
        String jsonInfo="eyAgCiJqc29ucnBjIjogIjIuMCIsICAKIm1ldGhvZCI6ICJxdWVyeSIsICAKInBhcmFtcyI6IHsgIAoidHlwZSI6IDEsICAKImNoYWluY29kZUlEIjp7ICAKIm5hbWUiOiIxNGI4N2IzYTFjNTZmOGNhNTZkY2VlMWEyNTBkZGRmMzYyZGQxZGZjOTc5YmY3MDAzNzg1ZjFiYTM0NDcyMGYyNmViOGE3OGJiMzU2ZGYzOTIxMjI5YjUxM2I0OTExMTNmMWJlODNhOGM1MTZkZTA0NjFhODI4NmJiNGMyM2VlMCIgIAp9LCAgCiJjdG9yTXNnIjogeyAgCiJmdW5jdGlvbiI6InF1ZXJ5T2JqcyIsICAKImFyZ3MiOlsiMTAwMSwxMDAyLDEwMDMiLCJvcmRlcixvcmRlcixwcm8iXSAgCn0sICAKInNlY3VyZUNvbnRleHQiOiAgImppbSIgIAp9LCAgCiJpZCI6IDUgIAp9ICA=";
        StringBuffer stringBuffer = new StringBuffer("{\n" +
                "    \"fcn\":\"createObj\",\n" +
                "    \"args\":[\n" +
                "        \""+uuid+"\",\n" +
                "        \""+bussType+"\",\n" +
                "\""+jsonInfo+"\"\n" +
                "    ]\n" +
                "}");
        System.out.println(stringBuffer.toString());
        System.out.println("853cce273737c89b02a42c694d80a9ede2dacf1373ac1038152a3488c1457ecc".length());
        String getBlockInfo = doPost(" http://192.168.200.191:4000/createObj", stringBuffer.toString());
        System.out.println(getBlockInfo);
    }
}