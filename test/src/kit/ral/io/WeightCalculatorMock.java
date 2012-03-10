package kit.ral.io;

import kit.ral.common.Selection;
import kit.ral.common.WeightCalculator;
import kit.ral.controller.State;


public class WeightCalculatorMock extends WeightCalculator {

    
    
    public WeightCalculatorMock(State state) {
        super(state); 
    }

    
    
    public int calcWeight(Selection edge) {
        return calcWeight(edge.getFrom(), edge.getTo());
    }
    
    @Override
    protected int calcWeightWithHeight(int fromID, int toID) {
        return 1;
    }
    
    @Override
    protected int calcWeight(int fromID, int toID) {
        return 1;
    }
}
