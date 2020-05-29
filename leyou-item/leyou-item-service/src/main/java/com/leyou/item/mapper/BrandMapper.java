package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/22 - 18:56
 */
public interface BrandMapper extends Mapper<Brand> {
    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 通过商品的id，也就是cid查出对应的商品品牌有哪些
     * @param cid
     * @return
     */
    @Select("select * from tb_brand where id in (select brand_id from tb_category_brand where category_id = #{cid})")
    List<Brand> selectBrandByCid(Long cid);
}
