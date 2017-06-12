package com.ecochain.ledger.service;

import java.util.List;

import com.ecochain.ledger.model.PageData;

public interface BlockHashService {
    /**
     * @describe:插入区块hash
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:11:27
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean insert(List<PageData> blockList) throws Exception;
    /**
     * @describe:huoqu
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:11:42
     * @throws Exception
     * @return: Integer
     */
    Integer getMaxHeight() throws Exception;
    /**
     * @describe:获取最新的10个区块
     * @author: zhangchunming
     * @date: 2017年6月9日下午2:28:56
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getBlockList10()throws Exception;
}
