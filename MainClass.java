import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.TimableOutput;
import test.Generator;

import java.io.IOException;
import java.io.FileWriter;

import static java.lang.Thread.sleep;

public class MainClass {

    public static void normal() {
        Building building = new Building();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                building.setEnd(true);
                break;
            } else {
                if (request instanceof PersonRequest) {
                    building.addRequest((PersonRequest) request);
                } else if (request instanceof ElevatorRequest) {
                    building.addElevator((ElevatorRequest) request);
                } else if (request instanceof MaintainRequest) {
                    building.maintain(((MaintainRequest) request).getElevatorId());
                }
            }
        }

        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generate() {
        String path = "/home/alex/study/OO/U2/hw/homework_5/ 面向对象第二单元数据投喂包/stdin.txt";
        try {
            FileWriter writer = new FileWriter(path);
            Generator generator = Generator.getInstance();
            generator.edgeData1().forEach(s ->
                {
                    try {
                        writer.write(s + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void debug() {
        Building building = new Building();
        building.addElevator(new ElevatorRequest(8,9,8,0.2));
        building.addRequest(new PersonRequest(10,8,1));
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        building.maintain(1);
        building.setEnd(true);
    }

    public static void main(String[] argv) {
        TimableOutput.initStartTimestamp();

        if (argv.length == 0) {
            normal();
//            debug();
        } else if (argv[0].equals("generate")) {
            generate();
        } else if (argv[0].equals("debug")) {
            debug();
        }
    }
}
