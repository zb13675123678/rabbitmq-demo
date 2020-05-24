package com.qfedu.demo3;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 1:56
 * @Description: 发布订阅队列模式:单发送多队列接收
 */
public class Provider {
    //提供者(Provider) ->交换机 -> 队列(多个) ->消费者(多个Consumer)

    public static void main(String[] args) throws IOException {
        //1,获取连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建管道
        Channel channel = connection.createChannel();

        //3,创建交换机(交换机类型:direct, topic,headers, fanout)
        channel.exchangeDeclare("myexchange","fanout",true,true,false,null);

        //4,发送消息
        String msg ="Hello!!!";
        channel.basicPublish("myexchange","",null,msg.getBytes("utf-8"));
        System.out.println("提供端只发送了一条消息");


        //5,关闭连接
        connection.close();
    }
}
