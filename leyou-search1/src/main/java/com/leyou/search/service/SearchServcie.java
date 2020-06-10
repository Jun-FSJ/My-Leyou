package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Autowired
    private GoodsRepository goodsRepository;

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

    /**
     * 搜索
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {

        // 判断查询条件
        if (StringUtils.isBlank(request.getKey())) {
            // 返回默认结果集
            return null;
        }

        // 初始化自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加查询条件
//        MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBooleanQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        // 添加结果集过滤，只需要：id,subTitle, skus
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        // 获取分页参数
        Integer page = request.getPage();
        Integer size = request.getSize();
        // 添加分页
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 执行搜索，获取搜索的结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        // 解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));
        //判断分类聚合的结果集大小，等于1则聚合
        List<Map<String,Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1){
            specs = getParamAggResult((Long)categories.get(0).get("id"),basicQuery);
        }
        // 封装成需要的返回结果集
        return new SearchResult(goodsPage.getContent(), goodsPage.getTotalElements(), goodsPage.getTotalPages(), categories,brands,specs);
    }

    /**
     * 构建bool查询码构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }
        //获取用户现在的过滤条件
        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌",key)){
                key = "brandId";
            }else if (StringUtils.equals("分类",key)){
                key = "cid3";
            }else {
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 聚合出规格参数过滤条件
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, BoolQueryBuilder basicQuery) {
        //创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParamByThing(null, cid, null, true);
        //添加聚合
        specParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        //只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        //定义一个集合，收集聚合结果集
        List<Map<String,Object>> paramList = new ArrayList<>();
        //解析聚合结果的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            //放入规格参数名
            map.put("k",entry.getKey());
            //手机规格参数值
            List<String> options = new ArrayList<>();
            //解析每个聚合
            StringTerms terms = (StringTerms)entry.getValue();
            //遍历每个聚合桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options",options);
            paramList.add(map);
        }
        return paramList;
    }

    /**
     * 解析品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        // 处理聚合结果集
        LongTerms terms = (LongTerms)aggregation;
        // 获取所有的品牌id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 定义一个品牌集合，搜集所有的品牌对象
        List<Brand> brands = new ArrayList<>();
        // 解析所有的id桶，查询品牌
        buckets.forEach(bucket -> {
            Brand brand = this.brandClient.queryBrandByBid(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;
    }

    /**
     * 解析分类
     * @param aggregation
     * @return
     */
    private List<Map<String,Object>> getCategoryAggResult(Aggregation aggregation) {
        // 处理聚合结果集
        LongTerms terms = (LongTerms)aggregation;
        // 获取所有的分类id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        // 定义一个品牌集合，搜集所有的品牌对象
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();
        // 解析所有的id桶，查询品牌
        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    /**
     * 接收消息进行创建索引的操作
     * @param id
     * @throws IOException
     */
    public void createInex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        //构建商品
        Goods goods = this.buildGoods(spu);
        //保存数据到索引库
        this.goodsRepository.save(goods);
    }

    /**
     * 接收消息进行删除索引的操作
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
