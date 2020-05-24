package com.qfedu.demo4;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 2:41
 * @Description:
 */
public class ProviderTTL2 {

    public static void main(String[] args) throws IOException {

        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建队列
        channel.queueDeclare("durablequeue", true, false, false, null);

        //5,给队列发送消息-->并指定单个消息本身的过期时间
        for (int i = 0; i < 10; i++) {
            String msg = "Hello" + i;

            if(i == 5){
                //给单条消息设置过期时间
                AMQP.BasicProperties properties = new AMQP.BasicProperties()
                        .builder()
                        .expiration("5000")
                        .build();

                channel.basicPublish("", "durablequeue", properties, msg.getBytes("utf-8"));
            } else {
                channel.basicPublish("", "durablequeue", null, msg.getBytes("utf-8"));
            }
        }



        connection.close();
    }
}
