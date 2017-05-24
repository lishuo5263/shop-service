package com.ecochain.ledger.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.QklLibService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.HttpUtil;
import com.ecochain.ledger.util.Logger;
import com.ecochain.ledger.util.StringUtil;
import com.sun.jna.Library;
import com.sun.jna.Native;

@Component("qklLibService")
public class QklLibServiceImpl implements QklLibService {

    private final Logger logger = Logger.getLogger(QklLibServiceImpl.class);
    
    @Autowired
    private  SysGenCodeService sysGenCodeService;

    @Override
    public PageData getPriPubKey(String seedsStr) throws Exception {
        byte[] seedsByte = seedsStr.getBytes();
        byte[] pubkeyByte = new byte[64];
        byte[] prikeyByte = new byte[64];
        byte[] errmsgByte = new byte[64];
        
        String pubkey = "";
        String prikey = "";
        String errmsg = "";
        Clibrary.INSTANCE.InitCrypt();
        int getPriPubKey = Clibrary.INSTANCE.GetPriPubKey(seedsByte,pubkeyByte,prikeyByte,errmsgByte);
        
        pubkey = new String(pubkeyByte);
        prikey = new String(prikeyByte);
        errmsg = new String(errmsgByte);
        Clibrary.INSTANCE.StopCrypt();
        System.out.println("pubkey="+pubkey+",prikey="+prikey+",errmsg="+errmsg);
        System.out.println("=================掉动态库结束=============返回值getPriPubKey="+getPriPubKey);
        errmsg = StringUtil.isNotEmpty(errmsg)?errmsg.trim():errmsg;
        if(getPriPubKey==0&&"success".equals(errmsg)){
            PageData pd = new PageData();
            pd.put("pubkey", pubkey);
            pd.put("prikey", prikey);
            pd.put("errmsg", errmsg);
            pd.put("prikeyByte", prikeyByte);
            System.out.println("=================掉动态库结束1=============pd value is"+pd.toString());
            return pd;
        }else{
            System.out.println("=================掉动态库结束1");
            return null;
        }
    }

   /* @Override
    public String sendDataToSys(String seedsStr, Object data) throws Exception {
        System.out.println("*************获取数据签名***************data value is "+data.toString());
        PageData pd = this.getPriPubKey(seedsStr);
        
        System.out.println("=================掉动态库结束2=============pd value is"+pd.toString());
        if(pd == null){
            return null;
        }
        byte[] prikeyByte = (byte[])pd.get("prikeyByte");
        byte[] signByte = new byte[128];
        byte[] errmsg_sign = new byte[64];
        byte[] dataByte = (Base64.getBase64(JSON.toJSONString(data)).replaceAll("\r|\n", "")+"\0").getBytes();;
        String errmsg = "";
        String sign = "";
        System.out.println("*************获取数据签名**************start*********");
        Clibrary.INSTANCE.InitCrypt();
        int getDataSign = Clibrary.INSTANCE.GetDataSign(prikeyByte,dataByte,signByte,errmsg_sign);
        sign = new String(signByte);
        errmsg = new String(errmsg_sign);
        errmsg = StringUtil.isNotEmpty(errmsg)?errmsg.trim():errmsg;
        System.out.println("获取数据签名getDataSign="+getDataSign+",sign="+sign+",data="+new String(dataByte).trim()+",errmsg="+errmsg);
        if(getDataSign==0&&"success".equals(errmsg)){
            Map<String, Object> paramentMap =new HashMap<String, Object>();
            paramentMap.put("publickey",pd.getString("pubkey"));
            paramentMap.put("data",new String(dataByte).trim());
            paramentMap.put("sign",StringUtil.isNotEmpty(sign)?sign.trim():sign);
            List<PageData> codeList = sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            String kql_url ="";
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            System.out.println("===================调区块链，请求地址："+kql_url+"/send_data_to_sys，参数："+JSON.toJSONString(paramentMap));
            String result = HttpUtil.sendPostData(kql_url+"/send_data_to_sys", JSON.toJSONString(paramentMap));
            JSONObject json = JSON.parseObject(result);
            System.out.println("===================添加区块数据返回结果json.toJSONString()"+json.toJSONString());
            if(StringUtil.isNotEmpty(json.getString("result"))&&!json.getString("result").contains("failure")){
                accDetail.put("hash", json.getString("result")); 
                pd.put("trade_hash", json.getString("result")); 
                return result;
            }else{
                Clibrary.INSTANCE.StopCrypt();
                throw new RuntimeException("===================商城支付添加区块数据失败================================");
            }
           
        }else{
            Clibrary.INSTANCE.StopCrypt();
            throw new RuntimeException("===================商城支付掉动态库失败================================");
        }
    }*/
    @Override
    public String sendDataToSys(String seedsStr, Object data) throws Exception {
        logger.info("===================sendDataToSys发送数据到区块链=======start===========");
        logger.info("参数seeds="+seedsStr+",data="+data.toString());
        byte[] seedsByte = seedsStr.getBytes();
        byte[] pubkeyByte = new byte[64];
        byte[] prikeyByte = new byte[64];
        byte[] errmsgByte = new byte[64];
        byte[] errmsg_sign = new byte[64];
        byte[] signByte = new byte[128];
//        byte[] dataByte = JSON.toJSONString(JSON.toJSONString(accDetail)).getBytes();
        byte[] dataByte = (Base64.getBase64(JSON.toJSONString(data)).replaceAll("\r|\n", "")+"\0").getBytes();;
        
        
        String publickey = "";
        String privateKey = "";
        String errmsg = "";
        String sign = "";
        
        Clibrary.INSTANCE.InitCrypt();
        int getPriPubKey = Clibrary.INSTANCE.GetPriPubKey(seedsByte,prikeyByte,pubkeyByte,errmsgByte);
        publickey = new String(pubkeyByte);
        publickey = StringUtil.isNotEmpty(publickey)?publickey.trim():publickey;
        privateKey = new String(prikeyByte);
        errmsg = new String(errmsgByte);
        logger.info("publickey="+publickey+",privateKey="+privateKey+",errmsg="+errmsg);
        errmsg = StringUtil.isNotEmpty(errmsg)?errmsg.trim():errmsg;
        int getDataSign = -1;
        if(getPriPubKey==0&&"success".equals(errmsg)){
            System.out.println("publickey="+new String(publickey));
            getDataSign = Clibrary.INSTANCE.GetDataSign(prikeyByte,dataByte,signByte,errmsg_sign);
            sign = new String(signByte);
            errmsg = new String(errmsg_sign);
            errmsg = StringUtil.isNotEmpty(errmsg)?errmsg.trim():errmsg;
            logger.info("获取数据签名getDataSign="+getDataSign+",sign="+sign+",data="+new String(dataByte).trim()+",errmsg="+errmsg);
            System.out.println("===================获取签名=======end===========");
            if(getDataSign==0&&"success".equals(errmsg)){
                Map<String, Object> paramentMap =new HashMap<String, Object>();
                paramentMap.put("publickey",publickey);
                paramentMap.put("data",new String(dataByte).trim());
                paramentMap.put("sign",StringUtil.isNotEmpty(sign)?sign.trim():sign);
                List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
                String kql_url ="";
                for(PageData mapObj:codeList){
                    if("QKL_URL".equals(mapObj.get("code_name"))){
                        kql_url = mapObj.get("code_value").toString();
                    }
                }
                logger.info("===================发送数据到区块链，请求地址："+kql_url+"/send_data_to_sys，参数："+JSON.toJSONString(paramentMap));
                String result = HttpUtil.sendPostData(kql_url+"/send_data_to_sys", JSON.toJSONString(paramentMap));
                JSONObject json = JSON.parseObject(result);
                logger.info("===================发送数据到区块链，返回结果json.toJSONString()"+json.toJSONString());
                if(StringUtil.isNotEmpty(json.getString("result"))&&!json.getString("result").contains("failure")){
                    logger.info("===================sendDataToSys发送数据到区块链=======end===========");
                    return result;
                }else{
                    Clibrary.INSTANCE.StopCrypt();
                    throw new RuntimeException("===================发送数据到区块链失败================================");
                }
               
            }else{
                Clibrary.INSTANCE.StopCrypt();
                throw new RuntimeException("===================获取签名失败================================");
            }
        }else{
            Clibrary.INSTANCE.StopCrypt();
            throw new RuntimeException("===================获取公私钥失败================================");
        }
    }
    
    public interface Clibrary extends Library { 

//      Dll INSTANCE = (Dll) Native.loadLibrary("btcsign", Dll.class);//加载动态库文件
      
          public Clibrary INSTANCE = (Clibrary) Native.loadLibrary("/data/lib/libbtcsign.so", Clibrary.class);
          //获得公钥和私钥,第一个参数密钥种子为可选项，如果有值，则长度为32
//          public int GetPriPubKey( String seeds, String prikey, String pubkey, String errmsg);
          public int GetPriPubKey( byte[] seeds, byte[] prikey, byte[] pubkey, byte[] errmsg);

          //获得数据的sign,输入私钥和数据，返回签名
//          public int GetDataSign( String prikey, String data, String sign, String errmsg);
          public int GetDataSign(byte[] prikey, byte[] data, byte[] sign, byte[] errmsg);
          public void InitCrypt();
          public void StopCrypt();
  }
    public static void main(String[] args) {
        PageData data = new PageData();
        data.put("a", "测试");
//       logger.info("====================测试代码========start================");
        try { 
            
                String jsonStr = HttpUtil.sendPostData("http://192.168.200.81:8332/get_new_key", "");
                JSONObject keyJsonObj = JSONObject.parseObject(jsonStr);
                PageData keyPd = new PageData();
                keyPd.put("data",Base64.getBase64((JSON.toJSONString(data))));
                keyPd.put("publicKey",keyJsonObj.getJSONObject("result").getString("publicKey"));
                keyPd.put("privateKey",keyJsonObj.getJSONObject("result").getString("privateKey"));
                System.out.println("keyPd value is ------------->"+JSON.toJSONString(keyPd));
                //2. 获取公钥签名
           
                String signJsonObjStr =HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_for_sign",JSON.toJSONString(keyPd));
                JSONObject signJsonObj = JSONObject.parseObject(signJsonObjStr);
                Map<String, Object> paramentMap =new HashMap<String, Object>();
                paramentMap.put("publickey",keyJsonObj.getJSONObject("result").getString("publicKey"));
                paramentMap.put("data",Base64.getBase64((JSON.toJSONString(data))));
                paramentMap.put("sign",signJsonObj.getString("result"));
                String result = HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_to_sys", JSON.toJSONString(paramentMap));
                JSONObject json = JSON.parseObject(result);
                if(StringUtil.isNotEmpty(json.getString("result"))){
                    System.out.println("result="+result);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
