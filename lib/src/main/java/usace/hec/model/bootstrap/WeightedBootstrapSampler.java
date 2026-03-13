package usace.hec.model.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;

public class WeightedBootstrapSampler implements BootstrapSampler {
    private final String[] EventNames;
    private final Double[] Weights;
    public WeightedBootstrapSampler(String[] eventNames, Double[] weights){
        EventNames = eventNames;
        Weights = weights;
    }
    @Override
    public String[] sample(int eventCount, long seed){
        List<String> output = new ArrayList<String>();
        Random rng = new Random(seed);
        DoubleStream stream = rng.doubles(eventCount,0.0d, 1.0d);
        DoubleConsumer consumer = (p) ->{
            output.add(EventNames[findIndex(p)]);
        };
        stream.forEach(consumer);
        String[] result = output.toArray(String[]::new);
        return result ;
    }
    private int findIndex(double probability){
        double sum = 0;
        int index = 0;
        for (double weight:Weights) {
            double newSum = sum + weight;
            if (sum <probability && probability <newSum){
                return index;
            }
            index ++;
            sum = newSum;
        }
        return index;
    }
}
