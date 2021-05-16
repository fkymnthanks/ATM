package com.openmhealth.util.threadPool;

import java.util.concurrent.Executor;

/**
 *
 */
public interface ThreadPool {

    Executor getExecutor(int threadSize, int queues);
}
