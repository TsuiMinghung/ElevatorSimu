public enum Direction {
    UP,DOWN;

    public Direction negate() {
        if (this.equals(Direction.UP)) {
            return Direction.DOWN;
        } else {
            return Direction.UP;
        }
    }
}
