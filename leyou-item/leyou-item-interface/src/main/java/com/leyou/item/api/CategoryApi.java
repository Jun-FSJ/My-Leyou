package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/29 - 10:07
 */
@RequestMapping("category")
public interface CategoryApi {
    /**
     * 根据商品分类id，查询商品分类名称
     * @param ids
     * @return
     */
    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
