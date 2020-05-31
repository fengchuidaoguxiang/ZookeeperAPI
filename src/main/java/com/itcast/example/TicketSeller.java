package com.itcast.example;

import java.util.concurrent.TimeUnit;

public class TicketSeller {

    private void sell(){
        System.out.println("售票开始");
        //  线程随机休眠数秒，模拟现实中的费时操作
        int sleepTime = 5;
        try {
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    public void sellTicketWithLock() throws Exception{
        MyLock lock = new MyLock();
        // 获取锁
        lock.acquireLock();
        sell();
        // 释放锁
        lock.releaseLock();
    }

    public static void main(String[] args) throws Exception{
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }
}
