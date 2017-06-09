package com.ecochain.ledger.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.BlockHashService;
import com.ecochain.ledger.util.Logger;
@Component("blockHashService")
public class BlockHashServiceImpl implements BlockHashService {
    
//    private final Logger logger = Logger.getLogger(AccDetailServiceImpl.class);
    @Resource(name = "daoSupport")
    private DaoSupport dao;
    
    @Override
    public boolean insert(List<PageData> pd) throws Exception {
        return (Integer)dao.save("BlockHashMapper.insert", pd)>0;
    }

    @Override
    public Integer getMaxHeight() throws Exception {
        return (Integer)dao.findForObject("BlockHashMapper.getMaxHeight", null);
    }

    @Override
    public List<PageData> getBlockList10() throws Exception {
        return (List<PageData>)dao.findForList("BlockHashMapper.getBlockList10", null);
    }

}
