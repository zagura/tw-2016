package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 21.04.16.
 */
public class Task {
    final int portionSize;
    enum TaskType{
        CONSUME, PRODUCE
    }
    final TaskType taskType; //Give or take
    final Future result;
    private boolean status;
    public Task(int size, int type){
        if(type == 0){
            taskType = TaskType.CONSUME;
        }else if (type == 1){
            taskType = TaskType.PRODUCE;
        }
        else taskType = null;
        portionSize = size;
        result = new Future();
        status = false;
    }
    public Future getFuture(){
        return this.result;
    }
    public int getType(){
        if(taskType == TaskType.PRODUCE){
            return 1;
        }
        else if(taskType == TaskType.CONSUME){
            return 0;
        }
        return 0;
    }
    public void finish(){
        this.status = true;
    }
    public boolean isFinished(){
        return this.status;
    }
}
