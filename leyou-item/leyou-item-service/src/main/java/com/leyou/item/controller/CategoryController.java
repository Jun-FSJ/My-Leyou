package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategorySevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;

/**
 * @author Jun
 * @create 2020/5/22 - 15:44
 */
@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategorySevice categorySevice;

    /**
     *分级显示商品类别
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryById(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        if (pid == null || pid < 0){
            return ResponseEntity.badRequest().build();//响应400
        }
        List<Category> categories = categorySevice.queryCategoryById(pid);

        if (CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();//响应404
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据商品分类id，查询商品分类名称
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){
        List<String> names = this.categorySevice.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }

}
