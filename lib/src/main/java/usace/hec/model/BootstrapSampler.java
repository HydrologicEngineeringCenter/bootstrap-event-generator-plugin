package usace.hec.model;

public interface BootstrapSampler {
    public String[] sample(int eventCount, long seed);
}
