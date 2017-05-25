package com.ecochain.ledger.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.annotation.LoginVerify;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.constants.CookieConstant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.AccDetailService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.service.SysMaxnumService;
import com.ecochain.ledger.service.UserLoginService;
import com.ecochain.ledger.service.UserWalletService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Logger;
import com.ecochain.ledger.util.RequestUtils;
import com.ecochain.ledger.util.SessionUtil;
import com.github.pagehelper.PageInfo;
/**
 * 账户控制类
 * @author zhangchunming
 */
@RestController
@RequestMapping(value = "/api/acc")
@Api(value = "账户管理")
public class AccWebService extends BaseWebService{
    
    private final Logger logger = Logger.getLogger(AccWebService.class);
    
    @Autowired
    private AccDetailService accDetailService;
    @Autowired
    private SysGenCodeService sysGenCodeService;
//    @Autowired
//    private PayOrderService payOrderService;
    @Autowired
    private UserWalletService userWalletService;
//    @Autowired
//    private UsersDetailsService usersDetailsService;
    @Autowired
    private SysMaxnumService sysMaxnumService;
    @Autowired
    private UserLoginService userLoginService;

    /**
     * @describe:查询账户列表
     * @author: zhangchunming
     * @date: 2016年11月7日下午8:02:29
     * @param request
     * @param page
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/listPageAcc")
    @ApiOperation(nickname = "账户流水", value = "账户流水", notes = "账户流水")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse listPageAcc(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            PageData pd = new PageData();
            pd = this.getPageData();
            pd.put("user_id", user.get("id"));
            //查询账户列表
            List<PageData> listPageAcc  = accDetailService.listPageAcc(pd);
            //查询小计
//            PageData subTotal = accDetailService.getSubTotal(pd, Constant.VERSION_NO);
//            data.put("subTotal", subTotal);
            data.put("pageInfo", new PageInfo<PageData>(listPageAcc));
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
    
    
    /*@LoginVerify
    @RequestMapping(value="/withdrawal", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse withdrawal(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        try {
            PageData pd = new PageData();
            pd = this.getPageData();
            logger.info("**************提现*******pd value is "+pd.toString());
            String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            String userType = user.getString("user_type");
            boolean hasWithDrawaling = payOrderService.isHasWithDrawaling(String.valueOf(user.get("id")));
            if(hasWithDrawaling){
                ar.setSuccess(false);
                ar.setMessage("您的上笔提现订单火速处理中，请等待处理完毕再进行此操作！");
                ar.setErrorCode(CodeConstant.ERROR_PROCESSING);
                return ar;
            }
            if(StringUtil.isEmpty(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("请输入提现金额");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(!Validator.isMoney2(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("提现金额格式有误，请按提示输入");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(StringUtil.isEmpty(pd.getString("pay_type"))){
                ar.setSuccess(false);
                ar.setMessage("请选择提现账号");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if("1".equals(pd.getString("pay_type"))){//1-支付宝 2-微信 3-银联  
               if(StringUtil.isEmpty(pd.getString("revbankaccno"))){//提现账号（支付宝/银行卡号）
                   ar.setSuccess(false);
                   ar.setMessage("请输入您的支付宝账号");
                   ar.setErrorCode(CodeConstant.PARAM_ERROR);
                   return ar;
               }
               if(StringUtil.isEmpty(pd.getString("name"))){//所选银行名称或者支付宝名称
                   ar.setSuccess(false);
                   ar.setMessage("请输入您的支付宝名称");
                   ar.setErrorCode(CodeConstant.PARAM_ERROR);
                   return ar;
               } 
            }else if("3".equals(pd.getString("pay_type"))){//银联提现
                if(StringUtil.isEmpty(pd.getString("revorgname"))){//所选银行名称或者支付宝名称
                    ar.setSuccess(false);
                    ar.setMessage("请选择银行");
                    ar.setErrorCode(CodeConstant.PARAM_ERROR);
                    return ar;
                }
                if(StringUtil.isEmpty(pd.getString("revbankaccno"))){//提现账号（支付宝/银行卡号）
                    ar.setSuccess(false);
                    ar.setMessage("输入您的银行卡卡号");
                    ar.setErrorCode(CodeConstant.PARAM_ERROR);
                    return ar;
                } 
                if(StringUtil.isEmpty(pd.getString("name"))){//持卡人姓名
                    ar.setSuccess(false);
                    ar.setMessage("输入持卡人真实姓名");
                    ar.setErrorCode(CodeConstant.PARAM_ERROR);
                    return ar;
                } 
            }else if("2".equals(pd.getString("pay_type"))){//微信提现
                if(StringUtil.isEmpty(pd.getString("revbankaccno"))){//提现账号（支付宝/银行卡号）
                    ar.setSuccess(false);
                    ar.setMessage("请输入您的微信账号");
                    ar.setErrorCode(CodeConstant.PARAM_ERROR);
                    return ar;
                }
            }
            
            String codeName = "";
            if("1".equals(userType)){
                ar.setSuccess(false);
                ar.setMessage("对不起，您没有提现权限");
                ar.setErrorCode(CodeConstant.ERROR_NO_WITHDRAWAL);
                return ar;
            }else if("2".equals(userType)){
                ar.setSuccess(false);
                ar.setMessage("对不起，您没有提现权限");
                ar.setErrorCode(CodeConstant.ERROR_NO_WITHDRAWAL);
                return ar;
            }else if("3".equals(userType)){//店铺
                codeName = "DP_WITHDRAWAL_LIMIT";
            }else if("4".equals(userType)){//供应商
                codeName = "DL_WITHDRAWAL_LIMIT";
            }else if("5".equals(userType)){//代理商
                codeName = "GY_WITHDRAWAL_LIMIT";
            }
            
            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            if((new BigDecimal(pd.getString("money"))).compareTo(new BigDecimal(String.valueOf(userWallet.get("money"))))>0){
                ar.setSuccess(false);
                ar.setMessage("余额不足，无法提现");
                ar.setErrorCode(CodeConstant.BALANCE_NOT_ENOUGH);
                return ar;
            }
            *//**************************提现金额上下限判断-----------start***********************//*
            //提现上限下限（现金/每次）
            String uplimit = "";
            String lowlimit = "";
            List<PageData> codeList =  sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
            for(PageData code:codeList){
                if(codeName.equals(code.get("code_name"))){
                    uplimit = String.valueOf(code.get("uplimit"));
                    lowlimit = String.valueOf(code.get("lowlimit"));
                }
            }
            if(StringUtil.isNotEmpty(uplimit)&&new BigDecimal(pd.getString("money")).compareTo(new BigDecimal(uplimit))>0){//超出上线
                ar.setSuccess(false);
                ar.setMessage("提现金额超出单次上限，请重新输入");
                ar.setErrorCode(CodeConstant.ERROR_OVER_UPLIMIT);
                return ar;
            }
            if(StringUtil.isNotEmpty(lowlimit)&&new BigDecimal(pd.getString("money")).compareTo(new BigDecimal(lowlimit))<0){
                ar.setSuccess(false);
                ar.setMessage("提现金额低于单次下限，请重新输入");
                ar.setErrorCode(CodeConstant.ERROR_LOWER_LOWLIMIT);
                return ar;
            }
            *//**************************提现金额上下限判断-----------end***********************//*
            //生成支付号
            Long tMaxno =sysMaxnumService.findMaxNo("payno", Constant.VERSION_NO);
            if(tMaxno==null){
                logger.info("payno  tSysMaxnum findMaxNo  is null!");
                ar.setSuccess(false);
                ar.setMessage("系统繁忙,请稍后重试！");
                ar.setErrorCode(CodeConstant.SYS_ERROR);
                return ar;
            }
            String pay_no =tMaxno.toString();
            
            pd.put("user_id", user.get("id"));
            pd.put("pay_no", pay_no);//工单号
            pd.put("fee_type", "2");//工单号
            pd.put("cuy_type", "3");//1-三界石2-三界宝3-人民币
            pd.put("revorgname", "3");//银行名称、微信、支付宝名称
            pd.put("revbankaccno", pd.getString("revbankaccno"));//银行、支付宝账号
            if(StringUtil.isNotEmpty(pd.getString("name"))){
                pd.put("remark2", pd.getString("name"));//持卡人姓名或支付宝姓名
            }
            pd.put("txamnt", pd.getString("money"));//1-三界石2-三界宝3-人民币
            pd.put("status", "0");//0-待审核 1-成功 2-失败
            pd.put("txdate", DateUtil.getCurrDateTime());
            pd.put("operator", user.getString("mobile_phone"));
            if(payOrderService.applyWithDrawal(pd, Constant.VERSION_NO)){
                ar.setSuccess(true);
                ar.setMessage("提现申请成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    /**
     * @describe:查询账户余额
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:20:54
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/getWallet")
    @ApiOperation(nickname = "查询账户余额", value = "查询账户余额", notes = "查询账户余额！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getWallet(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            data.put("userWallet", userWallet);
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
     * @describe:转账
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:25:39
     * @param request
     * @param response
     * @return: AjaxResponse
     *//*
    @LoginVerify
    @RequestMapping(value="/transferAccount", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse transferAccount(HttpServletRequest request,HttpServletResponse response){
        logBefore(logger, "---------转三界石----transferAccount-----------");
        AjaxResponse ar = new AjaxResponse();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            
            String userType = user.getString("user_type");
            PageData pd = new PageData();
            pd = this.getPageData();
            pd.put("revbankaccno", pd.getString("revbankaccno")==null?"":pd.getString("revbankaccno").trim());
            pd.put("money", pd.getString("money")==null?"":pd.getString("money").trim());
            if(StringUtil.isEmpty(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("请输入转账金额");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(!Validator.isMoney(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("三界石必须为整数，不能为小数");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(StringUtil.isEmpty(pd.getString("revbankaccno"))){
                ar.setSuccess(false);
                ar.setMessage("请输入对方账户");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(!usersDetailsService.findIsExist(pd.getString("revbankaccno"), Constant.VERSION_NO)){//revbankaccno对方账户即对方手机号
                ar.setSuccess(false);
                ar.setMessage("对方账户不存在，请重新输入");
                ar.setErrorCode(CodeConstant.ERROR_NO_ACCOUNT);
                return ar;
            }
            if(!userLoginService.findIsExist(pd.getString("revbankaccno"), Constant.VERSION_NO)){
                ar.setSuccess(false);
                ar.setMessage("对方账户不存在，请重新输入");
                ar.setErrorCode(CodeConstant.ERROR_NO_ACCOUNT);
                return ar;
            }
            PageData userLogin = userLoginService.getUserLoginByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            if(pd.getString("revbankaccno").equals(userLogin.getString("account"))){
                ar.setSuccess(false);
                ar.setMessage("不能转入自己账户，请重新输入");
                ar.setErrorCode(CodeConstant.ERROR_DISABLE);
                return ar;
            }
            PageData userInfo  = userLoginService.getUserInfoByAccount(pd.getString("revbankaccno"), Constant.VERSION_NO);
            if("3".equals(userInfo.getString("user_type"))){
                ar.setSuccess(false);
                ar.setMessage("对方账户是店铺会员身份，不允许转入三界石！");
                ar.setErrorCode(CodeConstant.DISABLE_TURN_IN);
                return ar;
            }
            //转账上限、下限查询
            String  uplimit = "";
            String  lowlimit = "";
            String codeName = "";
            if("1".equals(userType)){
                ar.setSuccess(false);
                ar.setMessage("抱歉，您没有转三界石权限，只有创业会员和店铺会员才有哦！");
                ar.setErrorCode(CodeConstant.ERROR_NO_TRANSFER);
                return ar;
            }else if("2".equals(userType)){
                codeName = "ZS_TRANSFER_LIMIT";
            }else if("3".equals(userType)){
                codeName = "DP_TRANSFER_LIMIT";
            }else if("4".equals(userType)){//供应商
                ar.setSuccess(false);
                ar.setMessage("抱歉，您没有转三界石权限，只有创业会员和店铺会员才有哦！");
                ar.setErrorCode(CodeConstant.ERROR_NO_TRANSFER);
                return ar;
            }else if("5".equals(userType)){//代理商
                ar.setSuccess(false);
                ar.setMessage("抱歉，您没有转三界石权限，只有创业会员和店铺会员才有哦！");
                ar.setErrorCode(CodeConstant.ERROR_NO_TRANSFER);
                return ar;
//                codeName = "DL_TRANSFER_LIMIT";
            }
            List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
            for(PageData code:codeList){
                if(codeName.equals(code.get("code_name"))){
                    uplimit = code.get("uplimit").toString();
                    lowlimit = code.get("lowlimit").toString();
                }
            }
            if(StringUtil.isNotEmpty(uplimit)&&new BigDecimal(pd.getString("money")).compareTo(new BigDecimal(uplimit))>0){
                ar.setSuccess(false);
                ar.setMessage("转账金额不能超出上限");
                ar.setErrorCode(CodeConstant.ERROR_OVER_UPLIMIT);
                return ar;
            }
            if(StringUtil.isNotEmpty(lowlimit)&&new BigDecimal(pd.getString("money")).compareTo(new BigDecimal(lowlimit))<0){
                ar.setSuccess(false);
                ar.setMessage("转账金额不能低于下限");
                ar.setErrorCode(CodeConstant.ERROR_LOWER_LOWLIMIT);
                return ar;
            }
            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            String future_currency = String.valueOf(userWallet.get("future_currency"));
            if(new BigDecimal(pd.getString("money")).compareTo(new BigDecimal(future_currency))>0){
                ar.setSuccess(false);
                ar.setMessage("您的三界石余额不足！");
                ar.setErrorCode(CodeConstant.BALANCE_NOT_ENOUGH);
                return ar;
            }
            //开始转账
            pd.put("user_id", user.get("id"));
            pd.put("user_type", userType);
            pd.put("future_currency", pd.getString("money"));
            pd.put("operator", user.getString("mobile_phone"));
            pd.put("usercode", user.getString("usercode"));
            pd.remove("money");
            //生成支付号
            Long tMaxno =sysMaxnumService.findMaxNo("payno", Constant.VERSION_NO);
            if(tMaxno==null){
                logger.info("payno  tSysMaxnum findMaxNo  is null!");
                ar.setSuccess(false);
                ar.setMessage("系统繁忙,请稍后重试！");
                ar.setErrorCode(CodeConstant.SYS_ERROR);
                return ar;
            }
            String pay_no =tMaxno.toString();
            pd.put("pay_no", pay_no);
            userWalletService.transferAccount(pd, Constant.VERSION_NO);
            ar.setSuccess(true);
            ar.setMessage("转账成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }finally{
            logAfter(logger);
        }   
        return ar;
    }*/
    /**
     * @describe:充值创建支付订单
     * @author: zhangchunming
     * @date: 2016年11月2日上午9:34:05
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    /*@LoginVerify
    @RequestMapping(value="/recharge", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse recharge(HttpServletRequest request,HttpServletResponse response){
        logBefore(logger, "充值----recharge");
        AjaxResponse ar = new AjaxResponse();
        try {
            String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);
            JSONObject user = JSONObject.parseObject(userstr);
            String userType = user.getString("user_type");//用户类型:1-普通会员 2-创业会员 3-店铺 4-供应商 5-代理商
            PageData pd = new PageData();
            pd = this.getPageData();
            
            if(StringUtil.isEmpty(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("请输入充值金额");
                return ar;
            }
            if(!Validator.isMoney(pd.getString("money"))){
                ar.setSuccess(false);
                ar.setMessage("三界石必须为整数，不能为小数");
                return ar;
            }
            if(StringUtil.isEmpty(pd.getString("pay_type"))){
                ar.setSuccess(false);
                ar.setMessage("请选择支付方式");
                return ar;
            }
            if("4".equals(user.getString("user_type"))){//供应商
                ar.setSuccess(false);
                ar.setMessage("供应商无充值权限");
                return ar;
            }
            //费率查询
            String  feeRate = "";
            String codeName = "";
            if("1".equals(userType)){
                codeName = "PT_RATE";
            }else if("2".equals(userType)){
                codeName = "ZS_RATE";
            }else if("3".equals(userType)){
                codeName = "DP_RATE";
            }else if("5".equals(userType)){
                codeName = "DL_RATE";
            }
            List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
            for(PageData code:codeList){
                if(codeName.equals(code.get("code_name"))){
                    feeRate = code.get("code_value").toString();
                }
            }
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }finally{
            logAfter(logger);
        }   
        return ar;
    }*/
    
    /**
     * @describe:跳往充值页面
     * @author: zhangchunming
     * @date: 2016年11月3日上午9:38:06
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    /*@LoginVerify
    @RequestMapping(value="/toRecharge", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse toRecharge(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        try {
            String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            String userType = user.getString("user_type");
            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            //费率查询
            String  feeRate = "";
            String codeName = "";
            if("1".equals(userType)){
                codeName = "PT_RATE";
            }else if("2".equals(userType)){
                codeName = "ZS_RATE";
            }else if("3".equals(userType)){
                codeName = "DP_RATE";
            }else if("5".equals(userType)){
                codeName = "DL_RATE";
            }
            List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);

            for(PageData code:codeList){
                if(codeName.equals(code.get("code_name")==null?"":code.getString("code_name").trim())){
                    feeRate = code.get("code_value").toString();
                }
            }
            
            data.put("user_type", userType);//会员身份
            data.put("future_currency", userWallet.get("future_currency"));//三界石余额
            data.put("feeRate", feeRate);
            ar.setData(data);
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    /**
     * @describe:跳往转账页面
     * @author: zhangchunming
     * @date: 2016年11月3日上午10:04:19
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    /*@LoginVerify
    @RequestMapping(value="/toTransferAcc", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse toTransferAcc(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        try {
            String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            String userType = user.getString("user_type");
            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            //转账上限、下限查询
            String  uplimit = "";
            String  lowlimit = "";
            String codeName = "";
            if("1".equals(userType)){
                ar.setSuccess(false);
                ar.setMessage("对不起，您没有转账权限");
                ar.setErrorCode(CodeConstant.ERROR_NO_TRANSFER);
                return ar;
            }else if("2".equals(userType)){
                codeName = "ZS_TRANSFER_LIMIT";
            }else if("3".equals(userType)){
                codeName = "DP_TRANSFER_LIMIT";
            }else if("4".equals(userType)){//供应商
                ar.setSuccess(false);
                ar.setMessage("对不起，您没有转账权限");
                ar.setErrorCode(CodeConstant.ERROR_NO_TRANSFER);
                return ar;
            }else if("5".equals(userType)){//代理商
                codeName = "DL_TRANSFER_LIMIT";
            }
            List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
            for(PageData code:codeList){
                if(codeName.equals(code.get("code_name"))){
                    uplimit = code.get("uplimit").toString();
                    lowlimit = code.get("lowlimit").toString();
                }
            }
            
            data.put("user_type", userType);//会员身份
            data.put("future_currency", userWallet.getString("future_currency"));//三界石余额
            data.put("uplimit", uplimit);
            data.put("lowlimit", lowlimit);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("转账成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }*/
    
    
    /**
     * @describe:转账时查询对方账户信息并做隐藏处理
     * @author: zhangchunming
     * @date: 2016年12月2日下午8:10:26
     * @param request
     * @param response
     * @return: AjaxResponse
     *//*
    @LoginVerify
    @RequestMapping(value="/getAccount", method=RequestMethod.POST)
    @ResponseBody
    public AjaxResponse getAccount(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            pd.put("revbankaccno", pd.getString("revbankaccno")==null?"":pd.getString("revbankaccno").trim());
            if(StringUtil.isEmpty(pd.getString("revbankaccno"))){
                ar.setSuccess(false);
                ar.setMessage("请输入对方账户");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            pd.put("account", pd.getString("revbankaccno"));
            PageData userLogin = userLoginService.getUserByAccount(pd, Constant.VERSION_NO);
            if(userLogin==null){
                ar.setMessage("对方账户不存在！");
                ar.setErrorCode(CodeConstant.ERROR_NO_ACCOUNT);
                ar.setSuccess(false);
                return ar;
            }
            PageData usersDetail = usersDetailsService.selectById((Integer)userLogin.get("user_id"), Constant.VERSION_NO);
            if(Validator.isMobile(usersDetail.getString("user_name"))){
                data.put("user_name", FormatNum.convertPhone((usersDetail.getString("user_name"))));
            }else{
                data.put("user_name", FormatNum.convertRealName(usersDetail.getString("user_name")));
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
    
    
    
}
