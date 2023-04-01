package test;

import com.oocourse.elevator2.PersonRequest;

import static test.PersonState.inElev;
import static test.PersonState.outElev;

public class PersonSimulator {

    private final PersonRequest request;
    private PersonState prevState;
    private final int id;
    private int finalFloor;

    public PersonSimulator(PersonRequest p) {
        this.request = p;
        finalFloor = p.getFromFloor();
        prevState = outElev;
        this.id = p.getPersonId();
    }

    public int getId() {
        return id;
    }

    public void check(PersonCmd cmd) {
        if (cmd.getState().equals(inElev)) {
            checkIn(cmd);
        } else if (cmd.getState().equals(outElev)) {
            checkOut(cmd);
        }
    }

    //check:state
    //update: state
    private void checkIn(PersonCmd cmd) {
        if (prevState.equals(inElev)) {
            error(cmd,"rein elevator");
        }
        prevState = inElev;
    }

    //check:state
    //update:state final floor
    private void checkOut(PersonCmd cmd) {
        if (prevState.equals(outElev)) {
            error(cmd,"reout elevator");
        }
        prevState = outElev;
        finalFloor = cmd.getFloor();
    }

    public boolean isFinished() {
        return finalFloor == request.getToFloor();
    }

    private void error(Cmd cmd,String message) {
        System.err.println("[" + cmd.getTime() + "]: " + message);
        System.exit(1);
    }
}
