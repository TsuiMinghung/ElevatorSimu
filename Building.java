
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.ElevatorRequest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Building {
    private final HashMap<Integer,RequestQueue> floors;
    private final HashMap<Integer,Elevator> elevators;
    public static final int MAXFLOOR = 11;
    public static final int MINFLOOR = 1;

    private boolean isEnd;

    public Building() {
        this.isEnd = false;
        this.floors = new HashMap<>();
        for (int i = MINFLOOR; i <= MAXFLOOR; ++i) {
            floors.put(i,new RequestQueue());
        }
        this.elevators = new HashMap<>();
        for (int i = 1;i <= 6;++i) {
            Elevator elevator = new Elevator(this,i);
            elevators.put(i,elevator);
            elevator.start();
        }
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        for (Elevator elevator : elevators.values()) {
            elevator.setEnd(isEnd);
        }
        notifyAll();
    }

    public boolean isEnd() {
        return isEnd;
    }

    public synchronized void addRequest(PersonRequest request) {
        floors.get(request.getFromFloor()).push(request);
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        for (RequestQueue requestQueue : floors.values()) {
            if (!requestQueue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public synchronized PersonRequest getMainRequest(int floorNum,Direction direction) {
        if (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PersonRequest result = getRequest(floorNum,direction);
        if (result == null) {
            result = getRequest(floorNum, direction.negate());
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

    public synchronized boolean needContinue(Direction dir,int floorNum) {
        if (dir.equals(Direction.UP)) {
            for (int i = floorNum;i <= MAXFLOOR;++i) {
                if (!floors.get(i).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int i = floorNum;i >= MINFLOOR;--i) {
                if (!floors.get(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized void addElevator(ElevatorRequest elevatorRequest) {
        Elevator elevator = new Elevator(this,elevatorRequest);
        elevator.start();
        elevators.put(elevator.getElevId(),elevator);
        notifyAll();
    }

    public synchronized void maintain(int id) {
        elevators.get(id).maintain();
        notifyAll();
    }

    public synchronized void startAll() {
        List<Elevator> tmp = new ArrayList<>(elevators.values());
        tmp.sort(new SortBySpeed());
        for (Elevator e : tmp) {
            if (!e.isAlive() && !e.needMaintain()) {
                Elevator elevator = new Elevator(this,new ElevatorRequest(e.getElevId(),
                        e.getFloor(),e.getCapacity(),e.getSpeed()));
                elevators.put(e.getElevId(),elevator);
                elevator.start();
            }
        }
        notifyAll();
    }

    private static class SortBySpeed implements Comparator<Elevator> {
        public int compare(Elevator e1,Elevator e2) {
            return Double.compare(e1.getSpeed(),e2.getSpeed());
        }
    }

}
