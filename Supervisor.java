public class Supervisor extends Thread {

    public Supervisor() {

    }

    @Override
    public void run() {
        try {
            sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Elevator elevator : Scheduler.getInstance().getElevators()) {
            if (elevator.isAlive()) {
                System.err.println(elevator);
            }
        }
        System.err.println("finish supervisor");
        System.exit(2);
    }
}
