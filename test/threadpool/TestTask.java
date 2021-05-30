package threadpool;

public class TestTask implements Runnable {
    private int number;
    public TestTask(int number){
        this.number = number;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(100);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}