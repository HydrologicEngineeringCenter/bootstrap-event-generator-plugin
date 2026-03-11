package usace.hec.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SeriallyCorrelatedBootstrapSampler implements BootstrapSampler {
    private final String[] EventNames;
    private final Double[] Weights;
    private final double SerialCorrelation;
    public SeriallyCorrelatedBootstrapSampler(String[] eventNames, Double[] weights, double serialCorrelation){
        EventNames = eventNames;
        Weights = weights;
        SerialCorrelation = serialCorrelation;
    }
    @Override
    public String[] sample(int eventCount, long seed){
        List<String> output = new ArrayList<String>();
        Random rng = new Random(seed);
        //DoubleStream stream = rng.doubles(eventCount,0.0d, 1.0d);
        double previous = 0.0;//random initalization
        for(int i = 0; i<eventCount;i++){
            double current = rng.nextDouble();
            if(i!=0){
                current = correlatedSample(previous, current, SerialCorrelation);
            }
            output.add(EventNames[findIndex(current)]);
            previous = current;
        }
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
    private double correlatedSample(double previous, double current, double correlation){
        //https://www.cmu.edu/biolphys/deserno/pdf/corr_gaussian_random.pdf
        double result = correlation*previous+Math.sqrt(1-Math.pow(correlation, 2.0d))+standardNormalInverse(current);
        return result;
    }
/*substitute the logic below out for hec.statistics eventually */
    private double standardNormalInverse(double probability) {
        int i;
        double x;
        double c0 = 2.515517;
        double c1 = .802853;
        double c2 = .010328;
        double d1 = 1.432788;
        double d2 = .189269;
        double d3 = .001308;
        double q;
        q = probability;
        if(q==.5){return 0.0d;}
        if(q<=0){q=.000000000000001;}
        if(q>=1){q=.999999999999999;}
        if(q<.5){i=-1;}else{
            i=1;
            q = 1-q;
        }
        double t = Math.sqrt(Math.log(1/Math.pow(q, 2)));
        x = t-(c0+c1*t+c2*(Math.pow(t,2)))/(1+d1*t+d2*Math.pow(t,2)+d3*Math.pow(t,3));
        x = i*x;
        return (x*1.0d)+0.0d;
    }  
}
