package com.ecochain.ledger.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tk.mybatis.mapper.util.StringUtil;

import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.annotation.LoginVerify;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.constants.CookieConstant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.AccDetailService;
import com.ecochain.ledger.service.ShopOrderGoodsService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.service.SysMaxnumService;
import com.ecochain.ledger.service.UserLoginService;
import com.ecochain.ledger.service.UserWalletService;
import com.ecochain.ledger.service.UsersDetailsService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Logger;
import com.ecochain.ledger.util.RequestUtils;
import com.ecochain.ledger.util.SessionUtil;
import com.ecochain.ledger.util.qrcode.QRCodeUtil;
/**
 * 支付控制类
 * @author zhangchunming
 */
@RestController
@RequestMapping(value = "/api/pay")
@Api(value = "支付")
public class PayWebService extends BaseWebService{
    
    private final Logger logger = Logger.getLogger(PayWebService.class);
    
    @Autowired
    private AccDetailService accDetailService;
    @Autowired
    private SysGenCodeService sysGenCodeService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private SysMaxnumService sysMaxnumService;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;
    @Autowired
    private ShopOrderInfoService shopOrderInfoService;
    @Autowired
    private UsersDetailsService usersDetailsService;

    
    /**
     * @describe:跳转支付
     * @author: zhangchunming
     * @date: 2017年5月31日下午7:46:46
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/toPay")
    @ApiOperation(nickname = "跳往支付页面", value = "跳往支付页面", notes = "跳往支付页面")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "shop_order_no", value = "订单号", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse toPay(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            if(StringUtil.isEmpty(pd.getString("shop_order_no"))){
                ar.setMessage("订单号不能为空！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setSuccess(false);
                return ar;
            }
            String goodsName = shopOrderGoodsService.getOneGoodsNameByOrderNo(pd.getString("shop_order_no"));
            PageData supplierInfo = shopOrderGoodsService.getSupplierInfoByOrderNo(pd.getString("shop_order_no"));
            pd.put("order_no", pd.getString("shop_order_no"));
            pd.put("user_id", user.get("user_id"));
            PageData shopOrderInfo = shopOrderInfoService.getShopOrderByOrderNo(pd, Constant.VERSION_NO);
            data.put("goodsName", goodsName);
            data.put("supplierInfo", supplierInfo);
            data.put("shopOrderInfo", shopOrderInfo);
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
     * @describe:生成支付二维码
     * @author: zhangchunming
     * @date: 2017年5月31日下午9:17:07
     * @param request
     * @param response
     * @return: void
     */
    @LoginVerify
    @GetMapping("/generatePayQRCode")
    @ApiOperation(nickname = "生成支付二维码", value = "生成支付二维码", notes = "生成支付二维码")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "order_no", value = "订单号", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "goods_name", value = "商品名称", required = false, paramType = "query", dataType = "String")
    })
    public void generatePayQRCode(HttpServletRequest request, HttpServletResponse response){
        String imgPath = "";
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            pd.put("user_id", String.valueOf(user.get("id")));
            PageData shopOrder = shopOrderInfoService.getShopOrderByOrderNo(pd, Constant.VERSION_NO);
            PageData supplierInfo = shopOrderGoodsService.getSupplierInfoByOrderNo(pd.getString("order_no"));
            String goodsName = shopOrderGoodsService.getOneGoodsNameByOrderNo(pd.getString("order_no"));
            String content = "order_no="+pd.getString("order_no")+"&order_amount="+String.valueOf(shopOrder.get("order_amount"))+"&goods_name="+goodsName+"&supplier_name="+supplierInfo.getString("supplier_name");
            OutputStream out = response.getOutputStream();
            QRCodeUtil.encode(content, imgPath, out, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
