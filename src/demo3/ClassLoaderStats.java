package demo3;

import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class ClassLoaderStats extends Tool {

    @Override
    public void run() {
        Map<Oop, LongAdder> map = new HashMap<>();
        VM.getVM().getSystemDictionary().classesDo((klass,loader) -> {
            map.computeIfAbsent(loader, oop -> new LongAdder()).increment();
        });

        map.entrySet()
                .stream()
                .sorted((o1,o2) -> (int) (o2.getValue().sum() - o1.getValue().sum()))
                .forEach(entry -> print(entry.getKey(), entry.getValue().sum()));
    }

    void print(Oop loader, long sum) {
        System.out.printf("%6d  %s\n", sum, loader == null
                ? "Bootstrap ClassLoader"
                : loader.getKlass().getName().asString());
    }

    public static void main(String[] args) {
        new ClassLoaderStats().execute(args);
    }
}
