package com.ecochain.ledger.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tk.mybatis.mapper.util.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.mapper.FabricBlockInfoMapper;
import com.ecochain.ledger.model.FabricBlockInfo;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.BlockHashService;
import com.ecochain.ledger.service.DataHashService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.HttpTool;
import com.ecochain.ledger.util.Logger;
/**
 * 账户控制类
 * @author zhangchunming
 */
@RestController
@RequestMapping(value = "/api/block")
@Api(value = "账户管理")
public class BlockDataWebService extends BaseWebService{
    
    private final Logger logger = Logger.getLogger(BlockDataWebService.class);
    
    @Autowired
    private SysGenCodeService sysGenCodeService;
    @Autowired
    private BlockHashService blockHashService;
    @Autowired
    private DataHashService dataHashService;
    @Autowired
    private FabricBlockInfoMapper fabricBlockInfoMapper;


    /**
     * @describe:获取最新的记录数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:35:14
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getDataList")
    @ApiOperation(nickname = "获取最新的记录数据", value = "获取最新的记录数据", notes = "获取最新的记录数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataList(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
            String result = HttpTool.doPost(kql_url+"/GetDataList", rows);
            JSONObject blockData = JSONObject.parseObject(result);
            JSONArray blockArray = blockData.getJSONArray("result");
            for(Object trade:blockArray){
                JSONObject tradeJSON = (JSONObject)trade;
                String dataStr = Base64.getFromBase64(tradeJSON.getString("data"));
                tradeJSON.put("data", JSONObject.parseObject(dataStr));
            }
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:查询区块列表
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:37:37
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockList")
    @ApiOperation(nickname = "获取最新的区块数据", value = "获取最新的区块数据", notes = "获取最新的区块数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询区块个数", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockList(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockList", rows);
            JSONObject blockData = JSONObject.parseObject(result);
            JSONArray blockArray  = blockData.getJSONArray("result");
            for(Object block :blockArray ){
                JSONObject blockJson = (JSONObject)block;
                blockJson.put("generateTime",DateUtil.stampToDate(blockJson.getString("generateTime")));
            }
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:按日期查询区块数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:39:44
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockByDate")
    @ApiOperation(nickname = "按日期查询区块数据", value = "按日期查询区块数据", notes = "按日期查询区块数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "date", value = "查询日期，请求样例：\"2017-05-25 12:30:10\"需带双引号", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockByDate(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String date = "\""+DateUtil.getCurrDateTime()+"\"";
            if(pd.getString("date")!=null){
                date = pd.getString("date");
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockByDate", date);
            JSONObject blockData = JSONObject.parseObject(result);
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据hash值查询交易数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午12:45:14
     * @param request
     * @return: AjaxResponse
     */
    /*@PostMapping("/getDataByHash")
    @ApiOperation(nickname = "根据hash值查询交易数据", value = "根据hash值查询交易数据", notes = "根据hash值查询交易数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "hash值，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataByHash(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入hash值！");
                ar.setSuccess(false);
                return ar;
            }
            
            String hash = pd.getString("hash");
            int length = pd.getString("hash").length();
            if(!(hash.substring(0, 1)+hash.substring(length-1, length)).equals("\"\"")){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("参数格式错误，需带双引号！");
                ar.setSuccess(false);
                return ar;
            }
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/get_data_from_sys", pd.getString("hash"));
            JSONObject blockData = JSONObject.parseObject(result);
            String dataStr = Base64.getFromBase64(blockData.getJSONObject("result").getString("data"));
            blockData.getJSONObject("result").put("data", JSONObject.parseObject(dataStr));
            data.put("result", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    
    @PostMapping("/getDataByHash")
    @ApiOperation(nickname = "根据hash值查询交易数据", value = "根据hash值查询交易数据", notes = "根据hash值查询交易数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "hash值，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataByHash(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入hash值！");
                ar.setSuccess(false);
                return ar;
            }
            
            String hash = pd.getString("hash");
            int length = pd.getString("hash").length();
            if(!(hash.substring(0, 1)+hash.substring(length-1, length)).equals("\"\"")){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("参数格式错误，需带双引号！");
                ar.setSuccess(false);
                return ar;
            }
           /* String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/get_data_from_sys", pd.getString("hash"));
            JSONObject blockData = JSONObject.parseObject(result);
            String dataStr = Base64.getFromBase64(blockData.getJSONObject("result").getString("data"));
            blockData.getJSONObject("result").put("data", JSONObject.parseObject(dataStr));*/
            
            String fabric_hash = pd.getString("hash").replaceAll("\"", "");
            FabricBlockInfo blockInfo = fabricBlockInfoMapper.getBlockByFabricHash(fabric_hash);
            if(blockInfo == null){
                ar.setData(null);
                ar.setSuccess(true);
                return ar;
            }
            JSONObject blockData = new JSONObject();
            JSONObject result = new JSONObject();
            result.put("data", blockInfo.getHashData());
            blockData.put("result", result);
            data.put("result", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    /**
     * @describe:根据区块hash值查询区块高度
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:55:23
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockHeight")
    @ApiOperation(nickname = "根据区块hash值查询区块高度", value = "根据区块hash值查询区块高度", notes = "根据区块hash值查询区块高度")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "区块hash值，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockHeight(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块hash值！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockHeight", pd.getString("hash"));
            JSONObject blockData = JSONObject.parseObject(result);
            data.put("result", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据区块高度获取区块的Hash
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:57:45
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockHashByHe")
    @ApiOperation(nickname = "根据区块高度获取区块的Hash", value = "根据区块高度获取区块的Hash", notes = "根据区块高度获取区块的Hash")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "height", value = "区块高度", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockHashByHe(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("height"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块高度！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockHashByHe", pd.getString("height"));
            JSONObject blockData = JSONObject.parseObject(result);
            data.put("result", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据区块哈希查询区块详细
     * @author: zhangchunming
     * @date: 2017年6月5日下午2:03:20
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockDetail")
    @ApiOperation(nickname = "根据区块hash查询区块详细", value = "根据区块hash查询区块详细", notes = "根据区块hash查询区块详细")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "区块hash，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockDetail(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块hash！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockDetail", pd.getString("hash"));
            JSONObject blockData = JSONObject.parseObject(result);
            data.put("result", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:获取最新的若干区块数据
     * @author: zhangchunming
     * @date: 2017年6月7日下午7:58:50
     * @param request
     * @return: AjaxResponse
     */
    /*@PostMapping("/getBlockChain")
    @ApiOperation(nickname = "获取最新的若干区块数据", value = "获取最新的若干区块数据", notes = "获取最新的若干区块数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "获取最新的若干区块数据", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockChain(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
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
                blockJson.put("generateTime",DateUtil.stampToDate(blockJson.getString("generateTime")));
            }
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    /*@PostMapping("/getDataList10")
    @ApiOperation(nickname = "获取最新的记录数据", value = "获取最新的记录数据", notes = "获取最新的记录数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataList10(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
            String result = HttpTool.doPost(kql_url+"/GetDataList", rows);
            JSONObject blockData = JSONObject.parseObject(result);
            JSONArray blockArray = blockData.getJSONArray("result");
            List<JSONObject> removelist = new ArrayList<JSONObject>();
            for(int i = 0;i<blockArray.size();i++){
                JSONObject tradeJSON = (JSONObject)blockArray.get(i);
                String dataStr = Base64.getFromBase64(tradeJSON.getString("data"));
                JSONObject jsonData = null;
                try {
                    jsonData = JSONObject.parseObject(dataStr);
                } catch (Exception e) {
                    System.out.println("不是一个json字符串");
                    removelist.add(tradeJSON);
                    continue;
                }
                if(jsonData==null){
                    continue;
                }
                if("insertOrder".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "提交订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价："+jsonData.getString("payPrice")+" HLC,总金额："+new BigDecimal(String.valueOf(jsonData.get("payPrice"))).multiply(new BigDecimal(jsonData.getString("goodsNumber")))+" HLC，订单状态：待支付");
                    jsonData.put("create_time", jsonData.getString("addTime"));
                    jsonData.put("user_name", jsonData.getString("userName"));
                }else if("payNow".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "ecoPay支付,订单号："+jsonData.getString("order_no")+",商品名称："+jsonData.getString("remark1")+",支付金额："+jsonData.get("order_amount")+" HLC,订单状态：已支付");
                }else if("deliverGoods".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "发货，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流公司："+jsonData.getString("logistics_name")+",订单状态：已发货");
                }else if("innerTransferLogisticss".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "国内物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("outerTransferLogisticss".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "境外物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("confirmReceipt".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "确认收货，订单号："+jsonData.getString("shop_order_no")+",卖家"+jsonData.getString("supplier_user_name")+"收到"+jsonData.getString("order_amount")+" HLC");
                }else if("transferAccount".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "转HLC，订单号："+jsonData.getString("flowno")+",对方账户："+jsonData.getString("revbankaccno")+",转账金额："+jsonData.getString("coin_amnt")+" HLC");
                }else if("currencyExchange".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "币种兑换，订单号："+jsonData.getString("flowno")+",兑换数量："+jsonData.getString("exchange_num")+" HLC,单价："+jsonData.getString("coin_rate")+" RMB,兑换金额："+jsonData.getBigDecimal("rmb_amnt")+" RMB");
                }else{
                    continue;
                }
                logger.info("-------------------bussType="+jsonData.getString("bussType"));
                if(jsonData.get("create_time")!=null&&jsonData.getString("create_time").length()>10){
                    jsonData.put("create_time", DateUtil.dateToStamp(jsonData.getString("create_time")));
                }
                tradeJSON.put("data", jsonData);
            }
            blockArray.removeAll(removelist);
            if(blockArray.size()>0){
                data.put("list", blockData);
            }else{
                data.put("list", null);
            }
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    /**
     * @describe:获取前10条数据
     * @author: zhangchunming
     * @date: 2017年6月8日上午10:28:31
     * @param request
     * @return: AjaxResponse
     */
    /*@PostMapping("/getDataList10")
    @ApiOperation(nickname = "获取最新的记录数据", value = "获取最新的记录数据", notes = "获取最新的记录数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataList10(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(pd.getRows()==null){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入查询条数!");
                ar.setSuccess(false);
                return ar;
            }
            List<PageData> dataList10 = dataHashService.getDataList(pd.getRows());
            List<PageData> removelist = new ArrayList<PageData>();
            for(int i = 0;i<dataList10.size();i++){
                PageData tradeJSON = (PageData)dataList10.get(i);
                String dataStr = Base64.getFromBase64(tradeJSON.getString("data"));
                JSONObject jsonData = null;
                try {
                    jsonData = JSONObject.parseObject(dataStr);
                } catch (Exception e) {
                    System.out.println("不是一个json字符串");
                    removelist.add(tradeJSON);
                    continue;
                }
                if(jsonData==null){
                    continue;
                }
                if("insertOrder".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "提交了订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价：2 HLC,总金额："+new BigDecimal(String.valueOf(jsonData.get("payPrice"))).multiply(new BigDecimal(jsonData.getString("goodsNumber")))+" HLC，订单状态：待支付");
//                    jsonData.put("describe", "提交了订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价：2 HLC,总金额："+jsonData.getString("payPrice")+" HLC，订单状态：待支付");
                    jsonData.put("create_time", jsonData.getString("addTime"));
                    jsonData.put("user_name", jsonData.getString("userName"));
                }else if("payNow".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "进行了商品支付,订单号："+jsonData.getString("order_no")+",商品名称："+jsonData.getString("remark1")+",支付金额："+jsonData.get("order_amount")+" HLC,订单状态：已支付");
                }else if("deliverGoods".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "将商品发货了，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流公司："+jsonData.getString("logistics_name")+",订单状态：已发货");
                }else if("innerTransferLogisticss".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "编辑了物流信息，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("outerTransferLogisticss".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "编辑了物流信息，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("confirmReceipt".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "确认收货了，订单号："+jsonData.getString("shop_order_no")+",卖家"+jsonData.getString("supplier_user_name")+"收到"+jsonData.getString("order_amount")+" HLC");
                    jsonData.put("describe", "确认收货了，订单号："+jsonData.getString("shop_order_no"));
                }else if("transferAccount".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "转HLC，订单号："+jsonData.getString("flowno")+",对方账户："+jsonData.getString("revbankaccno")+",转账金额："+jsonData.getString("coin_amnt")+" HLC");
                }else if("currencyExchange".equals(jsonData.getString("bussType"))){
                    jsonData.put("describe", "进行了币种兑换，订单号："+jsonData.getString("flowno")+",兑换数量："+jsonData.getString("exchange_num")+" HLC,单价："+jsonData.getString("coin_rate")+" RMB,兑换金额："+jsonData.getBigDecimal("rmb_amnt")+" RMB");
                }else{
                    continue;
                }
                logger.info("-------------------bussType="+jsonData.getString("bussType"));
                if(jsonData.get("create_time")!=null&&jsonData.getString("create_time").length()>10){
                    jsonData.put("create_time", DateUtil.dateToStamp(jsonData.getString("create_time")));
                }
                tradeJSON.put("data", jsonData);
            }
            dataList10.removeAll(removelist);
            if(dataList10.size()>0){
                Collections.sort(dataList10, new Comparator<PageData>(){  
                    
                      
                     * int compare(PageData o1, PageData o2) 返回一个基本类型的整型，  
                     * 返回负数表示：o1 小于o2，  
                     * 返回0 表示：o1和o2相等，  
                     * 返回正数表示：o1大于o2。  
                       
                    public int compare(PageData o1, PageData o2) {  
                      
                        //按照时间降序
                        if(Integer.valueOf(((JSONObject)o1.get("data")).getString("create_time")) < Integer.valueOf(((JSONObject)o2.get("data")).getString("create_time"))){  
                            return 1;  
                        }  
                        if(Integer.valueOf(((JSONObject)o1.get("data")).getString("create_time")) == Integer.valueOf(((JSONObject)o2.get("data")).getString("create_time"))){  
                            return 0;  
                        }  
                        return -1;  
                    }  
                });
                data.put("list", dataList10);
            }else{
                data.put("list", null);
            }
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    @PostMapping("/getDataList10")
    @ApiOperation(nickname = "获取最新的记录数据", value = "获取最新的记录数据", notes = "获取最新的记录数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataList10(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(pd.getRows()==null){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入查询条数!");
                ar.setSuccess(false);
                return ar;
            }
            List<FabricBlockInfo> dataList10 = fabricBlockInfoMapper.getDataList10();
//            List<PageData> dataList10 = dataHashService.getDataList(pd.getRows());
            List<FabricBlockInfo> removelist = new ArrayList<FabricBlockInfo>();
            List<PageData> dataList = new ArrayList<PageData>();
            for(int i = 0;i<dataList10.size();i++){
                PageData blockInfo = new PageData();
                String dataStr = dataList10.get(i).getHashData();
                JSONObject jsonData = null;
                try {
                    jsonData = JSONObject.parseObject(dataStr);
                } catch (Exception e) {
                    System.out.println("不是一个json字符串");
                    removelist.add(dataList10.get(i));
                    continue;
                }
                if(jsonData==null){
                    continue;
                }
                if("insertShopOrder".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "提交了订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价：2 HLC,总金额："+new BigDecimal(String.valueOf(jsonData.get("payPrice"))).multiply(new BigDecimal(jsonData.getString("goodsNumber")))+" HLC，订单状态：待支付");
//                    jsonData.put("describe", "提交了订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价：2 HLC,总金额："+jsonData.getString("payPrice")+" HLC，订单状态：待支付");
                    jsonData.put("create_time", jsonData.getString("addTime"));
                    jsonData.put("user_name", jsonData.getString("userName"));
                }else if("payNow".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "进行了商品支付,订单号："+jsonData.getString("order_no")+",商品名称："+jsonData.getString("remark1")+",支付金额："+jsonData.get("order_amount")+" HLC,订单状态：已支付");
                }else if("deliverGoods".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "将商品发货了，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流公司："+jsonData.getString("logistics_name")+",订单状态：已发货");
                }else if("innerTransferLogisticss".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "编辑了物流信息，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("outerTransferLogisticss".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "编辑了物流信息，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                }else if("confirmReceipt".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "确认收货了，订单号："+jsonData.getString("shop_order_no"));
                }else if("transferAccount".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "转HLC，订单号："+jsonData.getString("flowno")+",对方账户："+jsonData.getString("revbankaccno")+",转账金额："+jsonData.getString("coin_amnt")+" HLC");
                }else if("currencyExchange".equals(dataList10.get(i).getFabricBussType())){
                    jsonData.put("describe", "进行了币种兑换，订单号："+jsonData.getString("flowno")+",兑换数量："+jsonData.getString("exchange_num")+" HLC,单价："+jsonData.getString("coin_rate")+" RMB,兑换金额："+jsonData.getBigDecimal("rmb_amnt")+" RMB");
                }else{
                    continue;
                }
                logger.info("-------------------bussType="+dataList10.get(i).getFabricBussType());
                if(jsonData.get("create_time")!=null&&jsonData.getString("create_time").length()>10){
                    jsonData.put("create_time", DateUtil.dateToStamp(jsonData.getString("create_time")));
                }
                blockInfo.put("create_time", DateUtil.stampToDate1(String.valueOf(dataList10.get(i).getCreateTime().getTime())));
                blockInfo.put("id", dataList10.get(i).getId());
                blockInfo.put("hash", dataList10.get(i).getFabricHash());
                blockInfo.put("data", jsonData);
                dataList.add(blockInfo);
            }
//            dataList10.removeAll(removelist);
            if(dataList.size()>0){
                Collections.sort(dataList, new Comparator<PageData>(){  
                    
                    /*  
                     * int compare(PageData o1, PageData o2) 返回一个基本类型的整型，  
                     * 返回负数表示：o1 小于o2，  
                     * 返回0 表示：o1和o2相等，  
                     * 返回正数表示：o1大于o2。  
                     */  
                    public int compare(PageData o1, PageData o2) {  
                      
                        //按照时间降序
                        if(Integer.valueOf(((JSONObject)o1.get("data")).getString("create_time")) < Integer.valueOf(((JSONObject)o2.get("data")).getString("create_time"))){  
                            return 1;  
                        }  
                        if(Integer.valueOf(((JSONObject)o1.get("data")).getString("create_time")) == Integer.valueOf(((JSONObject)o2.get("data")).getString("create_time"))){  
                            return 0;  
                        }  
                        return -1;  
                    }  
                });
                data.put("list", dataList);
            }else{
                data.put("list", null);
            }
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:获取前10条数据
     * @author: zhangchunming
     * @date: 2017年6月8日上午10:28:31
     * @param request
     * @return: AjaxResponse
     */
    /*@PostMapping("/getBlockList10")
    @ApiOperation(nickname = "获取最新的区块列表", value = "获取最新的区块列表", notes = "获取最新的区块列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockList10(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd  = new PageData();
        pd = this.getPageData();
        try {
//            String object = (String)JedisUtil.get("blockList");
//            List<JSONObject> blockList = (List<JSONObject>)JedisUtil.get("blockList");
            if(pd.getRows() == null){
                ar.setSuccess(false);
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入查询条数！");
                return ar;
            }
            List<PageData> blockList = blockHashService.getBlockList10(pd.getRows());
            data.put("blockList", blockList);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    
    @PostMapping("/getBlockList10")
    @ApiOperation(nickname = "获取最新的区块列表", value = "获取最新的区块列表", notes = "获取最新的区块列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockList10(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd  = new PageData();
        pd = this.getPageData();
        try {
//            String object = (String)JedisUtil.get("blockList");
//            List<JSONObject> blockList = (List<JSONObject>)JedisUtil.get("blockList");
            if(pd.getRows() == null){
                ar.setSuccess(false);
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入查询条数！");
                return ar;
            }
//            List<PageData> blockList = blockHashService.getBlockList10(pd.getRows());
            List<FabricBlockInfo> dataList10 = fabricBlockInfoMapper.getDataList10();
            List<PageData> blockList = new ArrayList<PageData>();
            for(FabricBlockInfo fabricBlockInfo:dataList10){
                PageData block = new PageData();
                block.put("block_create_time", fabricBlockInfo.getCreateTime());
                block.put("block_height", fabricBlockInfo.getFabricBlockHeight());
                block.put("trade_num", 1);
                block.put("block_hash", fabricBlockInfo.getFabricBlockHash());
                blockList.add(block);
            }
            data.put("blockList", blockList);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据区块hash查询交易信息
     * @author: zhangchunming
     * @date: 2017年6月8日下午4:07:11
     * @param request
     * @return
     * @return: AjaxResponse
     */
    /*@PostMapping("/getDataByBlockHash")
    @ApiOperation(nickname = "根据区块hash查询交易信息", value = "根据区块hash查询交易信息", notes = "根据区块hash查询交易信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "区块hash，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataByBlockHash(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块hash！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/GetBlockDetail", pd.getString("hash"));
            JSONObject blockDetail = JSONObject.parseObject(result);
            JSONArray dataArray = new JSONArray();
            for(Object trade:blockDetail.getJSONObject("result").getJSONArray("qtx")){
                String dataHash = "\""+(String)trade+"\"";
                String result1 = HttpTool.doPost(kql_url+"/get_data_from_sys", dataHash);
                JSONObject tradeJson = JSONObject.parseObject(result1);
                String dataStr = Base64.getFromBase64(tradeJson.getJSONObject("result").getString("data"));
                JSONObject jsonData = null;
                try {
                    jsonData = JSONObject.parseObject(dataStr);
                } catch (Exception e) {
                    System.out.println("不是一个json字符串");
                    continue;
                }
                if(jsonData==null){
                    continue;
                }
                if("insertOrder".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "提交订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价："+jsonData.getString("payPrice")+" HLC,总金额："+new BigDecimal(String.valueOf(jsonData.get("payPrice"))).multiply(new BigDecimal(jsonData.getString("goodsNumber")))+" HLC，订单状态：待支付");
                    jsonData.put("create_time", jsonData.getString("addTime"));
                    jsonData.put("user_name", jsonData.getString("userName"));
                    jsonData.put("payPrice", "2");
                    jsonData.remove("csessionid");
                    jsonData.remove("skuInfo");
                    jsonData.remove("userName");
                    jsonData.remove("payName");
                    jsonData.remove("skuValue");
                    jsonData.remove("addTime");
                    jsonData.remove("supplierName");
                }else if("payNow".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "ecoPay支付,订单号："+jsonData.getString("order_no")+",商品名称："+jsonData.getString("remark1")+",支付金额："+jsonData.get("order_amount")+" HLC,订单状态：已支付");
                    jsonData.remove("seeds");
                    jsonData.remove("trans_password");
                }else if("deliverGoods".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "发货，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流公司："+jsonData.getString("logistics_name")+",订单状态：已发货");
                    jsonData.remove("createtime");
                }else if("innerTransferLogisticss".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "国内物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                    jsonData.remove("logistics_hash");
                }else if("outerTransferLogisticss".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "境外物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
                    jsonData.remove("logistics_hash");
                }else if("confirmReceipt".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "确认收货，订单号："+jsonData.getString("shop_order_no")+",卖家"+jsonData.getString("supplier_user_name")+"收到"+jsonData.getString("order_amount")+" HLC");
                }else if("transferAccount".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "转HLC，订单号："+jsonData.getString("flowno")+",对方账户："+jsonData.getString("revbankaccno")+",转账金额："+jsonData.getString("coin_amnt")+" HLC");
                    jsonData.remove("caldate");
                }else if("currencyExchange".equals(jsonData.getString("bussType"))){
//                    jsonData.put("describe", "币种兑换，订单号："+jsonData.getString("flowno")+",兑换数量："+jsonData.getString("exchange_num")+" HLC,单价："+jsonData.getString("coin_rate")+" RMB,"+"兑换金额："+jsonData.getBigDecimal("rmb_amnt")+" RMB");
                    jsonData.remove("caldate");
                }else{
                    continue;
                }
                if(jsonData.get("create_time")!=null&&jsonData.getString("create_time").length()>10){
                    jsonData.put("create_time", DateUtil.dateToStamp(jsonData.getString("create_time")));
                }
                dataArray.add(jsonData);
            }
            blockDetail.getJSONObject("result").put("qtx", dataArray);
            data.put("blockDetail", blockDetail);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }  
        return ar;
    }*/
    
    
    @PostMapping("/getDataByBlockHash")
    @ApiOperation(nickname = "根据区块hash查询交易信息", value = "根据区块hash查询交易信息", notes = "根据区块hash查询交易信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "区块hash，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataByBlockHash(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块hash！");
                ar.setSuccess(false);
                return ar;
            }
            JSONObject blockDetail = new JSONObject();
            JSONArray dataArray = new JSONArray();
            String block_hash = pd.getString("hash").replaceAll("\"", "");
            FabricBlockInfo blockInfo = fabricBlockInfoMapper.getBlockByHash(block_hash);
            if(blockInfo == null){
                ar.setData(null);
                ar.setSuccess(true);
                return ar;
            }
            JSONObject jsonData = JSONObject.parseObject(blockInfo.getHashData());
            if("insertShopOrder".equals(blockInfo.getFabricBussType())){
//              jsonData.put("describe", "提交订单，订单号："+jsonData.getString("orderNo")+"，商品名称："+jsonData.getString("goodsName")+",数量："+jsonData.getString("goodsNumber")+",单价："+jsonData.getString("payPrice")+" HLC,总金额："+new BigDecimal(String.valueOf(jsonData.get("payPrice"))).multiply(new BigDecimal(jsonData.getString("goodsNumber")))+" HLC，订单状态：待支付");
              jsonData.put("create_time", jsonData.getString("addTime"));
              jsonData.put("user_name", jsonData.getString("userName"));
              jsonData.put("payPrice", "2");
              jsonData.remove("csessionid");
              jsonData.remove("skuInfo");
              jsonData.remove("userName");
              jsonData.remove("payName");
              jsonData.remove("skuValue");
              jsonData.remove("addTime");
              jsonData.remove("supplierName");
          }else if("payNow".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "ecoPay支付,订单号："+jsonData.getString("order_no")+",商品名称："+jsonData.getString("remark1")+",支付金额："+jsonData.get("order_amount")+" HLC,订单状态：已支付");
              jsonData.remove("seeds");
              jsonData.remove("trans_password");
          }else if("deliverGoods".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "发货，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流公司："+jsonData.getString("logistics_name")+",订单状态：已发货");
              jsonData.remove("createtime");
          }else if("innerTransferLogisticss".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "国内物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
              jsonData.remove("logistics_hash");
          }else if("outerTransferLogisticss".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "境外物流运转，订单号："+jsonData.getString("shop_order_no")+",物流单号："+jsonData.getString("logistics_no")+",物流信息："+jsonData.getString("logistics_msg"));
              jsonData.remove("logistics_hash");
          }else if("confirmReceipt".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "确认收货，订单号："+jsonData.getString("shop_order_no")+",卖家"+jsonData.getString("supplier_user_name")+"收到"+jsonData.getString("order_amount")+" HLC");
          }else if("transferAccount".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "转HLC，订单号："+jsonData.getString("flowno")+",对方账户："+jsonData.getString("revbankaccno")+",转账金额："+jsonData.getString("coin_amnt")+" HLC");
              jsonData.remove("caldate");
          }else if("currencyExchange".equals(jsonData.getString("bussType"))){
//              jsonData.put("describe", "币种兑换，订单号："+jsonData.getString("flowno")+",兑换数量："+jsonData.getString("exchange_num")+" HLC,单价："+jsonData.getString("coin_rate")+" RMB,"+"兑换金额："+jsonData.getBigDecimal("rmb_amnt")+" RMB");
              jsonData.remove("caldate");
          }
          if(jsonData.get("create_time")!=null&&jsonData.getString("create_time").length()>10){
              jsonData.put("create_time", DateUtil.dateToStamp(jsonData.getString("create_time")));
          }
              dataArray.add(jsonData);
              JSONObject result = new JSONObject();
              result.put("qtx", dataArray);
              blockDetail.put("result", result);
              blockDetail.put("hash", pd.getString("hash"));
            data.put("blockDetail", blockDetail);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }  
        return ar;
    }
    public static void main(String[] args) {
        System.out.println("b0d60431b719cb91de8ad563085aaaa82c157c48aaef180cf7a5a4908a6b8d86".length());
    }
}
