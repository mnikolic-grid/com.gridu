package threadpool;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TestThreadPool {

    private ThreadPool threadPool = new ThreadPool(4,2);
    private ArrayList<Runnable> tasks = new ArrayList<>();

    @Before
    public void createTasks(){
        for(int taskNumber = 1; taskNumber <= 5; taskNumber++){
            Runnable task = () -> {
                try{
                    Thread.sleep(1);
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            };
            tasks.add(task);
        }
    }

    @After
    public void clear(){
        this.tasks.clear();
        this.threadPool.queue = null;
        this.threadPool.threads = null;
        this.threadPool = null;
    }

    @Test
    public void testEmptyQueue() throws InterruptedException {
        for(Runnable t: tasks){
            threadPool.submitTask(t, 1);
        }

        Thread.sleep(1000);
        threadPool.shutdown();
        assertTrue(threadPool.queue.dequeue()==null);
    }

    @Test
    public void testNumberQueue() throws InterruptedException {
        for(Runnable t: tasks){
            threadPool.submitTask(t, 1);
        }
        Thread.sleep(1000);
        assertTrue(threadPool.threads.size()==2);
        threadPool.shutdown();
    }

    @Test
    public void testShutDown() throws InterruptedException {
        for(Runnable t: tasks){
            threadPool.submitTask(t, 1);
        }
        Thread.sleep(1000);
        threadPool.shutdown();
        assertTrue(threadPool.isShutDown==true);
    }
}
