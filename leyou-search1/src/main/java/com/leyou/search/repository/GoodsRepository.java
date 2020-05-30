package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Jun
 * @create 2020/5/30 - 11:22
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
