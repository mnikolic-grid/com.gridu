package threadpool;

public class TaskExecutor implements Runnable {
    BlockingQueue<Runnable> queue;
    ThreadPool threadPool;
    public TaskExecutor(BlockingQueue<Runnable> queue, ThreadPool threadPool){
        this.queue = queue;
        this.threadPool = threadPool;
    }

    @Override
    public void run(){
        try{
            while(!threadPool.isShutDown){
                String name = Thread.currentThread().getName();
                Runnable task;
                while ((task=queue.dequeue())!=null){
                    System.out.println("Run thread "+name);
                    task.run();
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
