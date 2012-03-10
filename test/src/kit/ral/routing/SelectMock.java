package kit.ral.routing;

import java.util.Random;

import kit.ral.common.Selection;
import kit.ral.controller.State;


public class SelectMock {
    
    public static Selection getRandomSelection() {
        int numberOfNodes = State.getInstance().getLoadedGraph().getIDCount() - 1;
        
        Random random = new Random();
        int start = 0;
        boolean found = false;
        while(!found) {
            start = random.nextInt(numberOfNodes);
            if (State.getInstance().getLoadedGraph().getAllNeighbors(start).size() != 0) {
                found = true;
            }
        }
        int target = 0;
        int randomTargetNeighbourNumber = random.nextInt(State.getInstance().getLoadedGraph().getAllNeighbors(start).size()); 
        int i = 0; //there is no get-Method for collection, so we have to go with the Iterator through the list
        for (Integer neighbour : State.getInstance().getLoadedGraph().getAllNeighbors(start)) {
            if (i == randomTargetNeighbourNumber) {
                target = neighbour;
                break;
            }
            i++;
        }
        
        return new Selection(null, start, target, random.nextFloat(), "");
    }
        
}
