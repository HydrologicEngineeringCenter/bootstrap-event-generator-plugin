package usace.hec.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class BootstrapSampler {
    private final String[] EventNames;
    public BootstrapSampler(String[] eventNames){
        EventNames = eventNames;
    }
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
