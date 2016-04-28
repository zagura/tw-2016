package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 28.04.16.
 */
public class Servant {
    private final Scheduler scheduler;
    private int[] buffer;
    private int size;
    private int count;
    private int readIndex;
    private int writeIndex;
    public Servant(Scheduler scheduler, int size){
        this.scheduler = scheduler;
        count = 0;
        this.size = size;
        readIndex = 0;
        writeIndex = 0;
        buffer = new int[size];
    }
    public boolean isEmpty(){
        return (count == 0);
    }
    public boolean isFull(){
        return (count == size);
    }
    public void consume(Task task){
        int portion = task.portionSize;
        int index = readIndex;
        for(int i = readIndex; i < readIndex + portion; i++){
            buffer[i%size] = 0;
        }
        readIndex += portion;
        readIndex %= size;
        task.getFuture().setResult("Consumer: from " + index + " to " + readIndex + " with size: " + portion);
        task.finish();
    }
    public void produce(Task task){
        int portion = task.portionSize;
        int index = writeIndex;
        for(int i = writeIndex; i < readIndex + portion; i++){
            buffer[i%size] = 1;
        }
        writeIndex += portion;
        writeIndex %= size;
        task.getFuture().setResult("Producer: from " + index + " to " + writeIndex + " with size: " + portion);
        task.finish();
    }

}
