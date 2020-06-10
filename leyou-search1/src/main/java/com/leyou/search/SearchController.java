package com.leyou.search;

import com.leyou.page.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Jun
 * @create 2020/5/31 - 10:02
 */
@Controller
public class SearchController {
    @Autowired
    private SearchServcie searchServcie;

    /**
     * 搜索商品
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<SearchResult> Search(@RequestBody SearchRequest request){
        SearchResult pageResult = this.searchServcie.search(request);
        if (pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

}
