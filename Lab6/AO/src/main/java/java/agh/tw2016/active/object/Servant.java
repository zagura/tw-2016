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
    private long taskId;
    public Servant(Scheduler scheduler, int size){
        this.scheduler = scheduler;
        count = 0;
        this.size = size;
        readIndex = 0;
        writeIndex = 0;
        buffer = new int[size];
        taskId = 0;
    }
    public boolean isEmpty(){
        return (count == 0);
    }
    public boolean isFull(){
        return (count == size);
    }
    public void consume(Task task){
        int portion = task.portionSize;
        while(portion > count){
            scheduler.produce();
        }
        int index = readIndex;
        for(int i = readIndex; i < readIndex + portion; i++){
            buffer[i%size] = 0;
        }
        count -= portion;
        readIndex += portion;
        readIndex %= size;
        taskId++;
        task.getFuture().setResult(taskId + " Consumer: from " + index + " to " + readIndex + " with size: " + portion);
        task.finish();
    }
    public void produce(Task task){
        int portion = task.portionSize;
        while(portion > (size - count)){
            scheduler.consume();
        }
        int index = writeIndex;
        for(int i = writeIndex; i < readIndex + portion; i++){
            buffer[i%size] = 1;
        }
        count += portion;
        writeIndex += portion;
        writeIndex %= size;
        taskId++;
        task.getFuture().setResult(taskId + " Producer: from " + index + " to " + writeIndex + " with size: " + portion);
        task.finish();
    }

}
