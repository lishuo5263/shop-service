package com.ecochain.ledger.web.rest;

import com.ecochain.ledger.model.R8ExChangeDTO;
import com.ecochain.ledger.util.JedisUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lisandro on 2017/5/15.
 */
@RestController
@RequestMapping("/api/rest/r8ExChange")
public class R8ExChangeWebService {

    private Logger logger = Logger.getLogger(R8ExChangeWebService.class);

    /**
     * @return
     * @author lishuo
     * describe 获取三界宝利率
     */
    @PostMapping("getCurrencyRate/{currency}")
    @ApiOperation(nickname = "getThreeBabyRate", value = "获取数字资产当前价值", notes = "getCurrencyRate")
    @ApiImplicitParam(name = "currency", value = "币种类型 如SAN BTC LTC ", required = true, paramType = "path", dataType = "String")
    public ResponseEntity<R8ExChangeDTO> getThreeBabyRate(@PathVariable("currency") String currency) {
        //JedisUtil jedisUtil=new JedisUtil();
        //System.out.println(JedisUtil.getJedisPool().getResource().get("220100"));
        System.out.println(JedisUtil.get("220100"));
        Map result = new HashMap();
        R8ExChangeDTO response=new R8ExChangeDTO();
        try {
            JSONObject obj = JSONObject.fromObject(R8ExChangeWebService.get("https://www.vpdax.com/api/elecoin/coin_data", "utf-8"));
            for (Object resultMsg : obj.getJSONArray("result")) {
                JSONObject resultInfo = (JSONObject) resultMsg;
                if (currency.equals(resultInfo.getString("currency"))) {
                    result.put("currentPrice", resultInfo.getString("last"));
                    response.setResponseCode("001");
                    response.setMessage(result);
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("getSanExrate fail,reason is " + e.getMessage());
            logger.debug(e.toString(), e);
        }
        return new ResponseEntity<R8ExChangeDTO>(response, HttpStatus.OK);
    }

    public static String get(String url, String charset) throws Exception {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new MyM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        InputStream response = connection.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] data = new byte[10240];
        int count = -1;
        while (-1 != (count = response.read(data, 0, data.length))) {
            result.write(data, 0, count);
        }
        connection.disconnect();
        return new String(result.toByteArray(), charset);
    }

    static class MyM implements TrustManager, X509TrustManager {
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
