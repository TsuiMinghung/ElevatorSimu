import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestQueue {
    private final ArrayList<PersonRequest> requests;

    public RequestQueue() {
        requests = new ArrayList<>();
    }

    public synchronized void push(PersonRequest request) {
        requests.add(request);
        notifyAll();
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




