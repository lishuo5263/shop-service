package com.ecochain.ledger.service;

import java.util.List;

import com.ecochain.ledger.model.Page;
import com.ecochain.ledger.model.PageData;

public interface AccDetailService {
    boolean insertSelective(PageData pd,String versionNo) throws Exception;

    boolean insertStoreDownReturn(PageData pd,String versionNo) throws Exception;

    PageData selectById(Integer id,String versionNo) throws Exception;

    boolean updateByIdSelective(PageData pd,String versionNo) throws Exception;

    /**
     * @describe:分页查询我的账房
     * @author: zhangchunming
     * @date: 2016年10月27日下午2:15:40
     * @param page
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData listPageAcc(Page page, String versionNo) throws Exception;
    /**
     * @describe:区块交易记录
     * @author: zhangchunming
     * @date: 2017年4月7日上午10:40:21
     * @param page
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData listPageTradeData(Page page,String versionNo) throws Exception;
    /**
     * @describe:查询符合条件的所有账户信息
     * @author: zhangchunming
     * @date: 2016年10月27日下午2:17:24
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getAccList(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:金额合计
     * @author: zhangchunming
     * @date: 2016年10月27日下午3:01:01
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData getSubTotal(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:查询账户类型
     * @author: zhangchunming
     * @date: 2016年10月27日下午5:07:21
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getAccTypeList(PageData pd,String versionNo) throws Exception;

    /**
     * @describe:账户汇总
     * @author: zhangchunming
     * @date: 2016年11月13日下午4:27:39
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean accDetailSummary(PageData pd,String versionNo) throws Exception;

    /**
     * @describe:获取未统计的hash集合
     * @author: zhangchunming
     * @date: 2017年3月20日下午2:30:21
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    List<PageData> getHashList(PageData pd,String versionNo) throws Exception;

    /**
     * @describe:账户汇总更新统计标识
     * @author: zhangchunming
     * @date: 2016年11月13日下午4:28:34
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateCntflag(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:插入供应商销售业绩账户流水
     * @author: zhangchunming
     * @date: 2016年11月29日下午12:57:25
     * @param shop_order_no
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean addSupplierSalesAchievement(String shop_order_no,String versionNo) throws Exception;
    /**
     * @describe:转三界石记录
     * @author: zhangchunming
     * @date: 2016年12月21日上午10:31:16
     * @param page
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    PageData listPageTransferSJS(Page page ,String versionNo) throws Exception;
    /**
     * @describe:添加提现账户流水
     * @author: zhangchunming
     * @date: 2016年12月21日下午2:23:58
     * @throws Exception
     * @return: boolean
     */
    boolean addWithDrawalAccDetail()throws Exception;

    /**
     * @describe:商城支付模块，根据hash更新订单支付状态
     * @author: zhangchunming
     * @date: 2017年3月20日下午3:26:08
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean updatePayByHash(PageData pd ,String versionNo)throws Exception;
    /**
     * @describe:充值模块，根据hash更新充值状态
     * @author: zhangchunming
     * @date: 2017年3月20日下午3:44:37
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean updateRechargeByHash(PageData pd ,String versionNo)throws Exception;
    /**
     * @describe:根据hash更新统计标志和状态
     * @author: zhangchunming
     * @date: 2017年3月20日下午4:18:30
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateCntflagByHash(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:积分充值汇总
     * @author: zhangchunming
     * @date: 2017年3月22日下午1:45:32
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean accDetailHashSummary(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:分页查询账户流水
     * @author: zhangchunming
     * @date: 2017年5月24日下午7:58:05
     * @param pd
     * @throws Exception
     * @return: PageData
     */
    List<PageData> listPageAcc(PageData pd) throws Exception;
}
