import com.oocourse.elevator1.TimableOutput;

public class MyOutput {
    public static synchronized void println(Object obj) {
        TimableOutput.println(obj);
    }
}
