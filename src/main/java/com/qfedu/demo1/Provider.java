package com.qfedu.demo1;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 0:45
 * @Description:  简单队列模式:单发送单接受(优化:线程池接收)
 */
public class Provider {

    //提供者(Provider)  -> 队列 ->  消费者(Consumer)

    public static void main(String[] args) throws IOException {
        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建队列
        channel.queueDeclare("myqueue",false,false,false,null);

        //4,给队列发送消息
        for (int i = 0; i < 10; i++) {
            String msg ="Hello!!!"+i;
            channel.basicPublish("","myqueue",null,msg.getBytes("utf-8"));
            System.out.println("消息发送完成");
        }

        //5,关闭连接
        connection.close();
    }

}
