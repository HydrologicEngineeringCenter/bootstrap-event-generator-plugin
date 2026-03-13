package usace.hec.actions;

import java.util.List;
import java.util.Optional;

import usace.cc.plugin.api.DataSource;
import usace.hec.model.bootstrap.WeightedBootstrapSampler;
import usace.hec.model.hs.hsReader;
import usace.hec.model.hs.hsReader.HydrologicSampling;

public class ComputeWeightedBootstrapAction extends usace.cc.plugin.api.action_runner.ActionRunnerBase implements usace.cc.plugin.api.action_runner.ActionRunner  {

    @Override
    public String GetName() {
        return super.getName();
    }

    @Override
    public void Run() throws ActionRunnerException {
        Optional<Boolean> useHsFileOp = super.getAction().getAttributes().get("use-hs-file");
        boolean useHsFile = false;
        Double[] weights;
        String[] names;
        if (useHsFileOp.isPresent()){
            useHsFile = useHsFileOp.get();
        }
        if (useHsFile){
            Optional<String> hsFileOp = super.getAction().getAttributes().get("hs-file-datasource");
            if(hsFileOp.isPresent()){
                String hsFileDataSourceName = hsFileOp.get();
                DataSource ds = super.getAction().getInputDataSource(hsFileDataSourceName).get();
                String path = ds.getPath("hs-file");
                HydrologicSampling hs = hsReader.parse(path);
                weights = hs.getSeasonHydroEvents().get(0).getHydroEvents().getIncrProbOverride().getWeights();
                names = hs.getSeasonHydroEvents().get(0).getHydroEvents().getIncrProbOverride().getNames();
            }else{
                throw new IllegalArgumentException("hs-file-datsourc not found");
            }

        }else{
            System.out.println(super.getAction().getAttributes().get("event-names").get());
            System.out.println(super.getAction().getAttributes().get("event-weights").get());
            
            List<String> eventNames = (List<String>)super.getAction().getAttributes().get("event-names").get();
            List<Double> eventWeights = (List<Double>)super.getAction().getAttributes().get("event-weights").get();
            weights = eventWeights.toArray(Double[]::new);
            names = eventNames.toArray(String[]::new);
        }
        System.out.println(super.getAction().getInputDataSource("model").get().getPaths().get("hs_file"));
        System.out.println(super.getAction().getAttributes().get("seed").get());
        System.out.println(super.getAction().getAttributes().get("event-count").get());

        
        WeightedBootstrapSampler bs = new WeightedBootstrapSampler(names,weights);
        long seed =((Integer)super.getAction().getAttributes().get("seed").get()).longValue();
        String[] events = bs.sample((int)super.getAction().getAttributes().get("event-count").get(), seed);
        for (String event : events) {
            System.out.println(event);
        }
    }

}
