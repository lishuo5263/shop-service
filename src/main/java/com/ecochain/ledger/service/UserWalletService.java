package com.ecochain.ledger.service;


import com.ecochain.ledger.model.PageData;

public interface UserWalletService {
    boolean deleteById(Integer id ,String versionNo) throws Exception;

    boolean insert(PageData pd ,String versionNo) throws Exception;

    boolean insertSelective(PageData pd ,String versionNo) throws Exception;

    PageData selectById(Integer id ,String versionNo) throws Exception;

    boolean updateByIdSelective(PageData pd ,String versionNo) throws Exception;

    boolean updateById(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:查询钱包账户
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:09:21
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    PageData getWalletByUserId(String user_id ,String versionNo) throws Exception;
    /**
     * @describe:转账
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:57:05
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean transferAccount(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:从钱包减钱（三界宝、三界通、人民币）
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:59:45
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateSub(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:通过账号从钱包减钱（三界宝、三界通、人民币）
     * @author: zhangchunming
     * @date: 2016年12月9日上午10:07:31
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateSubByAccount(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:向钱包加钱（三界宝、三界通、人民币）
     * @author: zhangchunming
     * @date: 2016年11月1日下午7:59:45
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateAdd(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:通过三界通支付(商城支付)
     * @author: zhangchunming
     * @date: 2016年11月10日下午8:37:57
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNowBySJT(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:店铺爆品支付(通过三界石支付)
     * @author: zhangchunming
     * @date: 2016年11月24日下午5:13:13
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payStoreBySJT(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:通过人民币支付
     * @author: zhangchunming
     * @date: 2016年11月10日下午8:37:57
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNowByRMB(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:确认收货扣除冻结三界通
     * @author: zhangchunming
     * @date: 2016年11月10日下午9:11:04
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNowByFrozeSJT(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:店铺确认消费扣除冻结人民币
     * @author: zhangchunming
     * @date: 2016年11月10日下午9:11:04
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNowByFrozeRMB(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:增加冻结金额给供应商
     * @author: zhangchunming
     * @date: 2016年11月13日上午11:12:06
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean addFrozeMoneyToSupplier(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:增加冻结三界石给店铺
     * @author: zhangchunming
     * @date: 2016年11月24日下午5:27:18
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean addFrozeWlbToStore(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:增加人民币金额给供应商
     * @author: zhangchunming
     * @date: 2016年11月13日上午11:12:53
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean addMoneyToSupplier(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:会员店铺爆品消费，确认消费给店铺打钱
     * @author: zhangchunming
     * @date: 2016年11月24日下午8:22:03
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean addWlbToStore(PageData pd ,String versionNo) throws Exception;
    /**
     * @describe:转账中
     * @author: zhangchunming
     * @date: 2016年11月17日上午10:32:48
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean transfering(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:转账成功
     * @author: zhangchunming
     * @date: 2016年11月17日上午10:33:02
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean transferSuccess(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:转账失败
     * @author: zhangchunming
     * @date: 2016年11月17日上午10:33:12
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean transferfail(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:商城支付時先判断供应商信息是否存在
     * @author: zhangchunming
     * @date: 2016年11月22日下午4:14:58
     * @param shop_order_no
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean isExistSupplierInfo(String shop_order_no,String versionNo) throws Exception;
    /**
     * @describe:店铺爆品支付时，先查询店铺钱包信息是否存在
     * @author: zhangchunming
     * @date: 2016年11月24日下午5:24:27
     * @param store_order_sn
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean isExistStoreInfo(String store_order_sn,String versionNo) throws Exception;
    /**
     * @describe:根据账号查询钱包
     * @author: zhangchunming
     * @date: 2016年12月9日上午9:36:39
     * @param account
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    public PageData getWalletByAccount(String account,String versionNo) throws Exception;
    
    /**
     * @describe:三界石兑换人民币
     * @author: zhangchunming
     * @date: 2016年12月20日下午2:09:51
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean exchangeSJS2RMB(PageData pd)throws Exception;
    
    /**
     * @describe:人民币兑换三界石
     * @author: zhangchunming
     * @date: 2016年12月20日下午2:09:51
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean exchangeRMB2SJS(PageData pd)throws Exception;
    
    /******************************************提现模块**************start******************************************/
    /**
     * @describe:提现申请成功扣除余额到冻结余额
     * @author: zhangchunming
     * @date: 2016年12月21日下午6:47:33
     * @throws Exception
     * @return: boolean
     */
    boolean withDrawalSubMoney(PageData pd)throws Exception;
    /**
     * @describe:提现成功扣除冻结人民币余额
     * @author: zhangchunming
     * @date: 2016年12月21日下午6:38:16
     * @throws Exception
     * @return: boolean
     */
    boolean withDrawalSubFrozeMoney()throws Exception;
    /**
     * @describe:提现失败时金额加回去
     * @author: zhangchunming
     * @date: 2016年12月21日下午6:46:45
     * @return
     * @throws Exception
     * @return: boolean
     */
    boolean withDrawalAddMoney()throws Exception;
    /**
     * @describe:通过合链币支付
     * @author: zhangchunming
     * @date: 2017年5月24日上午11:48:45
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNowByHLB(PageData pd ,String versionNo) throws Exception;
    /******************************************提现模块**************end******************************************/
}