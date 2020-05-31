package com.itcast.watcher;

import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZKConnectionWatcher implements Watcher {

    static{
        BasicConfigurator.configure();
    }

    // 计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            // 事件类型
            if(watchedEvent.getType() == Event.EventType.None){
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功！");
                    countDownLatch.countDown();
                }else if(watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("断开连接！");
                }else if(watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("会话超时！");
                    zooKeeper = new ZooKeeper("192.168.79.31:2181", 5000, new ZKConnectionWatcher());
                }else if(watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("认证失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            zooKeeper = new ZooKeeper("192.168.79.31:2181", 5000, new ZKConnectionWatcher());
            // 阻塞线程，等待连接的创建
            countDownLatch.await();
            // 会话ID
            System.out.println(zooKeeper.getSessionId());
            // 添加授权用户
            zooKeeper.addAuthInfo("digest","itcast:123456".getBytes());
            byte[] data = zooKeeper.getData("/node11", false, null);
            System.out.println(new String(data));
            TimeUnit.SECONDS.sleep(300);
            zooKeeper.close();
            System.out.println("结束");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }

    }


}
