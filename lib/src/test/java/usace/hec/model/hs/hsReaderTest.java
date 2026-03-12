package usace.hec.model.hs;

import org.junit.Test;

import usace.hec.model.hs.hsReader.HydrologicSampling;
public class hsReaderTest {
    @Test
    public void readHSFile() {

        HydrologicSampling hs = hsReader.parse("/workspaces/bootstrap-event-generator-plugin/lib/src/test/resources/CRT_HS_2020L.hs");
        System.out.print(hs);
    }
}


