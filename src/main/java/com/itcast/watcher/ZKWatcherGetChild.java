package com.itcast.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.proto.WatcherEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZKWatcherGetChild {
    String IP = "192.168.79.31:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        System.out.println("before");
        // 计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // String connectString ：服务器的ip和端口
        // int sessionTimeout ：客户端与服务器之间的会话超时时间，以毫秒为单位的
        // Watcher watcher ：监视器对象
        zooKeeper = new ZooKeeper( IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("连接对象的参数！");
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功！");
                    countDownLatch.countDown();
                }
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        });
        // 主线程阻塞，等待连接对象的创建成功
        countDownLatch.await();
        // 会话编号
        System.out.println(zooKeeper.getSessionId());
    }

    @After
    public void after() throws Exception{
        System.out.println("after");
        zooKeeper.close();
    }

    @Test
    public void watcherGetChild1() throws Exception {
        // path: 节点的路径
        // watch: 使用连接对象中的watcher
        zooKeeper.getChildren("/watcher3",true);

        TimeUnit.SECONDS.sleep(50);
        System.out.println("结束");
    }
    @Test
    public void watcherGetChild2() throws Exception {
        // path: 节点的路径
        // watch: 自定义的watcher
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        });

        TimeUnit.SECONDS.sleep(50);
        System.out.println("结束");
    }
    @Test
    public void watcherGetChild3() throws Exception {
        // 一次性
        // path: 节点的路径
        // watch: 使用连接对象中的watcher
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
            }
        };
        zooKeeper.getChildren("/watcher3",watcher);

        TimeUnit.SECONDS.sleep(50);
        System.out.println("结束");
    }
    @Test
    public void watcherGetChild4() throws Exception {
        // 重复使用
        // path: 节点的路径
        // watch: 使用连接对象中的watcher
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher3",watcher);

        TimeUnit.SECONDS.sleep(50);
        System.out.println("结束");
    }


    @Test
    public void watcherGetChild5() throws Exception {
        // 多个监听器
        // path: 节点的路径
        // watch: 使用连接对象中的watcher
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("1");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            }
        });
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("2");
                System.out.println("path=" + watchedEvent.getPath());
                System.out.println("eventType=" + watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
            }
        });

        TimeUnit.SECONDS.sleep(50);
        System.out.println("结束");
    }
}
