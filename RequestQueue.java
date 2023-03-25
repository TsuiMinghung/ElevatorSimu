import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class RequestQueue {
    private final ArrayList<PersonRequest> requests;
    private boolean isEnd;

    public RequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void push(PersonRequest request) {
        requests.add(request);
        notifyAll();
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        return requests.isEmpty();
    }

    public synchronized PersonRequest tryPoll() {
        PersonRequest result = null;
        if (!requests.isEmpty()) {
            result = requests.get(0);
            requests.remove(0);
            notifyAll();
        }
        return result;
    }

    public synchronized void remove(PersonRequest request) {
        if (!requests.remove(request)) {
            System.err.println("try to remove nonexistent request");
            exit(1);
        }
        notifyAll();
    }

    public synchronized List<PersonRequest> willingPersons(int capacity,Direction dir) {
        ArrayList<PersonRequest> result = new ArrayList<>();
        for (PersonRequest p : requests) {
            if (capacity >= result.size()) {
                break;
            }
            if (dir.equals(Direction.UP) && (p.getFromFloor() < p.getToFloor())) {
                result.add(p);
            } else if (dir.equals(Direction.DOWN) && (p.getFromFloor() > p.getToFloor())) {
                result.add(p);
            }
        }
        for (PersonRequest p : result) {
            requests.remove(p);
        }
        notifyAll();
        return result;
    }

    public synchronized int size() {
        return requests.size();
    }

    public synchronized boolean sameDirection(Direction dir) {
        for (PersonRequest p : requests) {
            if (dir.equals(Direction.UP)) {
                if (p.getFromFloor() < p.getToFloor()) {
                    return true;
                }
            } else {
                if (p.getFromFloor() > p.getToFloor()) {
                    return true;
                }
            }
        }
        return false;
    }
}




