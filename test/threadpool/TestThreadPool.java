package threadpool;


import org.junit.Test;

public class TestThreadPool {

    @Test
    public void testThreadPool() throws InterruptedException {
        ThreadPool threadPool = new ThreadPool(5, 9);
        for(int taskNumber = 1; taskNumber <= 7; taskNumber++){
            TestTask task = new TestTask(taskNumber);
            threadPool.submitTask(task);
        }
    }
}
