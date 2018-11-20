package com.codeclen.rarone.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lin
 * @since 2018/11/14.
 */
public class RaroneThreadPool {

    private int threadSize;

    private AtomicInteger threadAlive = new AtomicInteger(0);

    private ExecutorService executorService;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    public RaroneThreadPool(int threadSize) {
        this.threadSize = threadSize;
        executorService = Executors.newFixedThreadPool(threadSize);
    }

    public RaroneThreadPool(int threadSize, ExecutorService executorService){
        this.executorService = executorService;
        this.threadSize = threadSize;
    }

    public void execute(final Runnable runnable) {
        if(threadAlive.get() > this.threadSize){
            try {
                reentrantLock.lock();
                while (threadAlive.get() > this.threadSize){
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        ;
                    }
                }
            }finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                }finally {
                    try {
                        reentrantLock.lock();
                        threadAlive.decrementAndGet();
                        condition.signal();
                    }finally {
                        reentrantLock.unlock();
                    }
                }
            }
        });
    }

    public boolean isShutdown(){
        return executorService.isShutdown();
    }

    public void shutdown(){
        executorService.shutdown();
    }
}
