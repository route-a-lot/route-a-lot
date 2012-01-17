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

import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(Precalculator.class);
    
    public void precalculate() {
        logger.info("Starting precalculation...");
        graph = State.getInstance().getLoadedGraph();
        inverted = graph.getInverted();
        if (doAreas()) {
            for (int i = 0; i < graph.getIDCount(); i++) {
                // I suppose we could run some parallel.
                createFlags(i);
                logger.info("Succesfully created ArcFlags");
            }
        } else {
            logger.error("Failed to do precalculation");
        }
        inverted = null; // Let the garbage-collector take care of it
        return;
    }

    private void createFlags(int node) {
        logger.info("Calculating ArcFlags for ID " + String.valueOf(node));
        // On further comments, see Router.fromAToB()
        boolean[] seen = new boolean[graph.getIDCount()];
        Route currentPath = null;
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
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
        logger.info("Done calculating ArcFlags for ID " + String.valueOf(node));
    }

    private boolean doAreas() {
        String AREAS = "63";
        String FILE = "graph.txt";
        // Now this is dirty
        logger.info("Creating " + AREAS + " Areas with Metis...");
        // Write graph file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(FILE));
            out.write(graph.getMetisRepresentation());
            out.close();
        } catch (IOException e) {
            logger.error("Couldn't create graph-file, got r/w rights?");
            return false;
        }
        
        //calculate areas with Metis
        String buffer = "";
        try {
            Process process = Runtime.getRuntime().exec("gpmetis "+ FILE + " " + AREAS);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // read the output from the command
            while ((buffer = stdInput.readLine()) != null) {
                logger.debug(buffer);
            }
            
            // read any errors from the attempted command
            while ((buffer = stdError.readLine()) != null) {
                logger.error(buffer);
            }
        } catch (IOException e) {
            logger.error("kmetis couldn't be executed.");
            return false;
        }
        
        // read resulting file
        String filePath = FILE + ".part." + AREAS + "Parts";
        byte[] areas = new byte[(int) new File(filePath).length()];
        BufferedInputStream file = null;
        try {
            file = new BufferedInputStream(new FileInputStream(filePath));
            file.read(areas);
        } catch (IOException e) {
            logger.error("Couldn't read area file, got r/w rights?");
            return false;
        } finally {
            if (file != null) {
                try {
                    file.close(); 
                } catch (IOException e) {
                    logger.error("Couldn't close file, prepare for mem-leak");
                    return false;
                }
            }
        }
        graph.readAreas(new String(areas));
        logger.info("Areas succesfully created");
        return true;
    }
}
