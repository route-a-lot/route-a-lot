package kit.route.a.lot.io;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WeightCalculator;


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
