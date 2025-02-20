import com.oocourse.elevator3.PersonRequest;

public enum Direction {
    UP,DOWN;

    public Direction negate() {
        if (this.equals(Direction.UP)) {
            return Direction.DOWN;
        } else {
            return Direction.UP;
        }
    }

    public boolean sameDirection(PersonRequest personRequest) {
        if (this.equals(Direction.UP)) {
            return personRequest.getToFloor() > personRequest.getFromFloor();
        } else {
            return personRequest.getFromFloor() > personRequest.getToFloor();
        }
    }
}
