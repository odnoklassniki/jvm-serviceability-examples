package demo4;

import org.mvel2.MVEL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Eval extends Thread {

    public static void main(String[] args) throws Exception {
        String script = new String(Files.readAllBytes(Paths.get("src/demo4/script.mvel")));
        HashMap<String, Object> context = new HashMap<>();
        AtomicLong count = new AtomicLong();

        new Thread(() -> {
            while (true) {
                MVEL.eval(script, context);
                count.incrementAndGet();
            }
        }).start();

        while (true) {
            Thread.sleep(1000);
            System.out.println(count.getAndSet(0));
        }
    }
}
