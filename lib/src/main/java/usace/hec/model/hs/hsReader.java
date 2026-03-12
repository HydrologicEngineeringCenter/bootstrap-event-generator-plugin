package usace.hec.model.hs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;

import java.util.List;

public class hsReader {
    @JacksonXmlRootElement(localName = "HydrologicSampling")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HydrologicSampling {
        @JacksonXmlProperty(isAttribute = true) private String Name;
        @JacksonXmlProperty(isAttribute = true) private boolean DataOK;
        @JacksonXmlProperty(isAttribute = true) private String Type;
        @JacksonXmlProperty(isAttribute = true) private boolean IsMultipleDuration;

        private String Version;
        private Description Description;
        private HydroRate HydroRate;
        
        @JacksonXmlElementWrapper(localName = "HydroSeasons")
        @JacksonXmlProperty(localName = "HydroSeason")
        private List<HydroSeason> HydroSeasons;

        private String SampledSeason;
        private String NumberSeasons;
        private String SeasonOneName;
        private String SeasonTwoName;
        private double MinTimeBetweenEvents;
        private double ProbabilitySeasonOne;
        private double ProbabilitySeasonTwo;

        @JacksonXmlElementWrapper(localName = "SeasonHydroEvents")
        @JacksonXmlProperty(localName = "SeasonHydroEvent")
        private List<SeasonHydroEvent> SeasonHydroEvents;

        // Getters and Setters
        public String getName() { return Name; }
        public void setName(String name) { Name = name; }
        public List<SeasonHydroEvent> getSeasonHydroEvents() { return SeasonHydroEvents; }
        public void setSeasonHydroEvents(List<SeasonHydroEvent> events) { SeasonHydroEvents = events; }
        // ... (Include other getters/setters for all fields)
    }

    public static class Description {
        private Desc desc;
        public Desc getDesc() { return desc; }
        public void setDesc(Desc desc) { this.desc = desc; }
    }

    public static class Desc {
        @JacksonXmlProperty(isAttribute = true) private int cnt;
        @JacksonXmlProperty(isAttribute = true) private String value;
        // Getters and Setters
    }

    public static class HydroRate {
        @JacksonXmlProperty(isAttribute = true) private String Modality;
        // Getters and Setters
    }

    public static class HydroSeason {
        @JacksonXmlProperty(isAttribute = true) private String SeasonShape;
        @JacksonXmlProperty(isAttribute = true) private String Start;
        @JacksonXmlProperty(isAttribute = true) private String End;
        // Getters and Setters
    }

    public static class SeasonHydroEvent {
        @JacksonXmlProperty(localName = "SeasonName")
        private String SeasonName;
        @JacksonXmlProperty(localName = "HydroEvents")
        private HydroEvents HydroEvents;
        // Getters and Setters
        public HydroEvents getHydroEvents() { return HydroEvents; }
        public void setHydroEvents(HydroEvents hydroEvents) { HydroEvents = hydroEvents; }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HydroEvents {
        @JacksonXmlProperty(isAttribute = true) private String Method;
        @JacksonXmlProperty(isAttribute = true) private boolean HasForecast;
        @JacksonXmlProperty(isAttribute = true) private boolean UseIPOverride;
        @JacksonXmlProperty(isAttribute = true) private boolean ContSim;
        @JacksonXmlProperty(localName = "IncrProbOverride")
        private IncrProbOverride IncrProbOverride;
        // Getters and Setters
        public IncrProbOverride getIncrProbOverride() { return IncrProbOverride; }
        public void setIncrProbOverride(IncrProbOverride override) { IncrProbOverride = override; }
    }

    public static class IncrProbOverride {
        @JacksonXmlProperty(isAttribute = true) private boolean VerifiedIPOverride;
        @JacksonXmlElementWrapper(localName = "IncrProbTable")
        @JacksonXmlProperty(localName = "Entry")
        private List<Entry> entries;
        // Getters and Setters
        public List<Entry> getEntries() { return entries; }
        public void setEntries(List<Entry> entries) { this.entries = entries; }
        public Double[] getWeights(){
            Double[] weights = new Double[entries.size()];
            int idx = 0;
            for (Entry entry : entries) {
                weights[idx] = entry.IncrProb;
                idx ++;
            }
            return weights;
        }
        public String[] getNames(){
            String[] names = new String[entries.size()];
            int idx = 0;
            for (Entry entry : entries) {
                names[idx] = entry.WaterYear;
                idx ++;
            }
            return names;
        }
    }

    public static class Entry {
        @JacksonXmlProperty(isAttribute = true) private String WaterYear;
        @JacksonXmlProperty(isAttribute = true) private double IncrProb;
        // Getters and Setters
        public String getWaterYear() { return WaterYear; }
        public double getIncrProb() { return IncrProb; }
        @Override
        public String toString() {
            return "Year: " + WaterYear + ", Prob: " + IncrProb;
        }
    }

    public static HydrologicSampling parse(String path) {
        try {
            XmlMapper xmlMapper = new XmlMapper();

            // Read from a file
            File file = new File(path);
            HydrologicSampling data = xmlMapper.readValue(file, HydrologicSampling.class);

            // Access the deeply nested list
            List<Entry> table = data.getSeasonHydroEvents().get(0)
                    .getHydroEvents()
                    .getIncrProbOverride()
                    .getEntries();

            table.forEach(System.out::println);
            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
