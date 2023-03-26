package test;

public enum PersonState {
    outElev,
    inElev;

    public static PersonState parse(String s) {
        if (s.equals("IN")) {
            return inElev;
        } else if (s.equals("OUT")) {
            return outElev;
        } else {
            System.err.println("invalid opt:" + s);
            System.exit(1);
            return null;
        }
    }

    public String toString() {
        if (this.equals(outElev)) {
            return "OUT";
        } else {
            return "IN";
        }
    }

}
