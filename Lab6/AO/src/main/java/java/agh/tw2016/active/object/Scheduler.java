package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 28.04.16.
 */
public class Scheduler extends Thread implements Runnable{
    private Servant servant;
    private ActiveQueue queue;
    public void setServant(Servant servant){
        this.servant = servant;
    }
    public Scheduler(){
        super();
        this.queue = ActiveQueue.getInstance();
    }
    public void produce(){
        servant.produce(queue.getNextProducer());
    }
    public void consume(){
        servant.consume(queue.getNextConsumer());
    }
    public void run(){
        while(true) {
            if (servant.isEmpty()) {
                this.produce();
            } else if (servant.isFull()) {
                this.consume();
            } else {
                Task task = queue.getNextTask();
                if (task.getType() == 1 && (!task.isFinished())) {
                    servant.produce(task);
                } else if (task.getType() == 0 && (!task.isFinished())) {
                    servant.consume(task);
                }
            }
        }
    }
}
