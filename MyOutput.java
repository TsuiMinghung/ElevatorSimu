import com.oocourse.elevator2.TimableOutput;

public class MyOutput {
    public static void println(String s) {
        TimableOutput.println(s);
        System.err.println(s);
    }
}
