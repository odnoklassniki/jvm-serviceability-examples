package demo4;

import org.mvel2.MVEL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.LongAdder;

public class ExpressionEval extends Thread {

    public static void main(String[] args) throws Exception {
        String script = new String(Files.readAllBytes(Paths.get("src/demo4/script.mvel")));
        HashMap<String, Object> context = new HashMap<>();
        LongAdder count = new LongAdder();

        new Thread(() -> {
            for (;;) {
                MVEL.eval(script, context);
                count.increment();
            }

        }).start();

        for (;;) {
            Thread.sleep(1000);
            System.out.println(count.sumThenReset());
        }
    }
}
