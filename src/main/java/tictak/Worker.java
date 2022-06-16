package tictak;

public class Worker extends Thread {

    private int id;
    private Data data;

    public Worker(int id, Data d) {
        this.id = id;
        data = d;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        try {
            synchronized (data) {
                for (int i = 0; i < 5; i++) {
                    while (id != data.getState()) {
                        data.wait();
                    }

                    if (id == 1) data.Tic();
                    else data.Tak();
                    data.notify();
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
