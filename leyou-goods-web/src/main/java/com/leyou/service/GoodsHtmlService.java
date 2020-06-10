package com.leyou.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Jun
 * @create 2020/6/8 - 19:14
 */
@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    /**
     * 创建静态html页面
     * @param spuId
     */
    public void createHtml(Long spuId){

        PrintWriter printWriter = null;
        try {
            //获取页面数据
            Map<String, Object> map = this.goodsService.loadData(spuId);
            //创建thymeleaf上下文对象
            Context context = new Context();
            //把数据放入上下文对象中
            context.setVariables(map);
            //创建输出流
            File file = new File("E:\\Java\\idea-workspace\\myleyou\\tools\\nginx-1.14.0\\html\\item\\"+spuId+".html");
            printWriter = new PrintWriter(file);
            //执行页面静态化方法
            engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        }finally {
            if (printWriter != null){
                printWriter.close();
            }
        }
    }

    /**
     * 处理消息delete
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("E:\\Java\\idea-workspace\\myleyou\\tools\\nginx-1.14.0\\html\\item\\"+id+".html");
        file.deleteOnExit();
    }
}
