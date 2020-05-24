package com.qfedu.demo6;

import com.qfedu.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
/**
 * @auther: Zhangbo
 * @date: 2020/5/23 0:45
 * @Description:
 */
public class Provider {

     //准备一个TreeMap集合，用来进行消息的重试
     private static TreeMap<Long, String> treeMap = new TreeMap<>();//红-黑树 key有序

    public static void main(String[] args) throws IOException {

        //1,获取rabbitMq的连接
        Connection connection = RabbitMqUtils.getConnection();

        //2,创建连接通道
        Channel channel = connection.createChannel();


        //3,创建一个死信交换机
        channel.exchangeDeclare("dead-exchange", "fanout", true, false, false, null);
        //4,创建死信队列
        channel.queueDeclare("dead-queue", true, false, false, null);
        //5,绑定死信队列到死信交换机
        channel.queueBind("dead-queue", "dead-exchange", "");

        //----------------------(所谓的死信和普通都是相同的产品,只不过是使用上的概念)------------------------------------------
        //6,创建一个普通的交换机
        channel.exchangeDeclare("normal-exchange", "fanout", true, false, false, null);
        //7,创建一个普通的队列-->并绑定到死信交换机
        Map<String, Object> map = new HashMap<>();
            //TODO 设置所有消息的过期时间-->(当消息过期后，就会产生死信消息)
            map.put("x-message-ttl", 5000);
            //TODO 绑定普通的队列到死信交换机-->(普通队列的死信消息,就会转发给死信交换机)
            map.put("x-dead-letter-exchange", "dead-exchange");
        channel.queueDeclare("normal-queue", true, false, false, map);

        //8,绑定普通队列到普通交换机-->正常消息还走正常交换机  |||-->正常队列
        channel.queueBind("normal-queue", "normal-exchange", "");

        //发送消息
//        String msg = "发送的时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


//        //事务模式确认消息
//        channel.txSelect();//将channel切换成事务模式
//
//        try {
//            //设置消息的持久化
//            AMQP.BasicProperties properties = new AMQP.BasicProperties()
//                    .builder()
//                    .deliveryMode(2)//消息持久化模式
//                    .build();
//
//            channel.basicPublish("normal-exchange", "", properties, msg.getBytes("utf-8"));
//
//            //提交事务
//            channel.txCommit();
//        } catch (IOException e) {
//            e.printStackTrace();
//            //回滚事务
//            channel.txRollback();
//
//            //进行消息的重试与补偿
//        }


//        //Publish Confirm同步模式
//        channel.confirmSelect();//设置为confirm模式
//
//        //发布消息
//        channel.basicPublish("normal-exchange", "", null, msg.getBytes("utf-8"));
//
//        //同步等待rabbitmq的响应
//        boolean falg = true;
//        try {
//            falg = channel.waitForConfirms();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            falg = false;
//        }
//
//        if(!falg){
//            //进行重试或者补偿
//        }

        //Publish Confirm异步模式 - 推荐
        channel.confirmSelect();


        //设置异步的监听方法
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //如果消息成功，就会回调该方法
                //deliveryTag - 表示成功的消息id
                //multiple - 表示是否批量，如果为true表示批量成功，如果为false，表示单条成功
                if (multiple){
                    System.out.println("id为" + deliveryTag + "消息以及之前的消息都成功了！");
                    //批量成功，将后续未成功的消息赋值给treeMap
//                    treeMap = (TreeMap<Long, String>) treeMap.tailMap(deliveryTag + 1);
                } else {
                    System.out.println("id为" + deliveryTag + "消息成功了！");
                    //消息处理成功,直接移除该消息
//                    treeMap.remove(deliveryTag);
                }
            }

            //1 2 3 4 5
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //如果消息失败，就回调该方法
                //deliveryTag - 表示失败的消息id
                //multiple - 表示是否批量，如果为true表示批量失败，如果为false，表示单条失败
                if (multiple){
                    System.out.println("id为" + deliveryTag + "消息以及之前的消息都失败了！");
//                    TreeMap failtreeMap = (TreeMap) treeMap.headMap(deliveryTag);
                    //对该失败的消息进行批量重试
                } else {
                    System.out.println("id为" + deliveryTag + "消息失败了！");
//                    String msg = treeMap.get(deliveryTag);
                    //对失败的消息msg，进行重试
                }
            }
        });


        //发布消息
        for (int i = 0; i < 1000; i++) {
            String msg = "消息" + i;

            //获得下一条消息的发送编号
            long seqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("normal-exchange", "", null, msg.getBytes("utf-8"));
            //发送消息后，缓存发送出去的消息
            treeMap.put(seqNo, msg);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connection.close();
    }
}
