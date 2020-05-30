package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.PragmaHandler;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.catalina.mapper.Mapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.soap.Addressing;
import java.io.IOException;
import java.util.*;

/**
 * @author Jun
 * @create 2020/5/30 - 11:24
 */
@Service
public class SearchServcie{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    public Goods buildGoods(Spu spu) throws IOException {
        //创建Goods对象
        Goods goods = new Goods();
        //设置参数
        goods.setBrandId(spu.getBrandId());
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());

        //根据brandId查询商品的名称
        Brand brand = brandClient.queryBrandByBid(spu.getBrandId());
        //根据spu的cids查询分类名称
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //spu的tittle+商品的名称+分类名称
        goods.setAll(spu.getTitle()+brand.getName()+ StringUtils.join(names," "));

        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        ArrayList<Map<String,Object>> skuMapList = new ArrayList<>();
        for(Sku sku : skus){
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.isNotBlank(sku.getImages())?
                    StringUtils.split(sku.getImages(),",")[0] : "");
            skuMapList.add(skuMap);
        }
        //
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));


        //查询出所有的搜素规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParamByThing(null, spu.getCid3(), null, true);
        //查询spuDetail,获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDetaliBySpuId(spu.getId());
        //获取通用的规格参数
        Map<Long,Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<Long,Object>>(){});
        //获取特殊的规格参数
        Map<Long,Object> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<Long,List<Object>>>(){});
        // 定义map接收{规格参数名，规格参数值}
        Map<String,Object> paramMap = new HashMap<>();
        for(SpecParam param : specParams){
            //先判断是否为通用参数
            if (param.getGeneric()){
                //获取通用参数的值
                String value = genericSpecMap.get(param.getId()).toString();
                //判断通用参数是否为数字类型
                if (param.getNumeric()){
                    //如果是数字类型，就判断该数值落在哪个区间
                    value = chooseSegment(value, param);
                }
                paramMap.put(param.getName(),value);
            }else {
                paramMap.put(param.getName(),specialSpecMap.get(param.getId()));
            }
        }
        goods.setSpecs(paramMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
