package kit.route.a.lot.routing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import kit.route.a.lot.controller.State;

import static kit.route.a.lot.common.Util.formatSeconds;


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
    
    private static int finishedIds = 0;
    private static double startTime;
    private static double startPeriod;
    private static double currentTime;
    
    public static void precalculate() {
        graph = State.getInstance().getLoadedGraph();
        inverted = graph.getInverted();
        int procNum = Runtime.getRuntime().availableProcessors();
        logger.info("Starting precalculation with " + procNum + " threads...");
        ExecutorService executorService = Executors.newFixedThreadPool(procNum);
        Collection<Future<?>> futures = new ArrayList<Future<?>>(graph.getIDCount());
        if (doAreas()) {
            logger.info("Starting calculation of ArcFlags");
            startTime = System.currentTimeMillis();
            startPeriod = startTime;
            for (int i = 0; i < graph.getIDCount(); i++) {
                final int currentI = i;
                futures.add(executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        createFlags(currentI);
                    }
                }));
                
            }
            logger.info("All tasks added waiting for them to complete...");
            executorService.shutdown();
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Succesfully created ArcFlags in " + formatSeconds((System.currentTimeMillis() - startTime) / 1000));
        } else {
            logger.error("Failed to do precalculation");
        }
        return;
    }
    
    private static synchronized void incrementFinishedIds() {
        finishedIds++;
        if (finishedIds % 10 == 0) {
            currentTime = System.currentTimeMillis();
            if (currentTime - startPeriod > 5000) {
                startPeriod = currentTime;
                logger.info("Calculation of ArcFlags at " + (finishedIds * 100 / graph.getIDCount()) + "%");
                double elapsedTime = (currentTime - startTime) / 1000;
                logger.info("Elapsed time: " + formatSeconds(elapsedTime)
                        + " - estimated time remaining: " + formatSeconds(elapsedTime / (((double) finishedIds) / graph.getIDCount()) - elapsedTime));
            }
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
        incrementFinishedIds();
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
