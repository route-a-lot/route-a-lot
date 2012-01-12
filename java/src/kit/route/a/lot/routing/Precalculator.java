package kit.route.a.lot.routing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;

import kit.route.a.lot.controller.State;


public class Precalculator {

    /**
     * Operation precalculate
     * 
     * @return
     * 
     * @return
     */
    
    private RoutingGraph graph, inverted;
    
    public void precalculate() {
        graph = State.getRoutingGraph();
        inverted = graph.getInverted();
        doAreas();
        for (int i = 0; i < graph.getIDCount(); i++) {
            // I suppose we could run some parallel.
            createFlags(i);
        }
        inverted = null; // Let the garbage-collector take care of it
        return;
    }

    private void createFlags(int node) {
        // On further comments, see Router.fromAToB()
        boolean[] seen = new boolean[graph.getIDCount()];
        Route currentPath = null;
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator());
        Arrays.fill(seen, false);
        heap.add(new Route(node, 0));
        byte area = graph.getAreaID(node);
        int currentNode;
        
        while (heap.peek() != null) {
            currentPath = heap.poll();
            currentNode = currentPath.getNode();
            if (seen[currentNode]) {
                // We already know a (shorter) path from that node, so ignore it.
                continue;
            }
            seen[currentNode] = true;
            // At this point, we have the shortest way for sure.
            graph.setArcFlag(currentPath.getRoute().getNode(), currentNode, area);
            for (Integer to: inverted.getAllNeighbors(currentNode)) {
                heap.add(new Route(to, graph.getWeight(currentNode, to), currentPath));
            }
        }
        // If there exist nodes not yet visited at this point, they can't reach the node at all.
    }

    private void doAreas() {
        int AREAS = 63;
        String FILE = "graph.txt";
        // Now this is dirty
        
        // Write graph file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("graph.txt"));
            out.write(graph.getMetisRepresentation());
            out.close();
        } catch (IOException e) {
            System.out.println("Exception ");
            return;
        }
        
        //calculate areas with Metis
        String buffer = "";
        try {
            Process process = Runtime.getRuntime().exec("kmetis "+ FILE + " " + AREAS);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            while ((buffer = stdInput.readLine()) != null) {
                // nop
            }
            
            // read any errors from the attempted command
            while ((buffer = stdError.readLine()) != null) {
                // nop
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // read resulting file
        String filePath = FILE + ".part." + AREAS + "Parts";
        byte[] areas = new byte[(int) new File(filePath).length()];
        BufferedInputStream file = null;
        try {
            file = new BufferedInputStream(new FileInputStream(filePath));
            file.read(areas);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (file != null) {
                try {
                    file.close(); 
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        
        graph.readAreas(new String(areas));
    }
}
