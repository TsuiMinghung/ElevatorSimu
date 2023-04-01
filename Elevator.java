import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Elevator extends Thread {
    private boolean isEnd;
    private boolean needMaintain;
    private final Building building;
    private PersonRequest mainRequest;
    private int floor;
    private final ArrayList<PersonRequest> room;
    private Direction direction;
    private final int id;
    private final double speed;
    private final int capacity;

    public static final int OPENTIME = 200;
    public static final int CLOSETIME = 200;
    public static final int MAXFLOOR = 11;
    public static final int MINFLOOR = 1;
    public static final HashMap<Double,Integer> SPEEDTOLEVEL = new HashMap<Double,Integer>() {
        {
            put(0.2,10);
            put(0.3,8);
            put(0.4,6);
            put(0.5,4);
            put(0.6,2);
        }
    };

    public Elevator(Building building,int id) {
        this.isEnd = building.isEnd();
        this.building = building;
        this.floor = 1;
        this.direction = Direction.UP;
        this.mainRequest = null;
        this.room = new ArrayList<>();
        this.id = id;
        this.speed = 0.4;
        this.capacity = 6;
        this.needMaintain = false;
        setPriority(SPEEDTOLEVEL.get(speed));
    }

    /*
    public Elevator(Building building,ElevatorRequest elevatorRequest) {
        this.isEnd = building.isEnd();
        this.building = building;
        this.floor = elevatorRequest.getFloor();
        this.direction = Direction.UP;
        this.mainRequest = null;
        this.room = new ArrayList<>();
        this.id = elevatorRequest.getElevatorId();
        this.speed = elevatorRequest.getSpeed();
        this.capacity = elevatorRequest.getCapacity();
        this.needMaintain = false;
        setPriority(SPEEDTOLEVEL.get(speed));
    }
     */

    @Override
    public void run() {
        while (true) {
            if (needMaintain) {
                //pullOver();
                return;
            }
            if (isEnd && mainRequest == null && room.isEmpty() && building.isEmpty()) {
                return;
            }
            if (mainRequest == null) {
                //race one request
                mainRequest = building.getMainRequest(floor,direction);
            }
            if (mainRequest == null) {
                //if not get one request ,retry
                continue;
            }
            //get main request ,should move
            if (updateDir()) {
                open();
                out();
                in();
                close();
            }
            move();
        }
    }

    /*
    private void pullOver() {
        if (room.isEmpty()) {
            if (mainRequest != null) {
                building.addRequest(mainRequest);
            }
        } else {
            open();
            boolean flag = !room.contains(mainRequest);
            for (PersonRequest p : room) {
                outPerson(p);
                if (p.getToFloor() != floor) {
                    building.addRequest(new PersonRequest(floor,
                            p.getToFloor(),p.getPersonId()));
                }
            }
            if (flag) {
                building.addRequest(new PersonRequest(mainRequest.getFromFloor(),
                        mainRequest.getToFloor(),mainRequest.getPersonId()));
            }
            mainRequest = null;
            close();
        }
        TimableOutput.println("MAINTAIN_ABLE-" + id);
        building.startAll();
    }
    */

    public int getFloor() {
        return floor;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean needMaintain() {
        return needMaintain;
    }

    public int getElevId() {
        return id;
    }

    private boolean updateDir() {
        direction = (floor == MAXFLOOR ? Direction.DOWN :
                floor == MINFLOOR ? Direction.UP : direction);
        for (PersonRequest p : room) {
            if (p.getToFloor() == floor) {
                return true;
            }
        }
        if (building.floorAt(floor).sameDirection(direction) && room.size() < capacity) {
            return true;
        } else if (mainRequest != null && mainRequest.getFromFloor() == floor
                && direction.sameDirection(mainRequest) && room.size() < capacity) {
            return true;
        }
        if (mainRequest.getFromFloor() == floor) {
            if (capacity <= room.size()) {
                building.addRequest(mainRequest);
                mainRequest = room.get(0);
                return false;
            } else {
                if (building.needContinue(direction,floor)) {
                    return direction.sameDirection(mainRequest);
                } else {
                    if (direction.sameDirection(mainRequest)) {
                        return true;
                    } else {
                        if (room.isEmpty()) {
                            direction = direction.negate();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        } else {
            if (room.isEmpty() && !building.needContinue(direction,floor)) {
                if (mainRequest.getFromFloor() > floor) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
            }
        }
        direction = (floor == MAXFLOOR ? Direction.DOWN :
                floor == MINFLOOR ? Direction.UP : direction);
        return false;
    }

    private void move() {
        if (mainRequest == null  && room.isEmpty()) {
            return;
        }
        if (direction.equals(Direction.UP)) {
            ++floor;
        } else {
            --floor;
        }
        try {
            sleep((long)(speed * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("ARRIVE-%d-%d",floor,id));
    }

    private void open() {
        TimableOutput.println(String.format("OPEN-%d-%d",floor,id));
        try {
            sleep(OPENTIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void in() {
        if (mainRequest != null &&  mainRequest.getFromFloor() == floor &&
                direction.sameDirection(mainRequest)) {
            if (capacity == room.size()) {
                building.addRequest(mainRequest);
                mainRequest = room.get(0);
                return;
            } else {
                inPerson(mainRequest);
                room.add(mainRequest);
            }
        }

        List<PersonRequest> ins = building.floorAt(floor).
                willingPersons(capacity - room.size(),direction);
        room.addAll(ins);
        for (PersonRequest p : ins) {
            inPerson(p);
        }
        if (mainRequest == null) {
            mainRequest = room.isEmpty() ? null : room.get(0);
        }
    }

    private void out() {
        List<PersonRequest> toBeRemoved = new ArrayList<>();
        for (PersonRequest p : room) {
            if (p.getToFloor() == floor) {
                outPerson(p);
                toBeRemoved.add(p);
            }
            if (mainRequest != null && mainRequest.equals(p)) {
                mainRequest = null;
            }
        }
        for (PersonRequest p : toBeRemoved) {
            room.remove(p);
        }
        if (room.isEmpty()) {
            if (!building.needContinue(direction,floor)) {
                if (mainRequest == null) {
                    direction = direction;
                } else if (mainRequest.getFromFloor() > floor) {
                    direction = Direction.UP;
                } else if (mainRequest.getFromFloor() < floor) {
                    direction = Direction.DOWN;
                } else {
                    if (mainRequest.getFromFloor() > mainRequest.getToFloor()) {
                        direction = Direction.DOWN;
                    } else {
                        direction = Direction.UP;
                    }
                }
            }
        }
    }

    private void inPerson(PersonRequest p) {
        TimableOutput.println(String.format("IN-%d-%d-%d",p.getPersonId(),p.getFromFloor(),id));
    }

    private void outPerson(PersonRequest p) {
        TimableOutput.println(String.format("OUT-%d-%d-%d",p.getPersonId(),floor,id));
    }

    private void close() {
        try {
            sleep(CLOSETIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("CLOSE-%d-%d",floor,id));
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public synchronized void maintain() {
        this.needMaintain = true;
    }

}
