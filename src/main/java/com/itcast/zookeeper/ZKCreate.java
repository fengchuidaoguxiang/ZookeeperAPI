package com.itcast.zookeeper;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZKCreate {

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
    }

    @After
    public void after() throws Exception{
        System.out.println("after");
        zooKeeper.close();
    }


    @Test
    public void create1() throws Exception{
        System.out.println("create1");
        // String path: 节点路径
        // byte[] data: 节点的数据
        // List<ACL> acl: 权限列表    ZooDefs.Ids.OPEN_ACL_UNSAFE  ： 对应 world:anyone:cdrwa 权限
        // CreateMode createMode: 节点类型 ， CreateMode.PERSISTENT ：持久化节点
        zooKeeper.create("/create/node1", "node1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
    }

    @Test
    public void create2() throws Exception{
        System.out.println("create2");
        // Ids.READ_ACL_UNSAFE world:anyone:r
        zooKeeper.create("/create/node2", "node2".getBytes(),ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create3() throws Exception{
        System.out.println("create3");
        // world授权模式
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("world", "anyone");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        acls.add(new ACL(ZooDefs.Perms.WRITE, id));
        zooKeeper.create("/create/node3", "node3".getBytes(),acls,CreateMode.PERSISTENT);
    }

    @Test
    public void create4() throws Exception{
        System.out.println("create4");
        // ip授权模式
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("ip", "192.168.79.31");
        // 权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        zooKeeper.create("/create/node4", "node4".getBytes(), acls,CreateMode.PERSISTENT);
    }

    @Test
    public void create5() throws Exception{
        System.out.println("create5");
        // auth授权模式
        // 添加授权用户
        zooKeeper.addAuthInfo("digest", "itcast:123456".getBytes());
        zooKeeper.create("/create/node5","node5".getBytes(),ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    @Test
    public void create6() throws Exception{
        System.out.println("create6");
        // auth授权模式
        // 添加授权用户
        zooKeeper.addAuthInfo("digest", "itcast:123456".getBytes());
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("auth", "itcast");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.READ, id));
        zooKeeper.create("/create/node6","node6".getBytes(),acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create7() throws Exception{
        System.out.println("create7");
        // digest授权模式
        // 权限列表
        List<ACL> acls = new ArrayList<>();
        // 授权模式和授权对象
        Id id = new Id("digest", "itheima:qlzQzCLKhBROghkooLvb+Mlwv4A=");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL, id));
        zooKeeper.create("/create/node7", "node7".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create8() throws Exception{
        System.out.println("create8");
        // 持久化有序节点
        String result = zooKeeper.create("/create/node8","node8".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    public void create9() throws Exception{
        System.out.println("create9");
        // 临时节点
        String result = zooKeeper.create("/create/node9","node9".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println(result);
    }

    @Test
    public void create10() throws Exception{
        System.out.println("create10");
        // 临时有序节点
        String result = zooKeeper.create("/create/node10","node10".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    public void create11() throws Exception{
        System.out.println("create11");
        // 异步方式创建节点
        zooKeeper.create("/create/node11","node11".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                // 0 代表创建成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
               // 节点的路径
                System.out.println(name);
                // 上下文参数
                System.out.println(ctx);
            }
        }, "I am context");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束");
    }

    /**
     * 同步修改
     */
    @Test
    public void set1() throws Exception{
        System.out.println("set1");
        // String path : 节点的路径
        // byte[] data : 修改的数据
        // int version : 数据版本号  -1代表版本号不参与更新
        Stat stat = zooKeeper.setData("/set/node1",  "node13".getBytes(), -1 );
        // 当前节点的版本号
        System.out.println(stat.getVersion());
    }

    /**
     *异步修改
     */
    @Test
    public void set2() throws Exception{
        System.out.println("set2");
        zooKeeper.setData("/set/node1","node14".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                // 0 代表成功
                System.out.println( i );
                // 节点路径
                System.out.println( s );
                // 上下文参数对象
                System.out.println( o );
                // 属性描述对象
                System.out.println(stat.getVersion());
            }
        },"I am Context");
        Thread.sleep(10000);
        System.out.println("结束");
    }

    /**
     * 同步删除
     */
    @Test
    public void delete1() throws Exception{
        System.out.println("delete1");
        // path : 删除节点的节点路径
        // version : 数据版本信息， -1代表删除节点时，不考虑版本信息
        zooKeeper.delete("/delete/node1",-1);

    }

    /**
     * 异步删除
     */
    @Test
    public void delete2() throws Exception{
        System.out.println("delete2");
        zooKeeper.delete("/delete/node2",-1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int i, String s, Object o) {
                // 0代表删除成功
                System.out.println(i);
                // 节点的路径
                System.out.println(s);
                // 上下文参数对象
                System.out.println(o);
            }
        },"I am Context");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束");
    }

    /**
     * 同步查看节点
     */
    @Test
    public void get1() throws Exception{
        System.out.println("get1");
        Stat stat = new Stat();
        // path: 节点的路径
        // stat: 读取节点属性的对象
        byte[] bytes = zooKeeper.getData("/get/node1",false, stat);
        // 打印数据
        System.out.println(new String(bytes));
        // 版本信息
        System.out.println(stat.getVersion());
    }

    /**
     * 异步查看节点
     */
    @Test
    public void get2() throws Exception{
        System.out.println("get2");
        zooKeeper.getData("/get/node1",false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                // 0 代表读取成功
                System.out.println(i);
                // 节点的路径
                System.out.println(s);
                // 上下文参数对象
                System.out.println(o);
                // 数据
                System.out.println(new String(bytes));
                // 属性对象
                System.out.println(stat.getVersion());
            }
        }, "I am Context");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束");
    }


    /**
     * 同步查看子节点
     */
    @Test
    public void getChild1() throws Exception{
        System.out.println("getChild1");
        // path:节点路径
        List<String> children = zooKeeper.getChildren("/get", false);
        for (String child : children) {
            System.out.println(child);
        }
    }

    /**
     * 异步查看子节点
     */
    @Test
    public void getChild2() throws Exception{
        System.out.println("getChild12");
        // path:节点路径
        zooKeeper.getChildren("/get", false, new AsyncCallback.ChildrenCallback() {
            @Override
            public void processResult(int i, String s, Object o, List<String> list) {
                // 0 代表读取成功
                System.out.println(i);
                // 节点的路径
                System.out.println(s);
                // 上下文参数对象
                System.out.println(o);
                // 子节点信息
                for (String s1 : list) {
                    System.out.println(s1);
                }
            }
        }, "I am Context");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束");
    }

    /**
     * 同步判断节点是否存在
     */
    @Test
    public void exist1() throws Exception{
        System.out.println("exist1");
        // path: 节点的路径
        Stat stat = zooKeeper.exists("/exists1", false);
        System.out.println(stat);
        System.out.println(stat.getVersion());

    }

    /**
     * 异步判断节点是否存在
     */
    @Test
    public void exist2() throws Exception{
        System.out.println("exist2");
        zooKeeper.exists("/exists1", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int i, String s, Object o, Stat stat) {
                // 0 代表执行成功
                System.out.println(i);
                // 节点的路径
                System.out.println(s);
                // 上下文参数
                System.out.println(o);
                if(stat != null){
                    // 节点的版本信息
                    System.out.println(stat.getVersion());
                }
            }
        },"I am Context");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束");
    }



}
