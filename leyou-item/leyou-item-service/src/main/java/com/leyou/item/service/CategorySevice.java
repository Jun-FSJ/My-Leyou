package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jun
 * @create 2020/5/22 - 15:42
 */
@Service
public class CategorySevice {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     *分级显示商品类别
     * @param pid
     * @return
     */
    public List<Category> queryCategoryById(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);
    }

    /**
     * 通过cid的id集合查询到类别名称
     * @param ids
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category : categories){
            names.add(category.getName());
        }
        return names;
    }
}
