package com.openmhealth.client;

import com.openmhealth.util.constant.Constants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class CnxnManager {

    private static final ReentrantLock lock = new ReentrantLock();

    private static final Condition connected = lock.newCondition();

    // ScheduledExecutorService将定时任务和线程池结合使用
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 单例模式，饿汉式（无lazy loading），类加载时就完成实例化
     */
    private final static CnxnManager instance = new CnxnManager();

    static {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    //
                }
            }
        }, Constants.RECONNECT_TIME_SECONDS, TimeUnit.SECONDS);
    }

    private CnxnManager() {
        // 每个Java程序都会有一个Runtime实例，该类会被自动创建
        // addShutdownHook是关闭虚拟机时触发的动作(比如可以用于清理资源之类的)
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }, "ShutdownHook"));
    }

    public static CnxnManager getInstance() {
        return instance;
    }


    public static void addConnectedHandler() {
        signalAvailableHandler();
    }

    private static void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(1000, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }
}
