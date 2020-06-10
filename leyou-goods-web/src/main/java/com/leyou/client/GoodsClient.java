package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jun
 * @create 2020/5/29 - 10:03
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
