package usace.hec.model.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class SimpleBootstrapSampler implements BootstrapSampler {
    private final String[] EventNames;
    public SimpleBootstrapSampler(String[] eventNames){
        EventNames = eventNames;
    }
    @Override
    public String[] sample(int eventCount, long seed){
        List<String> output = new ArrayList<String>();
        Random rng = new Random(seed);
        IntStream stream = rng.ints(eventCount,0, EventNames.length);
        IntConsumer consumer = (n) ->{
            output.add(EventNames[n]);
        };
        stream.forEach(consumer);
        String[] result = output.toArray(String[]::new);
        return result ;
    }
}
