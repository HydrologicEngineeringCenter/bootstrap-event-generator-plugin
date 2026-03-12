package usace.hec.model.hs;

import org.junit.Test;

import usace.hec.model.WeightedBootstrapSampler;
import usace.hec.model.hs.hsReader.HydrologicSampling;
public class hsReaderTest {
    @Test
    public void readHSFile() {

        HydrologicSampling hs = hsReader.parse("/workspaces/bootstrap-event-generator-plugin/lib/src/test/resources/wat/weighted-bootstrap-event-generator-from-hsfile/CRT_HS_2020L.hs");
        System.out.print(hs);
    }
    @Test
    public void computeWeightedBootstrapFromHSFile() {

        HydrologicSampling hs = hsReader.parse("/workspaces/bootstrap-event-generator-plugin/lib/src/test/resources/wat/weighted-bootstrap-event-generator-from-hsfile/CRT_HS_2020L.hs");
        Double[] weights = hs.getSeasonHydroEvents().get(0).getHydroEvents().getIncrProbOverride().getWeights();
        String[] names = hs.getSeasonHydroEvents().get(0).getHydroEvents().getIncrProbOverride().getNames();
        WeightedBootstrapSampler wsb = new WeightedBootstrapSampler(names, weights);
        String[] eventNames = wsb.sample(100, 1234);
        for (String eventName : eventNames) {
            System.out.println(eventName);
        }
    }
}


