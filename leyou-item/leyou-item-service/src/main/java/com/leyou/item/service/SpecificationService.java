package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/24 - 18:05
 */
@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupByCid(long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);//根据某个属性进行查询
    }
    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> querySpecParamByThing(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamMapper.select(specParam);
    }

}
