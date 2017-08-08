package com.ecochain.ledger.Task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.BlockDataHashService;
import com.ecochain.ledger.service.BlockHashService;
import com.ecochain.ledger.service.DataHashService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.HttpTool;

/**
 * Created by Lisandro on 2017/5/17.
 */
@Component
//@EnableScheduling
public class BlockListTask {
    
    private Logger logger = Logger.getLogger(BlockListTask.class);
    
    @Value("${server.port}")
    private String servicePort;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private ShopOrderInfoService shopOrderInfoService;
    @Autowired
    private BlockDataHashService blockDataHashService;
    @Autowired
    private SysGenCodeService sysGenCodeService;
    @Autowired
    private BlockHashService blockHashService;
    @Autowired
    private DataHashService dataHashService;

    

//    @Scheduled(fixedDelay=5000)
    public void getBlockList()throws  Exception {
        try {
            logger.info("------BlockListTask.getBlockList--------同步blockList-------start-----------");
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            logger.info("------BlockListTask.getBlockList--------kql_url="+kql_url);
            String rows = "100";
            String result = HttpTool.doPost(kql_url+"/GetBlockList", rows);
            
            Integer maxHeight = blockHashService.getMaxHeight();
            logger.info("------BlockListTask.getBlockList--------maxHeight="+maxHeight);
            JSONObject blockData = JSONObject.parseObject(result);
            JSONArray blockArray  = blockData.getJSONArray("result");
            List<PageData> blockList = new ArrayList<PageData>();
            for(Object block :blockArray ){
                JSONObject blockJson = (JSONObject)block;
                if((maxHeight == null||blockJson.getInteger("blockHeight")>maxHeight)&&blockJson.getInteger("records")>0){
                    PageData pd = new PageData();
                    pd.put("block_hash", blockJson.getString("blockHash"));
                    pd.put("trade_num", blockJson.getInteger("records"));
                    pd.put("block_height", blockJson.getInteger("blockHeight"));
                    blockList.add(pd);
                }
            }
            if(blockList.size()>0){
                blockHashService.insert(blockList);
                logger.info("------BlockListTask.getBlockList--------同步blockList-------end--------blockList="+blockList.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Scheduled(fixedDelay=5000)
    public void getDataList()throws  Exception {
        try {
            logger.info("------BlockListTask.getDataList--------同步dataList-------start-----------");
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            logger.info("------BlockListTask.getDataList--------kql_url="+kql_url);
            String rows = "40";
            String result = HttpTool.doPost(kql_url+"/GetDataList", rows);
            logger.info("----------------GetDataList="+result);
            List<String> hashList50 = dataHashService.getHashList50();
            
            JSONObject dataObj = JSONObject.parseObject(result);
            JSONArray dataArray  = dataObj.getJSONArray("result");
            List<PageData> dataList = new ArrayList<PageData>();
            if(hashList50 != null&&hashList50.size()>0){
                for(int i = dataArray.size()-1 ; i >= 0 ; i-- ){
                    JSONObject dataJson = (JSONObject)dataArray.get(i);
                    if(!hashList50.contains(dataJson.getString("hash"))){
                        PageData pd = new PageData(); 
                        pd.put("hash", dataJson.getString("hash"));
                        pd.put("data", dataJson.getString("data"));
                        dataList.add(pd);
                    }
                }
            }else{
                for(int i = dataArray.size()-1 ; i >= 0 ; i-- ){
                    JSONObject dataJson = (JSONObject)dataArray.get(i);
                    PageData pd = new PageData(); 
                    pd.put("hash", dataJson.getString("hash"));
                    pd.put("data", dataJson.getString("data"));
                    dataList.add(pd);
                }
            }
            
            /*if(dataList.size()>0){
                PageData pd = new PageData();
                pd.put("dataList", dataList);
                dataHashService.insert(pd);
                logger.info("------BlockListTask.getDataList--------同步dataList-------end-----dataList="+dataList.toString());
            }*/
            for(PageData pd:dataList){
                dataHashService.insertData(pd);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
