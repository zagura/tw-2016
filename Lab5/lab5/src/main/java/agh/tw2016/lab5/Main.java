package agh.tw2016.lab5;

/**
 * lab5
 * Created by Michał Zagórski on 19.04.16.
 */
public class Main {
    public static int maxBatch = 10;
    public static void main(String[] args) {
        int count = 5;
        int producerCount = 5;
        int consumerCount = 5;
        Monitor buffer = new Monitor(maxBatch);
        Producer producers[] = new Producer[producerCount];
        Consumer consumers[] = new Consumer[consumerCount];
        for(int i = 0; i < count; i++){
            if(i < producerCount){
                producers[i] = new Producer(maxBatch, buffer);
            }
            if(i < consumerCount){
                consumers[i] = new Consumer(maxBatch, buffer);
            }
        }
        System.out.println("Threads created");
        for(int i = 0; i < count; i++){
            if(i < producerCount){
                producers[i].start();
            }
            if(i < consumerCount){
                consumers[i].start();
            }
        }
/*        try {
            for (int i = 0; i < count; i++) {
                if (i < producerCount) {
                    producers[i].join();
                }
                if (i < consumerCount) {
                    consumers[i].join();
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
