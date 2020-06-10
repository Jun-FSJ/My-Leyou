package com.leyou.listener;

import com.leyou.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.RespectBinding;

/**
 * @author Jun
 * @create 2020/6/10 - 20:40
 */
@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 处理insert和update的消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.web.queue",durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void listenCreate(Long id){
        if (id == null){
            return;
        }
        //创建页面
        this.goodsHtmlService.createHtml(id);
    }

    /**
     * 处理delete的消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.delete.web.queue",durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "item.delete"
    ))
    public void listenDelete(Long id){
        if (id == null){
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
