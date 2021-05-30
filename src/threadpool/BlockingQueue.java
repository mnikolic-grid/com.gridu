package threadpool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class BlockingQueue<Type> {

    private Queue<Type> queue = new LinkedList<Type>();
    ThreadPool threadPool;
    private int EMPTY = 0;
    private int MAX_TASK_IN_QUEUE = 1;


    public BlockingQueue(int queueSize, ThreadPool threadPool) {
        this.MAX_TASK_IN_QUEUE = queueSize;
        this.threadPool = threadPool;
    }

    public synchronized void enqueue(Type task) throws InterruptedException {
        while(this.queue.size() == this.MAX_TASK_IN_QUEUE)
            wait();
        notify();
        this.queue.offer(task);
    }

    public synchronized Type dequeue() throws InterruptedException {

        while(this.queue.size() == EMPTY & !this.threadPool.isShutDown)
            wait(10);
        notify();
        //System.out.println( this.queue.poll());
        return this.queue.poll();
    }

    public int size(){
        return this.queue.size();
    }
}
