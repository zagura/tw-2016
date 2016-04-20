package agh.tw2016.lab5;

import java.util.Random;

/**
 * lab5
 * Created by Michał Zagórski on 19.04.16.
 */
public class Consumer extends Thread implements Runnable{
    private final int size;
    private final Random random;
    private final Monitor monitor;
    public Consumer(int size, Monitor monitor) {
        super();
        this.size = size;
        this.monitor = monitor;
        this.random = new Random();
    }
    public void run() {
        while(true){
            try{
                int pointer = monitor.take(getId());
                sleep(random.nextInt(990) + 10);
                monitor.confirmTake(getId(), pointer);
            }catch(InterruptedException inter){
                inter.getStackTrace();
            }
        }
    }
}
