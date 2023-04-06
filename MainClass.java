import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.TimableOutput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.MaintainRequest;
import test.Generator;

import java.io.IOException;
import java.io.FileWriter;

public class MainClass {

    public static void normal() {
        Scheduler scheduler = Scheduler.getInstance();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                scheduler.setEnd(true);
                break;
            } else {
                if (request instanceof PersonRequest) {
                    scheduler.addRequest((PersonRequest) request);
                } else if (request instanceof ElevatorRequest) {
                    scheduler.addElevator((ElevatorRequest) request);
                } else if (request instanceof MaintainRequest) {
                    scheduler.maintain(((MaintainRequest) request).getElevatorId());
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
        Scheduler scheduler = Scheduler.getInstance();
        scheduler.addElevator(new ElevatorRequest(7,5,5,0.6,5));
        scheduler.addRequest(new PersonRequest(3,1,1));
        scheduler.setEnd(true);
    }

    public static void main(String[] argv) {
        TimableOutput.initStartTimestamp();

        if (argv.length == 0) {
            normal();
            //debug();
        } else if (argv[0].equals("generate")) {
            generate();
        }
    }
}
