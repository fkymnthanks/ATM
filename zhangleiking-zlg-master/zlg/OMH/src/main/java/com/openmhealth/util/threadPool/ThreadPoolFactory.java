package com.openmhealth.util.threadPool;

import java.util.Map;

/**
 *
 */
public class ThreadPoolFactory {

    private Map<String, ThreadPool> threadPoolMap;

    public ThreadPool getThreadPool(String threadPoolName) {
        return this.threadPoolMap.get(threadPoolName);
    }
}
