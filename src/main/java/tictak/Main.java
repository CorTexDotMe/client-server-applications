package tictak;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();

        Worker firstWorker = new Worker(1, d);
        Worker secondWorker = new Worker(2, d);
        Worker thirdWorker = new Worker(3, d);

        thirdWorker.join();
        System.out.println("end of main...");
    }
}
