package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Jun
 * @create 2020/6/8 - 10:37
 */
@Service
public class GoodsService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String,Object> loadData(Long spuId){
        Map<String,Object> map = new HashMap<>();
        //查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetaliBySpuId(spuId);

        //查询skus集合
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);

        //查询分类
        List<Long> ids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(ids);
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String,Object> categoryMap = new HashMap<>();
            categoryMap.put("id",ids.get(i));
            categoryMap.put("name",names.get(i));
            categories.add(categoryMap);
        }

        //查询品牌
        Brand brand = this.brandClient.queryBrandByBid(spu.getBrandId());

        //查询规格参数组
        List<SpecGroup> groups = this.specificationClient.querySpecsByCid(spu.getCid3());

        //查询特殊规格参数值
        List<SpecParam> specParams = this.specificationClient.querySpecParamByThing(null, spu.getCid3(), false, null);
        Map<Long,String> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });

        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;
    }
}
