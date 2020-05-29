package com.leyou.search.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jun
 * @create 2020/5/29 - 10:14
 */
@FeignClient(value = "item-service")
public interface BrandClient extends BrandApi {
}
