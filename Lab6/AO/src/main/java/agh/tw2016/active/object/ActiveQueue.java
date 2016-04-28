package agh.tw2016.active.object;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ActiveObject
 * Created by Michał Zagórski on 21.04.16.
 */
public class ActiveQueue {
    private Lock queueLock = new ReentrantLock();
    private Condition emptyQueue = queueLock.newCondition();
    private Condition emptyConsumers = queueLock.newCondition();
    private Condition emptyProducers = queueLock.newCondition();
    private ArrayDeque<Task> fullQueue;
    private ArrayDeque<Task> consumerQueue;
    private ArrayDeque<Task> producerQueue;
    private boolean emptyConsumerQueue = true;
    private boolean emptyProducerQueue = true;
    private boolean emptyFullQueue = true;

    private static ActiveQueue queue;
    private ActiveQueue(){
        fullQueue = new ArrayDeque<>();
        consumerQueue = new ArrayDeque<>();
        producerQueue = new ArrayDeque<>();
    }
    public static ActiveQueue getInstance(){
        if(queue == null) queue = new ActiveQueue();
        return queue;
    }
    public void putTask(Task task){
        queueLock.lock();
        int condition = -1;
        try{
            fullQueue.addLast(task);
            if(task.getType() == 0){
                consumerQueue.addLast(task);
                if(emptyConsumerQueue){
                    condition = 0;
                }
            }
            else if(task.getType() == 1){
                producerQueue.addLast(task);
                if(emptyProducerQueue){
                    condition = 1;
                }
            }

        }catch(IllegalStateException stateException){
            stateException.printStackTrace();
        }
        if(emptyFullQueue) {
            emptyFullQueue = false;
            emptyQueue.signal();
        }
        if(condition == 0){
            emptyConsumerQueue = false;
            emptyConsumers.signal();
        }
        if(condition == 1){
            emptyProducerQueue = false;
            emptyProducers.signal();
        }
        queueLock.unlock();
    }
    public Task getNextTask(){
        queueLock.lock();
        Task task = null;
        Task taskWithType = null;
        while(emptyFullQueue){
            try {
                emptyQueue.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean rightTask = false;
        while(!rightTask){
            task = fullQueue.pollFirst();
            while(task == null){
                emptyFullQueue = true;
                while(emptyFullQueue){
                    try {
                        emptyQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                task = fullQueue.pollFirst();
            }
            if(!task.isFinished()){
                int type = task.getType();
                if(type == 1){
                    taskWithType = producerQueue.pollFirst();
                }else if(type == 0){
                    taskWithType = consumerQueue.pollFirst();
                }
            }
            if(taskWithType != null && task != null){
                rightTask = true;
            }
        }
        emptyFullQueue = fullQueue.isEmpty();
        emptyConsumerQueue = consumerQueue.isEmpty();
        emptyProducerQueue = producerQueue.isEmpty();
        queueLock.unlock();
        return task;
    }

    public Task getNextConsumer(){
        queueLock.lock();
        Task task = null;
        while(emptyConsumerQueue){
            try {
                emptyConsumers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        task = consumerQueue.poll();
        emptyConsumerQueue = consumerQueue.isEmpty();
        queueLock.unlock();
        return task;
    }

    public Task getNextProducer(){
        queueLock.lock();
        Task producerTask = null;
        while(emptyProducerQueue){
            try {
                emptyProducers.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        producerTask = producerQueue.poll();
        emptyProducerQueue = producerQueue.isEmpty();
        queueLock.unlock();
        return producerTask;
    }
}
