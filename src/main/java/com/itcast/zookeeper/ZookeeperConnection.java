package com.itcast.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConnection {

    public static void main(String[] args) {

        try{
            // 计数器对象
            CountDownLatch countDownLatch = new CountDownLatch(1);
            // String connectString ：服务器的ip和端口
            // int sessionTimeout ：客户端与服务器之间的会话超时时间，以毫秒为单位的
            // Watcher watcher ：监视器对象
            ZooKeeper zooKeeper = new ZooKeeper("192.168.79.31:2181,192.168.79.32:2181,192.168.79.33:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功！");
                        countDownLatch.countDown();
                    }
                }
            });
            // 主线程阻塞，等待连接对象的创建成功
            countDownLatch.await();
            // 会话编号
            System.out.println(zooKeeper.getSessionId());
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
