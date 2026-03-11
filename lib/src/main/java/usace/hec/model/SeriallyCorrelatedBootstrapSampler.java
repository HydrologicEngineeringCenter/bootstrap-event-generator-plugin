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
        double previousNormal = standardNormalInverse(previous);
        double errorNormal = standardNormalInverse(current);
        double resultNormal = (correlation*previousNormal)+(Math.sqrt(1-Math.pow(correlation, 2.0d))+errorNormal);
        double result = standardNormalCDF(resultNormal);
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
    private double TrapazoidalIntegration(double y1, double y2, double deltax){
        double deltay = 0;
        double rect = 0;
        if(y1>y2){
            deltay = y1-y2;
            rect = java.lang.Math.abs(y2*deltax);
        }else{
            deltay = y2-y1;
            rect = java.lang.Math.abs(y1*deltax);
        }
        double tri = (1/2)*(deltax*deltay);
        return rect + java.lang.Math.abs(tri);
    }
    private double FindArea(double a, double inc, double x){
        double x1 = standardNormalCDF(a);
        double x2 = standardNormalCDF(a+inc);
        while(x2>=x){
           x1 = x2;
           a += inc;
           x2 = standardNormalInverse(a+inc);
        }
        double y1 = standardNormalPDF(x1);
        double y2 = standardNormalPDF(x2);
        double deltax = java.lang.Math.abs(x1-x2);
        double area = TrapazoidalIntegration(y1,y2,deltax);
        double interpvalue = (x-x1)/(x2-x1);
        a+=area*interpvalue;
        return a;
    }
    public double standardNormalCDF(double value) {
        //decide which method i want to use.  errfunction, the method i came up with in vb, or something else.
        if(value == 0.0d){return .5;}
        double dist = value - 0.0d;
        int stdevs = (int)java.lang.Math.floor(java.lang.Math.abs(dist/1.0d));
        double inc = 1/250;
        double a = 0.5;
        double a1 = 0.682689492137/2;
        double a2 = 0.954499736104/2;
        double a3 = 0.997300203937/2;
        double a4 = 0.999936657516/2;
        double a5 = 0.999999426687/2;
        double a6 = 0.999999998027/2;
        double a7 = (a-a6)/2;
        switch(stdevs){
            case 0:
                if(dist<0){a+=-a1;}
                return FindArea(a,inc,value);
            case 1:
                if(dist<0){
                    a-=a2;
                }else{
                    a+=a1;
                }
                return FindArea(a,inc,value);
            case 2:
                if(dist<0){
                    a-=a3;
                }else{
                    a+=a2;
                }
                return FindArea(a,inc,value);
            case 3:
                if(dist<0){
                    a-=a4;
                }else{
                    a+=a3;
                }
                return FindArea(a,inc,value);
            case 4:
                if(dist<0){
                    a-=a5;
                }else{
                    a+=a4;
                }
                return FindArea(a,inc,value);
            case 5:
                if(dist<0){
                    a-=a6;
                }else{
                    a+=a5;
                }
                return FindArea(a,inc,value);
            case 6:
                if(dist<0){
                    a-=a7;
                }else{
                    a+=a6;
                }
                return FindArea(a,inc,value);
            default:
                if(dist<0){
                    return 0;
                }else{
                    return 1;
                }
        }
    }
    public double standardNormalPDF(double value) {
        return (1/Math.sqrt(2*Math.PI)*Math.pow(1.0d,2.0))*Math.exp((-(Math.pow(value-0.0d, 2)/(2*Math.pow(1.0d, 2)))));
    }
}
