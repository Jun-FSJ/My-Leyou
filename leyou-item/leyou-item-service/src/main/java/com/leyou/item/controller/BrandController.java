package com.leyou.item.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import com.leyou.page.PageResult;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/22 - 18:57
 */
@Controller
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 对品牌进行分页查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
        @RequestParam(value = "key",required = false) String key,
        @RequestParam(value = "page",defaultValue = "1") Integer page,
        @RequestParam(value = "rows",defaultValue = "5") Integer rows,
        @RequestParam(value = "sortBy",required = false) String sortBy,
        @RequestParam(value = "desc",required = false) Boolean desc

    ){
        PageResult<Brand> result = brandService.queryByandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand
            , @RequestParam("cids") List<Long> cids){//当后面为?cid，用@RequestParam可以直接接收
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 通过分类的id，也就是cid查出对应的商品品牌有哪些
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(
            @PathVariable("cid") Long cid){ //是当后面为/cid，用@PathVariable接收，并在@xxxMappering()里面接收
        List<Brand> brands = this.brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据品牌bid查询品牌名称
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandByBid(@PathVariable("id") Long id){
        Brand brand = this.brandService.queryBrandByBid(id);
        if (brand == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
