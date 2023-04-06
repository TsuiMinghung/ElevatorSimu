import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.ElevatorRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Elevator extends Thread {
    private boolean isEnd;
    private boolean needMaintain;
    private final Scheduler scheduler;
    private PersonRequest mainRequest;
    private int floor;
    private final ArrayList<PersonRequest> room;
    private Direction direction;
    private final int id;
    private final double speed;
    private final int capacity;
    private final int access;

    public static final int OPENTIME = 200;
    public static final int CLOSETIME = 200;
    private int maxFloor;
    private int minFloor;
    public static final HashMap<Double,Integer> SPEEDTOLEVEL = new HashMap<Double,Integer>() {
        {
            put(0.2,10);
            put(0.3,8);
            put(0.4,6);
            put(0.5,4);
            put(0.6,2);
        }
    };

    public Elevator(Scheduler scheduler, int id) {
        this.isEnd = scheduler.isEnd();
        this.scheduler = scheduler;
        this.floor = 1;
        this.direction = Direction.UP;
        this.mainRequest = null;
        this.room = new ArrayList<>();
        this.id = id;
        this.speed = 0.4;
        this.capacity = 6;
        this.needMaintain = false;
        this.access = Integer.parseInt("11111111111",2);
        this.minFloor = 1;
        this.maxFloor = 11;
        setPriority(SPEEDTOLEVEL.get(speed));
    }

    public Elevator(Scheduler scheduler, ElevatorRequest elevatorRequest) {
        this.isEnd = scheduler.isEnd();
        this.scheduler = scheduler;
        this.floor = elevatorRequest.getFloor();
        this.direction = Direction.UP;
        this.mainRequest = null;
        this.room = new ArrayList<>();
        this.id = elevatorRequest.getElevatorId();
        this.speed = elevatorRequest.getSpeed();
        this.capacity = elevatorRequest.getCapacity();
        this.needMaintain = false;
        this.access = elevatorRequest.getAccess();
        this.minFloor = 1;
        for (int i = 1;i <= 11;++i) {
            if (reachable(i)) {
                minFloor = i;
                break;
            }
        }
        this.maxFloor = 11;
        for (int i = 11;i >= 1;--i) {
            if (reachable(i)) {
                maxFloor = i;
                break;
            }
        }
        setPriority(SPEEDTOLEVEL.get(speed));
    }

    @Override
    public void run() {
        while (true) {
            if (needMaintain) {
                pullOver();
                return;
            }
            if (isEnd && mainRequest == null && room.isEmpty() && !scheduler.hasReachable(this)) {
                scheduler.finishTask();
                return;
            }
            if (mainRequest == null) {
                //race one request
                mainRequest = scheduler.getMainRequest(this);
            }
            if (mainRequest == null) {
                //if not get one request ,retry
                continue;
            }
            //get main request ,should move
            if (updateDir()) {
                boolean onlyPick = (outCount() == 0);
                if (onlyPick) {
                    onlyPick();
                } else {
                    serve();
                }
                open();
                out();
                in();
                close();
                if (onlyPick) {
                    finishPick();
                } else {
                    finishServe();
                }
            }
            move();
        }
    }

    private void pullOver() {
        if (room.isEmpty()) {
            if (mainRequest != null) {
                scheduler.addRequest(mainRequest);
            }
        } else {
            serve();
            open();
            boolean flag = !room.contains(mainRequest);
            for (PersonRequest p : room) {
                outPerson(p);
                if (p.getToFloor() != floor) {
                    scheduler.addRequest(new PersonRequest(floor,
                            p.getToFloor(),p.getPersonId()));
                }
            }
            if (flag) {
                scheduler.addRequest(mainRequest);
            }
            mainRequest = null;
            close();
            finishServe();
        }
        MyOutput.println("MAINTAIN_ABLE-" + id);
        scheduler.finishTask();
        //scheduler.startAll();
    }

    private void serve() {
        scheduler.floorAt(floor).serve();
    }

    private void finishServe() {
        scheduler.floorAt(floor).finishServe();
    }

    private void onlyPick() {
        scheduler.floorAt(floor).onlyPick();
    }

    private void finishPick() {
        scheduler.floorAt(floor).finishPick();
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getFloor() {
        return floor;
    }

    public int getElevId() {
        return id;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getAvailable() {
        return capacity - room.size();
    }

    private boolean updateDir() {
        direction = (floor == maxFloor ? Direction.DOWN :
                floor == minFloor ? Direction.UP : direction);
        if (!reachable(floor)) {
            return false;
        }
        for (PersonRequest p : room) {
            if (p.getToFloor() == floor) {
                return true;
            }
        }
        if (scheduler.floorAt(floor).hasSuitable(this) && room.size() < capacity) {
            return true;
        } else if (mainRequest != null && mainRequest.getFromFloor() == floor
                && direction.sameDirection(mainRequest) && room.size() < capacity) {
            return true;
        }
        if (mainRequest != null && mainRequest.getFromFloor() == floor) {
            if (capacity <= room.size()) {
                scheduler.addRequest(mainRequest);
                mainRequest = room.get(0);
                return false;
            } else {
                if (scheduler.needContinue(this)) {
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
            if (room.isEmpty() && !scheduler.needContinue(this)) {
                if (mainRequest.getFromFloor() > floor) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
            }
        }
        direction = (floor == maxFloor ? Direction.DOWN :
                floor == minFloor ? Direction.UP : direction);
        return false;
    }

    private int outCount() {
        int result = 0;
        for (PersonRequest p : room) {
            if (p.getToFloor() == floor) {
                result += 1;
            }
        }
        return result;
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
        MyOutput.println(String.format("ARRIVE-%d-%d",floor,id));
    }

    private void open() {
        MyOutput.println(String.format("OPEN-%d-%d",floor,id));
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
                scheduler.addRequest(mainRequest);
                mainRequest = room.get(0);
                return;
            } else {
                inPerson(mainRequest);
                room.add(mainRequest);
            }
        }

        List<PersonRequest> ins = scheduler.floorAt(floor).
                willingPersons(this);
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
            if (!scheduler.needContinue(this)) {
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
        MyOutput.println(String.format("IN-%d-%d-%d",p.getPersonId(),p.getFromFloor(),id));
    }

    private void outPerson(PersonRequest p) {
        scheduler.finishRequest(p);
        MyOutput.println(String.format("OUT-%d-%d-%d",p.getPersonId(),floor,id));
    }

    private void close() {
        try {
            sleep(CLOSETIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MyOutput.println(String.format("CLOSE-%d-%d",floor,id));
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public synchronized void maintain() {
        this.needMaintain = true;
    }

    public boolean reachable(int floorNum) {
        return ((1 << (floorNum - 1)) & access) != 0;
    }

    public int carryTo(PersonRequest request) {
        if (!reachable(request.getFromFloor())) {
            return 0;
        }
        int span = Math.abs(request.getFromFloor() - request.getToFloor());
        for (int i = 0;i < span;++i) {
            if (reachable(request.getToFloor() + i) && reachable(request.getToFloor() - i)) {
                if (request.getToFloor() > request.getFromFloor()) {
                    return request.getToFloor() - i;
                } else {
                    return request.getToFloor() + i;
                }
            } else if (reachable(request.getToFloor() + i)) {
                return request.getToFloor() + i;
            } else if (reachable(request.getToFloor() - i)) {
                return request.getToFloor() - i;
            }
        }
        return 0;
    }

}
