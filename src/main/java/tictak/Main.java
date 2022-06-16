package tictak;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();

        Worker firstWorker = new Worker(1, d);
        Worker secondWorker = new Worker(2, d);

        secondWorker.join();
        System.out.println("end of main...");
    }
}
