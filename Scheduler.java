
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.ElevatorRequest;

import java.util.Collection;
import java.util.HashMap;

public class Scheduler {

    public static final int MAXFLOOR = 11;
    public static final int MINFLOOR = 1;

    private static Scheduler INSTANCE = null;

    public static Scheduler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Scheduler();
        }
        return INSTANCE;
    }

    private boolean isEnd;
    private final HashMap<Integer, Floor> floors;
    private final HashMap<Integer,Elevator> elevators;
    private final HashMap<Integer,PersonRequest> originRequests;
    private final HashMap<Integer,Integer> currentFloors;
    private int finishCount;

    private Scheduler() {
        this.isEnd = false;
        this.floors = new HashMap<>();
        for (int i = MINFLOOR; i <= MAXFLOOR; ++i) {
            floors.put(i,new Floor());
        }
        this.elevators = new HashMap<>();
        for (int i = 1;i <= 6;++i) {
            Elevator elevator = new Elevator(this,i);
            elevators.put(i,elevator);
            elevator.start();
        }
        this.originRequests = new HashMap<>();
        this.currentFloors = new HashMap<>();
        this.finishCount = 0;
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

    public PersonRequest split(PersonRequest p,int targetFloor) {
        return new PersonRequest(currentFloors.get(p.getPersonId()),targetFloor,p.getPersonId());
    }

    public void finishRequest(PersonRequest p) {
        currentFloors.put(p.getPersonId(),p.getToFloor());
        PersonRequest origin = originRequests.get(p.getPersonId());
        if (p.getToFloor() != origin.getToFloor()) {
            addRequest(new PersonRequest(p.getToFloor(),origin.getToFloor(),p.getPersonId()));
        }
    }

    public synchronized void addRequest(PersonRequest request) {
        if (!originRequests.containsKey(request.getPersonId())) {
            originRequests.put(request.getPersonId(),request);
            currentFloors.put(request.getPersonId(), request.getFromFloor());
        }
        floors.get(request.getFromFloor()).push(request);
        notifyAll();
    }

    public synchronized PersonRequest getMainRequest(Elevator elevator) {
        if (!hasReachable(elevator)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PersonRequest result = getRequest(elevator,false);
        if (result == null) {
            result = getRequest(elevator,true);
        }
        return result;
    }

    private PersonRequest getRequest(Elevator elevator,boolean reverse) {
        int floorNum = elevator.getFloor();
        Direction direction = elevator.getDirection();
        direction = reverse ? direction.negate() : direction;
        PersonRequest result = null;
        for (int i = floorNum;direction == Direction.UP ? i <= elevator.getMaxFloor() :
                i >= elevator.getMinFloor(); i = (direction == Direction.UP ? i + 1 : i - 1)) {
            result = floors.get(i).tryPoll(elevator);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public Floor floorAt(int floorNum) {
        return floors.get(floorNum);
    }

    public synchronized boolean needContinue(Elevator elevator) {
        Direction dir = elevator.getDirection();
        int floorNum = elevator.getFloor();
        if (dir.equals(Direction.UP)) {
            for (int i = floorNum;i <= elevator.getMaxFloor();++i) {
                if (floors.get(i).hasReachable(elevator)) {
                    return true;
                }
            }
        } else {
            for (int i = floorNum;i >= elevator.getMinFloor();--i) {
                if (floors.get(i).hasReachable(elevator)) {
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

    public synchronized boolean hasReachable(Elevator elevator) {
        for (Floor floor : floors.values()) {
            if (floor.hasReachable(elevator)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void finishTask() {
        finishCount += 1;
        while (finishCount < elevators.size()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    public synchronized void restart() {
        finishCount -= 1;
        notifyAll();
    }

    public Collection<Elevator> getElevators() {
        return elevators.values();
    }

    public synchronized boolean isEmpty() {
        for (Floor floor : floors.values()) {
            if (!floor.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
