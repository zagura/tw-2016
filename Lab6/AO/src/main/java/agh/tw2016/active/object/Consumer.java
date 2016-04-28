package agh.tw2016.active.object;

import java.util.Random;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

/**
 * ActiveObject
 * Created by Michał Zagórski on 28.04.16.
 */
public class Consumer implements Runnable {
    int size;
    Proxy proxy;
    public Consumer(int size, Proxy proxy){
        this.size = size;
        this.proxy = proxy;
    }
    @Override
    public void run() {
        Random r = new Random();
        System.out.println("Consumer started");
        while(true){
            Future f = proxy.consume(r.nextInt(size - 1) + 1);
            long t1 = nanoTime();
            while(!f.isReady()){
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long t2 = nanoTime();
            System.out.println(f.getResult() + "\n\tTime elapsed:  " + (t2-t1) + ". ");
        }
    }
}
