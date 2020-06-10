package com.leyou.item.controller;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import com.leyou.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/25 - 18:05
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 分页进行商品查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value  = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuBo> result = this.goodsService.querySpuBoByPage(key,saleable,page,rows);
        if (CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增商品
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveBrand(
            @RequestBody SpuBo spuBo){//当要接收的数据是json格式时，得用@RequestBody注解接收
        this.goodsService.saveBrand(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品信息
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateBrand(@RequestBody SpuBo spuBo){
        this.goodsService.updateBrand(spuBo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId查询spuDetail表的信息
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetaliBySpuId(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail = this.goodsService.querySpuDetaliBySpuId(spuId);
        if (spuDetail == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }
    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skus = this.goodsService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spuID查询spu信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if (spu == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }
}
