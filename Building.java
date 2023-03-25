import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class Building {
    private final HashMap<Integer,RequestQueue> floors;
    private final ArrayList<Elevator> elevators;
    public static final int MAXFLOOR = 11;
    public static final int MINFLOOR = 1;

    public Building() {
        this.floors = new HashMap<>();
        for (int i = MINFLOOR; i <= MAXFLOOR; ++i) {
            floors.put(i,new RequestQueue());
        }
        this.elevators = new ArrayList<>();
        for (int i = 1;i <= 6;++i) {
            Elevator elevator = new Elevator(this,i);
            elevators.add(elevator);
            elevator.start();
        }
    }

    public synchronized void setEnd(boolean isEnd) {
        for (Elevator elevator : elevators) {
            elevator.setEnd(true);
        }
        notifyAll();
    }

    public synchronized void addRequest(PersonRequest request) {
        floors.get(request.getFromFloor()).push(request);
        notifyAll();
    }

    public boolean isEmpty() {
        for (RequestQueue requestQueue : floors.values()) {
            if (!requestQueue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public synchronized PersonRequest getMainRequest(int floorNum,Direction direction) {
        PersonRequest result = getRequest(floorNum,direction);
        if (result == null) {
            result = getRequest(floorNum, direction.negate());
            if (result == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private PersonRequest getRequest(int floorNum,Direction direction) {
        PersonRequest result = null;
        for (int i = floorNum;direction == Direction.UP ? i <= MAXFLOOR : i >= MINFLOOR;
             i = (direction == Direction.UP ? i + 1 : i - 1)) {
            result = floors.get(i).tryPoll();
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public RequestQueue floorAt(int floorNum) {
        return floors.get(floorNum);
    }
}
