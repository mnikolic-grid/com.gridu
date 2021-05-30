package threadpool;

import java.util.ArrayList;

public class ThreadPool {

    BlockingQueue<Runnable> queue;
    ArrayList<Thread> threads = new ArrayList<>();
    volatile boolean isShutDown;

    public ThreadPool(int queueSize, int nThreads) {
        this.queue = new BlockingQueue<>(queueSize, this);
        String threadName;
        TaskExecutor task;
        this.isShutDown = false;

        for (int count=0; count<nThreads; count++){
            threadName = "Thread-"+count;
            task = new TaskExecutor(this.queue, this);
            Thread thread = new Thread(task, threadName);
            thread.start();
            threads.add(thread);
        }
    }

    public void submitTask(Runnable task, int timeDelay) throws InterruptedException{
        Thread.sleep(timeDelay);
        this.queue.enqueue(task);
    }

    public void shutdown() {
        this.isShutDown = true;
    }

}
