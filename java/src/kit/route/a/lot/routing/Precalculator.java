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
    
    private static RoutingGraph graph, inverted;
    private static Logger logger = Logger.getLogger(Precalculator.class);
    
    public static void precalculate() {
        logger.info("Starting precalculation...");
        graph = State.getInstance().getLoadedGraph();
        inverted = graph.getInverted();
        if (doAreas()) {
            logger.info("Starting calculation of ArcFlags");
            double start = System.currentTimeMillis();
            double startPeriod = start;
            double currentTime;
            for (int i = 0; i < graph.getIDCount(); i++) {
                if (i % 10 == 0) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startPeriod > 5000) {
                        startPeriod = currentTime;
                        logger.info("Calculation of ArcFlags at " + (i * 100 / graph.getIDCount()) + "%");
                        double usedTime = (currentTime - start) / 1000;
                        logger.info("Time used: " + formatSeconds(usedTime)
                                + " - estimated time remaining: " + formatSeconds(usedTime / (((double) i) / graph.getIDCount()) - usedTime));
                    }
                }
                // TODO I suppose we could run some parallel.
                createFlags(i);
            }
            logger.info("Succesfully created ArcFlags");
        } else {
            logger.error("Failed to do precalculation");
        }
        return;
    }
    
    private static String formatSeconds(double dSeconds) {
        int iSeconds = (int) dSeconds;
        int seconds = iSeconds % 60;
        int minutes = iSeconds / 60 % 60;
        int hours = iSeconds / 3600 % 24;
        int days = iSeconds / 86400;
        String sSeconds = seconds + "s";
        String sMinutes = minutes + "min " + sSeconds;
        String sHours = hours + "h " + sMinutes;
        String sDays = days + "d " + sHours;
        if (days == 0) {
            if (hours == 0) {
                if (minutes == 0) {
                    return sSeconds;
                } else {
                    return sMinutes;
                }
            } else {
                return sHours;
            }
        } else {
            return sDays;
        }
    }

    private static void createFlags(int node) {
        logger.trace("Calculating ArcFlags for ID " + String.valueOf(node));
        // On further comments, see Router.fromAToB()
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false);
        Route currentPath = null;
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
        heap.add(new Route(node, 0));
        byte area = graph.getAreaID(node);
        int currentNode;
        int weight;
        while (heap.peek() != null) {
            currentPath = heap.poll();
            currentNode = currentPath.getNode();
            if (seen[currentNode]) {
                // We already know a (shorter) path from that node, so ignore it.
                continue;
            }
            seen[currentNode] = true;
            // At this point, we have the shortest way for sure.
            graph.setArcFlag(currentNode, currentPath.getRoute().getNode(), area);
            for (Integer from: inverted.getAllNeighbors(currentNode)) {
                weight = graph.getWeight(from, currentNode);
                if (weight > 0) {
                    heap.add(new Route(from, weight, currentPath));
                } else {
                    logger.fatal("Got negative weights, please fix");
                }
            }
        }
        // If there exist nodes not yet visited at this point, they can't reach the node at all.
        logger.trace("Done calculating ArcFlags for ID " + String.valueOf(node));
    }

    private static boolean doAreas() {
        final int AREAS = 63;
        final String FILE = "graph.txt";
        String BINARY = "gpmetis";
        logger.info("Creating areas with Metis...");
        // Write graph file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(FILE));
            out.write(graph.getMetisRepresentation());
            out.close();
        } catch (IOException e) {
            logger.error("Couldn't create graph-file, got rights?");
            return false;
        }
        
        //calculate areas with Metis
        boolean tryAgain;
        do {
            tryAgain = false;
            try {
                String buffer;
                Process process = Runtime.getRuntime().exec(BINARY + " " + FILE + " " + AREAS);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));               
                // read the output from the command
                while ((buffer = stdInput.readLine()) != null) {
                    logger.debug("Metis: " + buffer);
                }              
                // read any errors from the attempted command
                while ((buffer = stdError.readLine()) != null) {
                    logger.error("Metis: " + buffer);
                }
            } catch (IOException e) {
                tryAgain = (BINARY.equals("gpmetis"));
                if (!tryAgain) {
                    logger.error(BINARY + " failed to execute");
                    return false;  
                }
                BINARY = "./gpmetis";   
            }
        } while (tryAgain);
        
        // read resulting file
        String filePath = FILE + ".part." + AREAS;
        byte[] areas = new byte[(int) new File(filePath).length()];
        BufferedInputStream file;
        try {
            file = new BufferedInputStream(new FileInputStream(filePath));
            file.read(areas);
            file.close();
        } catch (IOException e) {
            logger.error("Couldn't read area file, got rights?");
            return false;
        }
        graph.readAreas(new String(areas));
        logger.info("Areas successfully created");
        return true;
    }
}
