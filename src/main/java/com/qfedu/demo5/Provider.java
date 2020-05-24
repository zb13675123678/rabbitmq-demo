package com.qfedu.demo5;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 2:41
 * @Description:  优先级队列
 *              首先,设置队列的优先级限定范围(0`255之间)
 *              其次,在设置当前队列中,每个消息的优先级
 *              最后,在测试每个消息读取的优先级顺序
 */
public class Provider {

    public static void main(String[] args) throws IOException {
        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建交换机
        channel.exchangeDeclare("myexchange", "fanout", true, false, null);

        //4.创建队列-->并指定队列优先级限制范围
        Map<String, Object> map = new HashMap<>();
        map.put("x-max-priority", 100);  //最大优先级可选范围：0~255
        channel.queueDeclare("myqueue", true, false, false, map);

        //5.绑定队列到交换机
        channel.queueBind("myqueue", "myexchange", "");


        for (int i = 0; i < 10; i++) {
            //为了测试消息本身的优先级:自动生成优先级随机数,设置在消息身上
            int pri = (int) (Math.random() * 100 + 1);

            //设置消息本身的优先级
            AMQP.BasicProperties properties = new AMQP.BasicProperties()
                    .builder()
                    .priority(pri)  //优先级的值
                    .build();

            //6,发送消息到交换机
            channel.basicPublish("myexchange", "", properties, ("消息！！！" + pri).getBytes("utf-8"));
        }

    }
}
