package test;

public enum ElevState {
    open,
    close,
    arrive;

    public static ElevState parse(String s) {
        switch (s) {
            case "OPEN":
                return open;
            case "CLOSE":
                return close;
            case "ARRIVE":
                return arrive;
            default:
                System.err.println("invalid State:" + s);
                System.exit(1);
                return null;
        }
    }

    public String toString() {
        switch (this) {
            case open:
                return "OPEN";
            case close:
                return "CLOSE";
            case arrive:
                return "ARRIVE";
            default:
                return null;
        }
    }
}
