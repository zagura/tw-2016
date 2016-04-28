package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 28.04.16.
 */
public class Proxy {
    enum TaskType{
        CONSUME(0),
        PRODUCE(1);
        TaskType(int i){
            type = i;
        }
        private int type;
        int getType(){
            return type;
        }
    }
    private static Future createTask(int size, int type){
        Task t = new Task(size, type);
        ActiveQueue q = ActiveQueue.getInstance();
        q.putTask(t);
        return t.getFuture();
    }
    public static Future consume(int size){
        return createTask(size, TaskType.CONSUME.getType());
    }
    public static Future produce(int size){
        return createTask(size, TaskType.PRODUCE.getType());
    }
}
