package usace.hec.model.bootstrap;

public interface BootstrapSampler {
    public String[] sample(int eventCount, long seed);
    
    public default BootstrapSampler bootstrap(int equivalentYearsofRecord, long seed){
        return new SimpleBootstrapSampler(this.sample(equivalentYearsofRecord, seed));
    }
}
