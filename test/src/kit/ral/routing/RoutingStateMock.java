package kit.ral.routing;

import kit.ral.controller.State;


public class RoutingStateMock extends State {
    public RoutingStateMock() {
        super();
        this.setLoadedGraph(new AdjacentFieldsRoutingGraphSimple());
    }
}
