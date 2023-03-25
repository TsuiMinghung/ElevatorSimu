//
//import com.oocourse.elevator1.PersonRequest;
//
//public class Schedule extends Thread {
//    private final Building building;
//
//    public Schedule(Building building) {
//        this.building = building;
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            if (building.isEmpty() && building.isEnd()) {
//                setElevatorsEnd();
//                return;
//            }
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public PersonRequest getMainRequest(int floor,Direction direction) {
//        notifyAll();
//        return building.getMainRequest(floor,direction);
//    }
//
//    public void setElevatorsEnd() {
//        building.setEnd(true);
//        for (Elevator elevator : elevators) {
//            elevator.setEnd(true);
//        }
//    }
//}
