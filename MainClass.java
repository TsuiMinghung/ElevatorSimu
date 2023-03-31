import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;
import test.Generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        try {
            Building building = new Building();
            InputStream is = Files.newInputStream(Paths.get("./stdin.txt"));
            ElevatorInput elevatorInput = new ElevatorInput(is);
            while (true) {
                PersonRequest request = elevatorInput.nextPersonRequest();
                if (request == null) {
                    building.setEnd(true);
                    break;
                } else {
                    building.addRequest(request);
                }
            }
            elevatorInput.close();
        } catch (Exception e) {
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
        } else if (argv[0].equals("debug")) {
            debug();
        }
    }
}
