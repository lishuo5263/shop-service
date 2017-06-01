package com.ecochain.ledger.service;

import java.util.List;

import com.ecochain.ledger.model.PageData;

public interface UsersDetailsService {
    
    boolean insertSelective(PageData pd ,String versionNo) throws Exception;
    
    /**
     * @describe:通过账号和密码获取数据
     * @author: zhangchunming
     * @date: 2017年3月1日下午3:50:39
     * @param pd
     * @throws Exception
     * @return: PageData
     */
    public PageData getUserByAccAndPass(PageData pd,String versionNo) throws Exception;
    
    /**
     * @describe:更新登陆时间
     * @author: zhangchunming
     * @date: 2017年3月1日下午3:49:09
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    public boolean updateLoginTimeById(PageData pd,String versionNo) throws Exception;
    
    /**
     * @describe:查询用户信息
     * @author: zhangchunming
     * @date: 2017年3月2日下午2:56:38
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    public PageData getUserInfoByUserId(Integer user_id,String versionNo) throws Exception;
    /**
     * @describe:判断用户是否存在
     * @author: zhangchunming
     * @date: 2017年3月2日下午2:56:52
     * @param account
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean findIsExist(String account,String versionNo) throws Exception;
    /**
     * @describe:用户注册
     * @author: zhangchunming
     * @date: 2017年3月2日下午4:15:00
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    public boolean addUser(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:注册用户数统计
     * @author: zhangchunming
     * @date: 2017年3月2日下午4:26:17
     * @throws Exception
     * @return: Integer
     */
    public Integer getUserCount()throws Exception;

    /**
     * @describe:根据帐号查询用户信息
     * @author: zhangchunming
     * @date: 2017年3月8日下午5:04:44
     * @param account
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    public PageData getUserInfoByAccount(String account,String versionNo) throws Exception;

    public PageData findAcceptInfo(PageData pd)throws Exception ;
    /**
     * @describe:更新用户信息
     * @author: zhangchunming
     * @date: 2017年3月9日上午10:03:35
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateByIdSelective(PageData pd,String versionNo) throws Exception;
    
    /**
     * @describe:修改手机号
     * @author: zhangchunming
     * @date: 2016年10月26日下午3:04:47
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean modifyPhone(PageData pd,String versionNo)throws Exception;
    
    public List<PageData> listPageUsers(PageData pd)throws Exception;
    
    /**
     * @describe:设置交易密码
     * @author: zhangchunming
     * @date: 2017年5月31日下午7:59:18
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean setTransPassword(PageData pd)throws Exception;
    
    /**
     * @describe:判断交易密码是否正确
     * @author: zhangchunming
     * @date: 2017年5月31日下午8:14:22
     * @param pd
     * @throws Exception
     * @return: Boolean
     */
    Boolean isExistTransPassword(PageData pd)throws Exception;
    
}
