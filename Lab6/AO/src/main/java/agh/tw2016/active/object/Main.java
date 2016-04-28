package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 21.04.16.
 */
public class Main {
    public static void main(String[] args) {
        Thread poll[] = new Thread[20];
        Scheduler s = new Scheduler();
        Servant servant = new Servant(s, 40);
        s.setServant(servant);
        for(int i = 0; i< 10; i++){
            poll[i] = new Thread(new Producer(20, new Proxy()));
            poll[i+10] = new Thread(new Consumer(20, new Proxy()));
            poll[i].start();
            poll[i+10].start();
        }
        s.start();
        s.run();
        try {
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
