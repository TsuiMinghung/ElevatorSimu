package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonCmd extends Cmd {

    private double time;
    private int id;
    private int elevID;
    private int floor;
    private PersonState state;

    public double getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public int getElevID() {
        return elevID;
    }

    public int getFloor() {
        return floor;
    }

    public PersonState getState() {
        return state;
    }

    public PersonCmd(String s) {
        Pattern p = Pattern.compile("\\[(\\d+(\\.\\d+))](.+)-(\\d+)-(\\d+)-(\\d+)");
        Matcher m = p.matcher(s);
        if (m.matches()) {
            time = Double.parseDouble(m.group(1));
            state = PersonState.parse(m.group(3));
            id = Integer.parseInt(m.group(4));
            floor = Integer.parseInt(m.group(5));
            elevID = Integer.parseInt(m.group(6));
        } else {
            System.err.println("invalid cmd:" + s);
            System.exit(1);
        }
    }

    public String toString() {
        return String.format("[%f]%s-%d-%d-%d",time, state,id,floor,elevID);
    }

    public static void main(String[] argv) {
        PersonCmd tmp = new PersonCmd("[11.6610]IN-1-4-1");
        System.out.println(tmp);
    }
}
