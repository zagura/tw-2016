package agh.tw2016.lab5;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * lab5
 * Created by Michał Zagórski on 19.04.16.
 */
public class Monitor {
    private Lock firstLock = new ReentrantLock();
    private Lock takeLock = new ReentrantLock();
    private Lock giveLock = new ReentrantLock();
    private Condition takeCondition = firstLock.newCondition();
    private Condition giveCondition = firstLock.newCondition();
    private Condition takeOtherCondition = takeLock.newCondition();
    private Condition giveOtherCondition = giveLock.newCondition();
    private boolean isFirstConsumer = false;
    private boolean isFirstProducer = false;
    private final int size;
    private int bufferCount;
    private int freeBuffer;
    public Monitor(int size) {
        this.size = 2*size;
        this.bufferCount = 0;
        freeBuffer = this.size;
    }

    public void take(long id, int portion) throws InterruptedException {
        takeLock.lock();
        try {
            while(isFirstConsumer){
                takeOtherCondition.await();
            }
            firstLock.lock();
            try {
                System.out.println("First in take queue: " + id);
                isFirstConsumer = true;
                while(bufferCount < portion){
                    takeCondition.await();
                }
                System.out.println("Size: " + bufferCount + " - id - " + id +
                    " takes: " + portion);
                bufferCount -= portion;
                freeBuffer += portion;
                giveCondition.signal();
                isFirstConsumer = false;
            } finally {
                firstLock.unlock();
            }

            takeOtherCondition.signal();
        } finally {
            takeLock.unlock();
        }
    }
    public void give(long id, int portion) throws InterruptedException {
        giveLock.lock();
        try {
            while(isFirstProducer){
                giveOtherCondition.await();
            }
            firstLock.lock();
            try {
                System.out.println("First in give queue: " + id);
                isFirstProducer = true;
                while(freeBuffer < portion){
                    giveCondition.await();

                }
                System.out.println("Size: " + bufferCount + " - id - " + id +
                        " gives: " + portion);
                bufferCount += portion;
                freeBuffer -= portion;
                takeCondition.signal();
                isFirstProducer = false;

            } finally {
                firstLock.unlock();
            }
            giveOtherCondition.signal();
        } finally {
            giveLock.unlock();
        }
    }
}
