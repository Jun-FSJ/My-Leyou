package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.page.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Jun
 * @create 2020/5/22 - 18:57
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 对品牌进行分页查询
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryByandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化Example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //进行模糊查询，根据名称，或根据首字母模糊查询
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
            //这里的property属性得对应数据库内的属性，因为这是通用mapper类帮忙进行模糊查询
        }

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+ (desc ? "desc":"asc"));
        }
        //添加分页条件
        PageHelper.startPage(page,rows);

        List<Brand> brands = brandMapper.selectByExample(example);
        //获取结果集,包装成pageinfo
        PageInfo<Brand> PageInfo = new PageInfo<>(brands);
//        //将最后的结果集返回
//        PageResult<Brand> result = new PageResult<>(PageInfo.getTotal(),PageInfo.getList());
//        return result;
        // 包装成分页结果集返回
        return new PageResult<>(PageInfo.getTotal(), PageInfo.getList());

    }
    /**
     * 新增商品分类和品牌中间表数据,和添加品牌
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand表(品牌表)
        Boolean flag = this.brandMapper.insert(brand) == 1;//表示新增成功
        //再来新增brand_categoyr(品牌-商品表)
        for(Long cid : cids){
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        }
    }
    /**
     * 通过商品的id，也就是cid查出对应的商品品牌有哪些
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = this.brandMapper.selectBrandByCid(cid);
        return brands;
    }
    /**
     * 根据品牌bid查询品牌名称
     * @param id
     * @return
     */
    public Brand queryBrandByBid(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}







