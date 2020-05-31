package com.itcast.example;

import com.itcast.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GloballyUniqueId implements Watcher {

    // zk的ip
    String IP = "192.168.79.31:2181";
    // 计数器对象
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 用户生成序号的节点
    String defaultPath = "/uniqueId";
    // 连接对象
    ZooKeeper zooKeeper;


    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            // 捕获事件状态
            if(watchedEvent.getType() == Event.EventType.None){
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if(watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("连接断开！");
                }else if(watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("连接超时！");
                    // 超时后服务器端已经连接释放，需要重新连接服务器端
                    zooKeeper = new ZooKeeper(IP,6000, new ZKConnectionWatcher());
                }else if(watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("验证失败！");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 构造方法
    public GloballyUniqueId() {

        try {
            // 打开连接
            zooKeeper = new ZooKeeper(IP,6000,this);
            // 阻塞线程，等待连接的创建成功
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 生成ID的方法
    public String getUniqueId(){
        String path = "";
        try {
            // 创建临时有序节点
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // /uniqueId0000000000001
        return path.substring(9);
    }

    public static void main(String[] args) {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();
        for (int i = 1; i <= 5 ; i++) {
            String id = globallyUniqueId.getUniqueId();
            System.out.println(id);
        }
    }


}
