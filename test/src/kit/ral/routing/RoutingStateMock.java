package kit.route.a.lot.routing;

import kit.route.a.lot.controller.State;


public class RoutingStateMock extends State {
    public RoutingStateMock() {
        super();
        this.setLoadedGraph(new AdjacentFieldsRoutingGraphSimple());
    }
}
