package com.qfedu.demo1;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther: Zhangbo
 * @date: 2020/5/23 1:01
 * @Description:  消费端
 *
 * 匿名内部类使用外部类的局部变量,该变量必须设为final修饰, * 匿名内部类使用外部类的局部变量，该变量必须用final修饰，jdk1.8之后可以省略不写，但是final仍然存在
 */
public class Consumer {

    //创建一个容量为10的线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();

        //3,创建队列(为了消除这种启动顺序，可以在提供端和消费端都进行同名队列和同名交换机的创建)
        channel.queueDeclare("myqueue",false,false,false,null);

        //4,监听队列-->处理队列的消息
        channel.basicConsume("myqueue",true,new DefaultConsumer(channel){

            //返回消息的处理方法(回调方法)
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, final byte[] body) throws IOException {
                //使用线程池快速处理
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("消费者获得消息：" + new String(body, "utf-8"));

                            //测试线程池的并行处理消息,(rabbitMq本来是串行处理消息的)
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //消费者需要实时监控对列,所以不能关闭连接
        //connection.close();

    }

}
