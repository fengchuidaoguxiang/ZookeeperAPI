package com.itcast.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CuratorLock {

    String IP = "192.168.79.31:2181,192.168.79.32:2181,192.168.79.33:2181";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }

    @Test
    public void lock1() throws Exception {
        // 排他锁
        // arg1:连接对象
        // arg2:节点路径
        InterProcessLock interProcessLock = new InterProcessMutex(client, "/lock1");
        System.out.println("等待获取锁对象！");
        // 获取锁
        interProcessLock.acquire();
        for (int i = 1; i < 10; i++) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(i);
        }
        // 释放锁
        interProcessLock.release();
        System.out.println("等待释放锁！");

        System.out.println("结束");

    }


    @Test
    public void lock2() throws Exception{
        // 读写锁
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(client, "/lock1");
        // 获取读锁对象
        InterProcessLock interProcessLock = interProcessReadWriteLock.readLock();
        System.out.println("等待获取锁对象！");
        // 获取锁
        interProcessLock.acquire();
        for (int i = 1; i < 10; i++) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println( "读锁" + i);
        }
        // 释放锁
        interProcessLock.release();
        System.out.println("等待释放锁！");
    }

    @Test
    public void lock3() throws Exception{
        // 读写锁
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(client, "/lock1");
        // 获取写锁对象
        InterProcessLock interProcessLock = interProcessReadWriteLock.writeLock();
        System.out.println("等待获取锁对象！");
        // 获取锁
        interProcessLock.acquire();
        for (int i = 1; i < 10; i++) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println("写锁" + i);
        }
        // 释放锁
        interProcessLock.release();
        System.out.println("等待释放锁！");
    }

}
