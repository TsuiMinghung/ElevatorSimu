package test;

import com.oocourse.elevator2.PersonRequest;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
    private static final int DATA_NUM = 70;
    private static final double BEGIN_TIME = 1.0;
    private static final double END_TIME = 50.0;
    private static final int MIN_FLOOR = 1;
    private static final int MAX_FLOOR = 11;

    private static Generator INSTANCE = null;

    public static Generator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Generator();
        }
        return INSTANCE;
    }

    private HashSet<Integer> ids;

    private Generator() {
        ids = new HashSet<>();
    }

    private double randTime() {
        return ThreadLocalRandom.current().nextDouble(BEGIN_TIME,END_TIME);
    }

    private int randID() {
        int tmp = ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        while (ids.contains(tmp)) {
            tmp = ThreadLocalRandom.current().nextInt(1,Integer.MAX_VALUE);
        }
        ids.add(tmp);
        return tmp;
    }

    private int randFloor() {
        return ThreadLocalRandom.current().nextInt(MIN_FLOOR,MAX_FLOOR + 1);
    }

    private PersonRequest randRequest() {
        int from = randFloor();
        int to = randFloor();
        while (from == to) {
            from = randFloor();
        }
        return new PersonRequest(from,to,randID());
    }

    public List<String> generate(int capacity) {
        Map<Double,String> tmp = new TreeMap<>();
        while (tmp.size() < capacity) {
            tmp.put(randTime(),randRequest().toString());
        }
        List<String> result = new ArrayList<>();
        for (Map.Entry<Double,String> entry : tmp.entrySet()) {
            result.add(String.format("[%.1f]",entry.getKey()) + entry.getValue());
        }
        return result;
    }

    public List<String> edgeData1() {
        List<String> result = new ArrayList<>();
        for (int i = 0;i < DATA_NUM;++i) {
            result.add(String.format("[%.1f]",END_TIME) + randRequest());
        }
        return result;
    }

    public List<String> edgeData2() {
        List<String> result = new ArrayList<>();
        result.addAll(generate(DATA_NUM / 2));
        while (result.size() < DATA_NUM) {
            result.add(String.format("[%.1f]",END_TIME) + randRequest());
        }
        return result;
    }

    public static void main(String[] argv) {
        Generator generator = Generator.getInstance();
        generator.edgeData1().forEach(System.out::println
        );
    }
}
