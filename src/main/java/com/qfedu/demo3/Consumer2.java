package com.qfedu.demo3;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 2:07
 * @Description:
 */
public class Consumer2 {

    public static void main(String[] args) throws IOException {
        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建消费队列2
        channel.queueDeclare("myqueue2",true, false, true, null);

        //4,绑定队列到交换机
        channel.queueBind("myqueue2","myexchange","");
        //绑定交换机到队列的方法
        //channel.exchangeBind("myexchange","myqueue1","");

        //5,监听队列-->处理消费消息
        channel.basicConsume("myqueue2",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1获得消息：" + new String(body, "utf-8"));
            }
        });
    }
}
