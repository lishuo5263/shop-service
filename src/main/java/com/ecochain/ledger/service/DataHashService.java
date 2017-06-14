package com.ecochain.ledger.service;

import java.util.List;

import com.ecochain.ledger.model.PageData;

public interface DataHashService {
    /**
     * @describe:插入数据hash
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:11:27
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean insert(PageData pd) throws Exception;
    /**
     * @describe:获取50条hash
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:11:42
     * @throws Exception
     * @return: Integer
     */
    List<String> getHashList50() throws Exception;
    /**
     * @describe:获取最新的10条数据
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:28:56
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getDataList(Integer rows)throws Exception;
}
