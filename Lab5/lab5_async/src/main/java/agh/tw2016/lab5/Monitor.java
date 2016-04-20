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
    private Lock bufferLock = new ReentrantLock();
    private Condition takeCondition = bufferLock.newCondition();
    private Condition giveCondition = bufferLock.newCondition();
    private final int size;
    private int bufferCount;
    private int producerPointer;
    private int consumerPointer;
    private int freeBuffer;
    private int[] buffer;
    public Monitor(int size) {
        this.size = size;
        this.bufferCount = 0;
        this.freeBuffer = this.size;
        this.producerPointer = 0;
        this.consumerPointer = 0;
        buffer = new int[this.size];
        for(int b: buffer){
            b = 0;
        }
    }

    public int take(long id) throws InterruptedException {
        int pointer = consumerPointer;
        bufferLock.lock();
        try {
            while(buffer[consumerPointer] != 2){
                takeCondition.await();
            }
            System.out.println("Pointer: " + consumerPointer + " - id - " + id + " takes." );
            bufferCount--;
            pointer = consumerPointer;
            buffer[consumerPointer] = 1;
            consumerPointer++;
            consumerPointer %= size;
        } finally {
            bufferLock.unlock();
        }
        return pointer;
    }

    public int give(long id) throws InterruptedException {
        bufferLock.lock();
        int pointer = producerPointer;
        try {
            while(buffer[producerPointer] != 0){
                giveCondition.await();
            }
            System.out.println("Pointer: " + producerPointer + " - id - " + id + " gives.");
            freeBuffer--;
            pointer = producerPointer;
            buffer[producerPointer] = 1;
            producerPointer++;
            producerPointer %= size;
        } finally {
            bufferLock.unlock();
        }
        return pointer;
    }

    public void confirmTake(long id, int index){
        bufferLock.lock();
        try{
            freeBuffer++;
            buffer[index] = 0;
            System.out.println(id + " confirmed take.");
            giveCondition.signal();
        }finally {
            bufferLock.unlock();
        }
    }

    public void confirmGive(long id, int index) {
        bufferLock.lock();
        try {
            bufferCount++;
            buffer[index] = 2;
            System.out.println(id + " confirmed give.");
            takeCondition.signal();
        } finally {
            bufferLock.unlock();
        }
    }
}
