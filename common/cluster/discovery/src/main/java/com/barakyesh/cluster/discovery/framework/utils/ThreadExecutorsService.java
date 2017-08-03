package com.barakyesh.cluster.discovery.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedMap;

/**
 * Created by Barak Yeshoua.
 */
public class ThreadExecutorsService {
    private final static Logger log = LoggerFactory.getLogger(ThreadExecutorsService.class);
    private final static Map<String,ExecutorService> executorServiceMap = synchronizedMap(new HashMap<String, ExecutorService>());


    public static ExecutorService newFixedThreadPool(String threadName, int numberOfThreads) {
        executorServiceMap.putIfAbsent(threadName,Executors.newFixedThreadPool(numberOfThreads, new ExecutorsThreadFactory(threadName)));
        return executorServiceMap.get(threadName);
    }

    public static void closeAndWait(String threadName, long timeout) {
        ExecutorService executorService = executorServiceMap.remove(threadName);
        if(executorService != null){
            executorService.shutdown();
            try {
                boolean isTerminated = executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                if(!isTerminated){
                    log.warn("Failed to wait to thread termination");
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public static void runAsync(Runnable runnable,String threadName) {
        ThreadExecutorsService.newFixedThreadPool(threadName, 1).execute(runnable);
    }

    public static void close(String threadName) {
        ExecutorService executorService = executorServiceMap.remove(threadName);
        if(executorService != null) {
            executorService.shutdown();
        }
    }


    static class ExecutorsThreadFactory implements ThreadFactory {
        private String name;

        ExecutorsThreadFactory(String name) {
            this.name = name;
        }

        public Thread newThread(Runnable r) {
            final Thread newThread = Executors.defaultThreadFactory().newThread(r);
            newThread.setName(name + "-" + newThread.getName());
            return newThread;
        }
    }

    public static void close(){
        executorServiceMap.forEach((s, executorService) -> executorService.shutdown());
    }
}
