package com.openmhealth.util.threadPool;

import java.util.concurrent.*;

/**
 *
 */
public class FixedThreadPool implements ThreadPool {

    private Executor executor;

    @Override
    public Executor getExecutor(int threadSize, int queues) {
        if(null == executor) {
            synchronized (this) {
                if(null == executor) {
                    executor = new ThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS, queues == 0 ? new SynchronousQueue<Runnable>() :
                            (queues < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues)),
                            new RejectedExecutionHandler() {
                                @Override
                                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                    String msg = String.format("Thread pool is rejected!" +
                                                    " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),",
                                            Thread.currentThread().getName(), executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getLargestPoolSize(),
                                            executor.getTaskCount(), executor.getCompletedTaskCount());
                                    try {
                                        throw new Exception(msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        }
        return executor;
    }
}
