package com.ecochain.ledger.Task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.model.BlockDataHash;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.BlockDataHashService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.HttpTool;
import com.ecochain.ledger.util.StringUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

    

//    @Scheduled(fixedDelay=5000)
    public void scheduler()throws  Exception {
        String kql_url =null;
        List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
        for(PageData mapObj:codeList){
            if("QKL_URL".equals(mapObj.get("code_name"))){
                kql_url = mapObj.get("code_value").toString();
            }
        }
        String rows = "100";
        String result = HttpTool.doPost(kql_url+"/GetBlockList", rows);
        JSONObject blockData = JSONObject.parseObject(result);
        JSONArray blockArray  = blockData.getJSONArray("result");
        for(Object block :blockArray ){
            JSONObject blockJson = (JSONObject)block;
            String blockHash = blockJson.getString("blockHash");
            String result1 = HttpTool.doPost(kql_url+"/GetBlockDetail", blockHash);
            JSONObject blockDetail = JSONObject.parseObject(result1);
            if(blockDetail.getJSONObject("result").getJSONArray("qtx").size()>0){
                
            }
        }
    }
}