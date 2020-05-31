package com.itcast.example;

import com.itcast.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MyConfigCenter implements Watcher {
    // zk的ip
    String IP = "192.168.79.31:2181";
    // 计数器
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    static ZooKeeper zooKeeper;


    // 用于本地化存储配置信息
    private String url;
    private String username;
    private String password;

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
                    // 超时后服务器端已经将连接释放，需要重新连接服务器端
                    zooKeeper = new ZooKeeper(IP,6000,new ZKConnectionWatcher());
                }else if(watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("验证失败！");
                }
            }
            // 当配置信息发生变化时，重新从zookeeper加载信息
            else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                initValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 构造方法
    public MyConfigCenter(){
        initValue();
    }

    // 连接zookeeper服务器，读取配置信息
    public void initValue(){
        try {
            // 创建连接对象
            zooKeeper = new ZooKeeper(IP,5000,this);
            // 阻塞线程，等待连接的创建成功
            countDownLatch.await();
            // 读取配置信息
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public static void main(String[] args) {
        try {
            MyConfigCenter myConfigCenter = new MyConfigCenter();
            for(int i = 1; i <= 20; i++){
                TimeUnit.SECONDS.sleep(5);
                System.out.println("url:" + myConfigCenter.getUrl());
                System.out.println("username:" + myConfigCenter.getUsername());
                System.out.println("password:" + myConfigCenter.getPassword());
                System.out.println("#########################################");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
