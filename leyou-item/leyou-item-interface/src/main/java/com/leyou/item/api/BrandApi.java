package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Jun
 * @create 2020/5/29 - 10:04
 */
@RequestMapping("brand")
public interface BrandApi{
    /**
     * 根据品牌bid查询品牌名称
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Brand queryBrandByBid(@PathVariable("id") Long id);
}
