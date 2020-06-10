package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.acl.Group;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jun
 * @create 2020/5/24 - 18:07
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") long cid){
        List<SpecGroup> groups = this.specificationService.querySpecGroupByCid(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParamByThing(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    )
    {
        List<SpecParam> specParams = this.specificationService.querySpecParamByThing(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(specParams)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);
    }

    /**
     * 根据cid查询规格参数组
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> list = this.specificationService.querySpecsByCid(cid);
        if (CollectionUtils.isEmpty(list)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
