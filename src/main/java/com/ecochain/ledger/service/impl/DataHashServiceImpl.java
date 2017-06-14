package com.ecochain.ledger.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.DataHashService;
@Component("dataHashService")
public class DataHashServiceImpl implements DataHashService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;
    
    @Override
    public boolean insert(PageData pd) throws Exception {
        return (Integer)dao.save("DataHashMapper.insert", pd)>0;
    }

    @Override
    public List<String> getHashList50() throws Exception {
        return (List<String>)dao.findForList("DataHashMapper.getHashList50", null);
    }

    @Override
    public List<PageData> getDataList(Integer rows) throws Exception {
        return (List<PageData>)dao.findForList("DataHashMapper.getDataList", rows);
    }

}
