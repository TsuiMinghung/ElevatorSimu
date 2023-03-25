import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;
import java.util.List;

public class Elevator extends Thread {
    private boolean isEnd;
    private final Building building;
    private PersonRequest mainRequest;
    private int floor;
    private final ArrayList<PersonRequest> room;
    private Direction direction;
    private final int id;

    public static final int CAPACITY = 6;
    public static final int MOVETIME = 400;
    public static final int OPENTIME = 200;
    public static final int CLOSETIME = 200;

    public Elevator(Building building,int id) {
        this.isEnd = false;
        this.building = building;
        this.floor = 1;
        this.direction = Direction.UP;
        this.mainRequest = null;
        this.room = new ArrayList<>();
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
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
            open();
            out();
            in();
            close();
            updateDir();
            move();
        }
    }

    private void move() {
        if (room.isEmpty()) {
            return;
        }
        if (direction.equals(Direction.UP)) {
            ++floor;
        } else {
            --floor;
        }
        TimableOutput.println(String.format("ARRIVE-%d-%d",floor,id));
        try {
            sleep(MOVETIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        if (mainRequest != null &&  mainRequest.getFromFloor() == floor) {
            if (CAPACITY == room.size()) {
                building.addRequest(mainRequest);
                mainRequest = room.get(0);
                return;
            } else {
                inPerson(mainRequest);
                room.add(mainRequest);
            }
        }

        List<PersonRequest> ins = building.floorAt(floor).
                willingPersons(CAPACITY - room.size(),direction);
        room.addAll(ins);
        for (PersonRequest p : ins) {
            inPerson(p);
        }
    }

    private void out() {
        List<PersonRequest> toBeRemoved = new ArrayList<>();
        boolean flag = false;
        for (PersonRequest p : room) {
            if (p.getToFloor() == floor) {
                outPerson(p);
                toBeRemoved.add(p);
            }
            if (mainRequest.equals(p)) {
                flag = true;
            }
        }
        for (PersonRequest p : toBeRemoved) {
            room.remove(p);
        }
        if (flag) {
            if (room.isEmpty()) {
                mainRequest = null;
            } else {
                mainRequest = room.get(0);
            }
        }
    }

    private void inPerson(PersonRequest p) {
        TimableOutput.println(String.format("IN-%d-%d-%d",p.getPersonId(),p.getFromFloor(),id));
    }

    private void outPerson(PersonRequest p) {
        TimableOutput.println(String.format("OUT-%d-%d-%d",p.getPersonId(),p.getToFloor(),id));
    }

    private void close() {
        try {
            sleep(CLOSETIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println(String.format("CLOSE-%d-%d",floor,id));
    }

    private void updateDir() {
        if (mainRequest == null) {
            return;
        }
        if (mainRequest.getToFloor() > floor) {
            direction = Direction.UP;
        } else if (mainRequest.getToFloor() < floor) {
            direction = Direction.DOWN;
        }
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
}
