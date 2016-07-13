package com.lmax.disruptor;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by m.monteiro on 13/07/2016.
 */
public class Runner {
    public static void main(String[] args) throws Exception
    {
        final String pack = "com.lmax.disruptor.sequenced"; // Or any other package
        final String pack2 = "com.lmax.disruptor.queue"; // Or any other package
        final String pack3 = "com.lmax.disruptor.raw"; // Or any other package
        final String pack4 = "com.lmax.disruptor.translator"; // Or any other package
        final String pack5 = "com.lmax.disruptor.workhandler"; // Or any other package

        Runner runner = new Runner();
        Stream.of(getTestClasses(pack), getTestClasses(pack2), getTestClasses(pack3), getTestClasses(pack4), getTestClasses(pack5))
                .flatMap(Function.<Stream<Class>>identity())
                .forEach(runner::Run);
    }

    public void Run(Class aClass) {
        try {
            Object instance = aClass.newInstance();

            Method method = aClass.getMethod("testImplementations");
            method.invoke(instance);
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    private static Stream<Class> getTestClasses(String pack) throws Exception {
        return PackageUtil.getClasses(pack).stream().filter(new Predicate<Class>() {
            @Override
            public boolean test(Class aClass) {
                return (AbstractPerfTestDisruptor.class.isAssignableFrom(aClass) && !aClass.equals(AbstractPerfTestDisruptor.class)) ||
                        (AbstractPerfTestQueue.class.isAssignableFrom(aClass) && !aClass.equals(AbstractPerfTestQueue.class));
            }
        });
    }
}
