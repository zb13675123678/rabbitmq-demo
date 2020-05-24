package com.qfedu.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @auther: Zhangbo
 * @date: 2020/5/22 17:25
 * @Description:   Rabbit的工具类
 */
public class RabbitMqUtils {

    //rabbitmq的连接对象
    private  static ConnectionFactory connectionFactory;

    //工厂对象只需要创建一次即可
    static  {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("101.133.165.183");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");  //设置虚拟机
    }

    //获取rabbitMq的连接的方法
    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
            System.out.println(connection);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;
    }


}
