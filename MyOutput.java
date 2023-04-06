import com.oocourse.elevator3.TimableOutput;

public class MyOutput {
    public static void println(String s) {
        TimableOutput.println(s);
        System.err.println(s);
    }
}
