
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final ArrayList<PersonRequest> requests;
    private int serving;
    private int picking;
    private static final int MX = 4;
    private static final int NX = 2;

    public Floor() {
        requests = new ArrayList<>();
        serving = 0;
        picking = 0;
    }

    public synchronized void push(PersonRequest request) {
        requests.add(request);
        notifyAll();
    }

    public synchronized PersonRequest tryPoll(Elevator elevator) {
        PersonRequest result = null;
        for (PersonRequest request : requests) {
            if (elevator.reachable(request.getFromFloor()) && (elevator.carryTo(request) != 0)) {
                result = request;
            }
        }
        if (result != null) {
            requests.remove(result);
            result = Scheduler.getInstance().split(result,elevator.carryTo(result));
        }
        notifyAll();
        return result;
    }

    public synchronized boolean hasSuitable(Elevator elevator) {
        for (PersonRequest request : requests) {
            if (elevator.reachable(request.getFromFloor()) && (elevator.carryTo(request) != 0)
                && elevator.getDirection().sameDirection(request)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasReachable(Elevator elevator) {
        for (PersonRequest request : requests) {
            if (elevator.reachable(request.getFromFloor()) && (elevator.carryTo(request) != 0)) {
                return true;
            }
        }
        return false;
    }

    public synchronized List<PersonRequest> willingPersons(Elevator elevator) {
        int available = elevator.getAvailable();
        Direction dir = elevator.getDirection();
        ArrayList<PersonRequest> result = new ArrayList<>();
        ArrayList<PersonRequest> toBeRemoved = new ArrayList<>();
        for (PersonRequest p : requests) {
            if (available <= result.size()) {
                break;
            }
            if (dir.sameDirection(p) && (elevator.carryTo(p) != 0)
                    && elevator.reachable(p.getFromFloor())) {
                toBeRemoved.add(p);
                result.add(Scheduler.getInstance().split(p,elevator.carryTo(p)));
            } else if (dir.sameDirection(p) && (elevator.carryTo(p) != 0)
                    && elevator.reachable(p.getFromFloor())) {
                toBeRemoved.add(p);
                result.add(Scheduler.getInstance().split(p,elevator.carryTo(p)));
            }
        }
        for (PersonRequest p : toBeRemoved) {
            requests.remove(p);
        }
        notifyAll();
        return result;
    }

    public synchronized void serve() {
        while (serving >= MX) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serving += 1;
        notifyAll();
    }

    public synchronized void finishServe() {
        serving -= 1;
        notifyAll();
    }

    public synchronized void onlyPick() {
        while (serving >= MX || picking >= NX) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serving += 1;
        picking += 1;
        notifyAll();
    }

    public synchronized void finishPick() {
        serving -= 1;
        picking -= 1;
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return requests.isEmpty();
    }

}




