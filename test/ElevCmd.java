package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElevCmd extends Cmd {
    private ElevState state;
    private int floor;
    private double time;
    private int id;

    public ElevCmd(String s) {
        Pattern p = Pattern.compile("\\[(\\d+(\\.\\d+))](.+)-(\\d+)-(\\d+)");
        Matcher m = p.matcher(s);
        if (m.matches()) {
            time = Double.parseDouble(m.group(1));
            state = ElevState.parse(m.group(3));
            floor = Integer.parseInt(m.group(4));
            id = Integer.parseInt(m.group(5));
        } else {
            System.err.println("unmatch output:" + s);
            System.exit(1);
        }
    }

    public String toString() {
        return String.format("[%f]%s-%d-%d",time,state,floor,id);
    }

    public ElevState getState() {
        return state;
    }

    public int getFloor() {
        return floor;
    }

    public double getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public static void main(String[] argv) {
        ElevCmd tmp = new ElevCmd("[11.0480]ARRIVE-3-1");
        System.out.println(tmp);
    }
}
