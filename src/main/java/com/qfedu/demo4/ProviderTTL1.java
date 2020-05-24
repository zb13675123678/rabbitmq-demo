package com.qfedu.demo4;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 2:41
 * @Description:  本类测试了a和c   ,b通过ProviderTTL2测试;
 *          消息过期时间:
 *              消息的过期时间;
 *                  (a)通过队列设置,所有消息的过期时间
 *                  (b)通过消息本身,设置自己过期时间
 *              队列的过期时间;
 *                  (c)通过队列设置,队列本身的过期时间
 */
public class ProviderTTL1 {

    public static void main(String[] args) throws IOException {

        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建队列
         Map<String, Object> map = new HashMap<>();
         //-->指定队列中所有消息过期时间为5秒
         // map.put("x-message-ttl", 5000);
         //-->指定队列中过期时间为10秒
            map.put("x-expires", 10000);
        channel.queueDeclare("ttlqueue", true, false, false, map);

        //4,给队列发送消息
        channel.basicPublish("", "ttlqueue", null, "Hello".getBytes("utf-8"));

        connection.close();
    }

}
