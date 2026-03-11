package usace.hec.actions;

import java.util.List;

import usace.hec.model.BootstrapSampler;

public class ComputeAction extends usace.cc.plugin.api.action_runner.ActionRunnerBase implements usace.cc.plugin.api.action_runner.ActionRunner  {

    @Override
    public String GetName() {
        return super.getName();
    }

    @Override
    public void Run() throws ActionRunnerException {
        System.out.println(super.getPm().getPayload().getInputDataSource("model").get().getPaths().get("file"));
        System.out.println(super.getPm().getPayload().getOutputDataSource("log").get().getPaths().get("log"));
        System.out.println(super.getAction().getAttributes().get("seed").get());
        System.out.println(super.getAction().getAttributes().get("event-count").get());
        System.out.println(super.getAction().getAttributes().get("event-names").get());
        List<String> eventNames = (List<String>)super.getAction().getAttributes().get("event-names").get();
        BootstrapSampler bs = new BootstrapSampler(eventNames.toArray(String[]::new) );
        long seed =((Integer)super.getAction().getAttributes().get("seed").get()).longValue();
        String[] events = bs.sample((int)super.getAction().getAttributes().get("event-count").get(), seed);
        for (String event : events) {
            System.out.println(event);
        }
    }

}
