package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Jun
 * @create 2020/5/22 - 15:41
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {

}
