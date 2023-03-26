package test;

import static test.ElevState.*;
import static test.PersonState.inElev;
import static test.PersonState.outElev;

//check elevator's time door(state) capacity position(floor)
public class ElevSimulator {

    private int floor;
    private double prevTime;
    private ElevState prevState;
    private int capacity;
    private final int id;

    private static final double CLOSE_TIME = 0.2;
    private static final double OPEN_TIME = 0.2;
    private static final double ARRIVE_TIME = 0.4;
    private static final int MAX_CAPACITY = 6;
    private static final int MAX_FLOOR = 11;
    private static final int MIN_FLOOR = 1;

    public ElevSimulator(int id) {
        this.id = id;
        this.floor = 1;
        this.prevTime = 0;
        this.capacity = 0;
        this.prevState = close;
    }

    public int getId() {
        return id;
    }

    public void check(Cmd cmd) {
        if (cmd instanceof ElevCmd) {
            checkMove((ElevCmd) cmd);
        } else if (cmd instanceof PersonCmd) {
            checkInOut((PersonCmd) cmd);
        }
    }

    private void checkMove(ElevCmd cmd) {
        switch (cmd.getState()) {
            case open:
                dealOpen(cmd);
                break;
            case close:
                dealClose(cmd);
                break;
            case arrive:
                dealArrive(cmd);
                break;
            default:
        }
    }

    private void checkInOut(PersonCmd cmd) {
        if (cmd.getState().equals(inElev)) {
            dealIn(cmd);
        } else if (cmd.getState().equals(outElev)) {
            dealOut(cmd);
        }
    }

    //before open: close arrive
    //check:time floor
    //update:time state
    private void dealOpen(ElevCmd cmd) {
        if (prevState.equals(open)) {
            error(cmd,"elev already open and open again");
        } else {
            prevTime = cmd.getTime();
            prevState = open;
        }
        checkCurFloor(cmd);
    }

    //before close:open
    //check:time floor
    //update:time state
    private void dealClose(ElevCmd cmd) {
        if (!prevState.equals(open)) {
            if (cmd.getTime() - prevTime < CLOSE_TIME + OPEN_TIME) {
                error(cmd,"close to fast!");
            }
            prevTime = cmd.getTime();
            prevState = close;
        } else {
            error(cmd,"elev already close and close again");
        }
        checkCurFloor(cmd);
    }

    private void checkCurFloor(ElevCmd cmd) {
        if (cmd.getFloor() != floor) {
            error(cmd,"error floor");
        }
        checkFloor(cmd);
    }

    private void checkFloor(ElevCmd cmd) {
        if (!(MIN_FLOOR <= cmd.getFloor() && cmd.getFloor() <= MAX_FLOOR)) {
            error(cmd,"floor out of bound");
        }
    }

    private void checkMovFloor(ElevCmd cmd) {
        switch (Math.abs(floor - cmd.getFloor())) {
            case 0:
                error(cmd,"zero move");
                break;
            case 1:
                break;
            default:
                error(cmd,"move more than 1 floor at a time");
        }
        checkFloor(cmd);
    }

    //before arrive: arrive close
    //check:time floor
    //update:time floor state
    private void dealArrive(ElevCmd cmd) {
        if (prevState.equals(open)) {
            error(cmd,"move before close");
        } else {
            checkMovFloor(cmd);
            if (cmd.getTime() - prevTime < ARRIVE_TIME) {
                error(cmd,"move too fast");
            }
            floor = cmd.getFloor();
            prevTime = cmd.getTime();
            prevState = arrive;
        }
    }

    //check: capacity state
    //update: capacity
    private void dealOut(PersonCmd cmd) {
        if (!prevState.equals(open)) {
            error(cmd,"elev didn't open and people out");
        } else {
            capacity -= 1;
            checkCapacity(cmd);
        }
    }

    private void checkCapacity(Cmd cmd) {
        if (capacity < 0) {
            error(cmd,"negative elev capacity");
        } else if (capacity > MAX_CAPACITY) {
            error(cmd,"elev capacity out of bound");
        }
    }

    //check: capacity state
    //update: capacity
    private void dealIn(PersonCmd cmd) {
        if (!prevState.equals(open)) {
            error(cmd,"elev didn't open and people in");
        } else {
            capacity += 1;
            checkCapacity(cmd);
        }
    }

    private void error(Cmd cmd,String message) {
        System.err.println("[" + cmd.getTime() + "]: " + message);
        System.exit(1);
    }
}
