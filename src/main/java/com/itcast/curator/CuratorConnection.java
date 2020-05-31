package com.itcast.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

public class CuratorConnection {


    public static void main(String[] args) {
        // 创建连接对象
        CuratorFramework client = CuratorFrameworkFactory.builder()
                // IP地址端口号
                .connectString("192.168.79.31:2181,192.168.79.32:2181,192.168.79.33:2181")
                // 会话超时时间
                .sessionTimeoutMs( 5000)
                // 重连机制
                .retryPolicy(new RetryOneTime(3000))
                // 命名空间
                .namespace("create")
                // 构建连接对象
                .build();
        // 打开连接
        client.start();
        System.out.println(client.isStarted());
        // 关闭连接
        client.close();
    }
}
