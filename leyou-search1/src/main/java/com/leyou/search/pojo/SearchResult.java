package com.leyou.search.pojo;

import com.leyou.item.pojo.Brand;
import com.leyou.page.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author Jun
 * @create 2020/6/3 - 16:52
 */
public class SearchResult extends PageResult<Goods> {
    private List<Map<String, Object>> categories;
    private List<Brand> brands;
    private List<Map<String, Object>> specs;

    public SearchResult() {
    }

    public SearchResult(List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(List<Goods> items, Long total, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total,items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(List<Goods> items, Long total, Integer totalPage, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total,items,totalPage);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}