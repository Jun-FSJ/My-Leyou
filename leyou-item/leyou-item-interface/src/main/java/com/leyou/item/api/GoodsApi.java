package com.leyou.item.api;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.page.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/29 - 10:10
 */
public interface GoodsApi {
    /**
     * 分页进行商品查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value  = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );
    /**
     * 根据spuId查询spuDetail表的信息
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetaliBySpuId(@PathVariable("spuId") Long spuId);
    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);
}
