package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.leyou.page.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Jun
 * @create 2020/5/25 - 18:05
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategorySevice categorySevice;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页进行商品查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加模糊搜索条件
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+ key + "%");
        }
        //添加上下架的过滤条件
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加分页条件
        PageHelper.startPage(page,rows);

        //执行查询，获取List<Spu> spuList的集合
        List<Spu> spus = this.spuMapper.selectByExample(example);//根据Example条件进行查询
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //将spuList 集合转成 List<SpuBo> spuBoList集合
        List<SpuBo> spuBos = new ArrayList<>();
        for (Spu spu : spus){
            SpuBo spuBo = new SpuBo();
            //将spu的字段复制给spubo
            BeanUtils.copyProperties(spu,spuBo);
            //通过spu获得brandId设置品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());//根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = this.categorySevice.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names,"-"));
            spuBos.add(spuBo);
        }
        //返回PageResult<SpuBo>对象
        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }

    /**
     * 新增商品信息
     * @param spuBo
     */
    @Transactional
    public void saveBrand(SpuBo spuBo) {
        //先添加spu表
        spuBo.setId(null);//防止数据库被注入攻击
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 再通过添加的spuId添加spu_detail表
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        saveSkuAndStock(spuBo);
        sendMessage(spuBo.getId(),"insert");
    }

    /**
     * 修改商品信息
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateBrand(SpuBo spuBo) {
        //根据spuId查询要删除的sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(sku);
        for(Sku sku1 : skus){
            //通过得到的skuId删除strock表(库存表)
            this.stockMapper.deleteByPrimaryKey(sku1.getId());
        }

        //再删除sku表,根据传过来的spuId删除
        this.skuMapper.delete(sku);

        //新增sku表,再新增strok表
        this.saveSkuAndStock(spuBo);

        //修改spu表
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        //修改spuDeatil表
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMessage(spuBo.getId(),"update");
    }

    /**
     * 封装发送信息的方法
     * @param id
     * @param type
     */
    public void sendMessage(Long id,String type){
        //发送消息
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * 封装成一个方法，添加sku表和stock表
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        for (Sku sku : skus){
            //再添加sku表
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            //再添加库存表stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }



    /**
     * 根据spuId查询spuDetail表的信息
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetaliBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        for(Sku sku1 : skus){
            Stock stock = this.stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        }
        return skus;
    }

    /**
     * 根据spuID查询spu信息
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
