package threadpool;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        ThreadPool executorPool = new ThreadPool(5, 4);

        for(int taskNumber = 1; taskNumber <= 10; taskNumber++){
            Runnable task = () -> {
                try{
                    Thread.sleep(10000);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            };

            executorPool.submitTask(task);

        }
        sleep(1000);
        executorPool.shutdown();
        System.out.println("Shut Downing Thread Pool.");
    }
}
