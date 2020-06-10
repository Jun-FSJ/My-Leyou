package com.leyou.search.listener;

import com.leyou.search.service.SearchServcie;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Jun
 * @create 2020/6/10 - 20:24
 */
@Component
public class GoodsListener {

    @Autowired
    private SearchServcie searchServcie;

    /**
     * 处理insert和update的消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public void listenCreate(Long id) throws IOException {
        if (id == null){
            return;
        }
        //创建或更新索引
        this.searchServcie.createInex(id);
    }

    /**
     * 处理delete的消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.delete.index.queue",durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "item.delete"
    ))
    public void delete(Long id){
        if (id == null){
            return;
        }
        //删除索引
        this.searchServcie.deleteIndex(id);
    }
}
