import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;
import test.Generator;

import java.io.IOException;
import java.io.FileWriter;

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
                /*
                if (request instanceof PersonRequest) {
                    building.addRequest((PersonRequest) request);
                } else if (request instanceof ElevatorRequest) {
                    building.addElevator((ElevatorRequest) request);
                } else if (request instanceof MaintainRequest) {
                    building.maintain(((MaintainRequest) request).getElevatorId());
                }
                */
                building.addRequest(request);
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

    public static void main(String[] argv) {
        TimableOutput.initStartTimestamp();

        if (argv.length == 0) {
            normal();
        } else if (argv[0].equals("generate")) {
            generate();
        }
    }
}
