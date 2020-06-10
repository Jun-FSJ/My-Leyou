package com.leyou.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Jun
 * @create 2020/5/29 - 10:14
 */
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationApi {
}
