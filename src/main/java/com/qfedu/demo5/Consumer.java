package com.qfedu.demo5;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer {

    public static void main(String[] args) throws IOException {

        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建队列
        channel.basicConsume("myqueue", true, new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费到消息：" + new String(body, "utf-8"));
            }
        });

    }
}
