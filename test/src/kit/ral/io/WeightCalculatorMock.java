package kit.ral.io;

import kit.ral.common.Selection;
import kit.ral.common.WeightCalculator;


public class WeightCalculatorMock extends WeightCalculator {

    @Override
    public int calcWeight(int fromID, int toID) {
        return 1;
    }
    
    public int calcWeight(Selection edge) {
        return calcWeight(edge.getFrom(), edge.getTo());
    }
    
    @Override
    public int calcWeightWithHeight(int fromID, int toID) {
        return 1;
    }
}
