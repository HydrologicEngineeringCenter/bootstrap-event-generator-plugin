package usace.hec.actions;

public class ComputeAction extends usace.cc.plugin.api.action_runner.ActionRunnerBase implements usace.cc.plugin.api.action_runner.ActionRunner  {

    @Override
    public String GetName() {
        return super.getName();
    }

    @Override
    public void Run() throws ActionRunnerException {
        System.out.println(super.getPm().getPayload().getInputDataSource("model").get().getPaths().get("file"));
        System.out.println(super.getPm().getPayload().getOutputDataSource("log").get().getPaths().get("log"));
        System.out.println(super.getAction().getAttributes().get("attribute_1"));
        System.out.println(super.getAction().getAttributes().get("attribute_2"));
    }

}
