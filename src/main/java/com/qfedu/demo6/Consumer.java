package com.qfedu.demo6;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer {

    public static void main(String[] args) throws IOException {
        Connection connection = RabbitMqUtils.getConnection();
        final Channel channel = connection.createChannel();

        //限制消息的处理数量
        channel.basicQos(100);
        channel.basicConsume("dead-queue", false, new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("接收到消息：" + new String(body, "utf-8"));

                //没有手动确认
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }
}
