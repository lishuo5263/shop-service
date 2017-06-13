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
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.HttpTool;

/**
 * Created by Lisandro on 2017/5/17.
 */
@Component
@EnableScheduling
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

    

    @Scheduled(fixedDelay=5000)
    public void scheduler()throws  Exception {
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "100";
            String result = HttpTool.doPost(kql_url+"/GetBlockList", rows);
            
            Integer maxHeight = blockHashService.getMaxHeight();
            
            JSONObject blockData = JSONObject.parseObject(result);
            JSONArray blockArray  = blockData.getJSONArray("result");
            List<PageData> blockList = new ArrayList<PageData>();
            for(Object block :blockArray ){
                JSONObject blockJson = (JSONObject)block;
                /*String blockHash = "\""+blockJson.getString("blockHash")+"\"";
                String result1 = HttpTool.doPost(kql_url+"/GetBlockDetail", blockHash);
                JSONObject blockDetail = JSONObject.parseObject(result1);
                
                if(blockList.size()>=10){
                    break;
                }
                try {
                    if(blockDetail.getJSONObject("result").getJSONArray("qtx").size()>0){
                        if(maxHeight == null||blockDetail.getJSONObject("result").getInteger("blockHeight")>maxHeight){
                            PageData pd = new PageData();
                            pd.put("block_hash", blockJson.getString("blockHash"));
                            pd.put("trade_num", blockDetail.getJSONObject("result").getJSONArray("qtx").size());
                            pd.put("block_height", blockDetail.getJSONObject("result").getInteger("blockHeight"));
                            blockList.add(pd);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("--------GetBlockList------查询区块详细的接口未更新--------------");
                    break;
//                e.printStackTrace();
                }*/
                if(maxHeight == null||blockJson.getInteger("blockHeight")>maxHeight){
                    PageData pd = new PageData();
                    pd.put("block_hash", blockJson.getString("blockHash"));
                    pd.put("trade_num", blockJson.getInteger("records"));
                    pd.put("block_height", blockJson.getInteger("blockHeight"));
                    blockList.add(pd);
                }
            }
            if(blockList.size()>0){
                blockHashService.insert(blockList);
//                JedisUtil.set("blockList", blockList, 60*30);
//                List<JSONObject> block = (List<JSONObject>)JedisUtil.get("blockList");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
