import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;

import java.io.IOException;

public class MainClass {

    public static void normal() {
        Building building = new Building();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                building.setEnd(true);
                break;
            } else {
                building.addRequest(request);
            }
        }

        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debug() {
        Building building = new Building();
        building.addRequest(new PersonRequest(4,5,1));
        building.setEnd(true);
    }

    public static void main(String[] argv) {
        TimableOutput.initStartTimestamp();
        if (true) {
            normal();
        } else {
            debug();
        }
    }
}
