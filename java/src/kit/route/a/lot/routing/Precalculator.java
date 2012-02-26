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

import kit.route.a.lot.common.Progress;
import kit.route.a.lot.controller.State;

import static kit.route.a.lot.common.Util.formatSeconds;


public class Precalculator {

    /**
     * Operation precalculate
     * 
     * @return
     */
    
    private static RoutingGraph graph, inverted;
    private static Logger logger = Logger.getLogger(Precalculator.class);
    
    private static int finishedIds = 0;
    private static long startTime, startPeriod, currentTime;
    
    public static void precalculate(final Progress p) {
        graph = State.getInstance().getLoadedGraph();
        inverted = graph.getInverted();
        logger.info("Starting precalculation...");
        int procNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(procNum);
        Collection<Future<?>> futures = new ArrayList<Future<?>>(graph.getIDCount());
        if (doAreas(p.createSubProgress(0.01))) {
            logger.info("Starting calculation of ArcFlags  with " + procNum + " threads...");
            startTime = System.currentTimeMillis();
            startPeriod = startTime;
            for (int i = 0; i < graph.getIDCount(); i++) {
                final int currentI = i;
                futures.add(executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        createFlags(currentI, p.createSubProgress(0.99f / graph.getIDCount()));
                    }
                }));       
            }
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
            graph.setAllArcFlags();
            logger.error("Failed to do precalculation");
        }
        p.finish();
        return;
    }
    
    private static synchronized void incrementFinishedIds() {
        finishedIds++;
        if (finishedIds % 10 == 0) {
            currentTime = System.currentTimeMillis();
            if (currentTime - startPeriod > 5000) {
                startPeriod = currentTime;
                logger.info("Calculation of ArcFlags at " + (finishedIds * 100 / graph.getIDCount()) + "%");
                long elapsedTime = (currentTime - startTime) / 1000;
                logger.info("Elapsed time: " + formatSeconds(elapsedTime) + " - estimated time remaining: "
                        + formatSeconds((long)(elapsedTime  * ((graph.getIDCount() / (double) finishedIds) - 1))));
            }
        }
    }
    
    private static void createFlags(int node, Progress progress) {
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
        progress.finish();
        incrementFinishedIds();
        // If there exist nodes not yet visited at this point, they can't reach the node at all.
        logger.trace("Done calculating ArcFlags for ID " + String.valueOf(node));
    }

    private static boolean doAreas(Progress p) {
        final int AREAS = 63;
        File file;
        try {
            file = File.createTempFile("graph", ".txt");
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        file.deleteOnExit();
        //final String FILE = "graph.txt";
        String BINARY = "gpmetis";
        logger.info("Creating areas with Metis...");
        // Write graph file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(graph.getMetisRepresentation());
            out.close();
        } catch (IOException e) {
            logger.error("Couldn't create graph-file, got rights?");
            return false;
        }
        p.addProgress(0.2);
        
        //calculate areas with Metis
        boolean tryAgain;
        do {
            tryAgain = false;
            try {
                String buffer;
                Process process = Runtime.getRuntime().exec(BINARY + " " + file.getAbsolutePath() + " " + AREAS);
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
        p.addProgress(0.7);
        
        // read resulting file
        String filePath = file.getAbsolutePath() + ".part." + AREAS;
        byte[] areas = new byte[(int) new File(filePath).length()];
        BufferedInputStream buff;
        try {
            buff = new BufferedInputStream(new FileInputStream(filePath));
            buff.read(areas);
            buff.close();
        } catch (IOException e) {
            logger.error("Couldn't read area file, got rights?");
            return false;
        }
        File metisFile = new File(filePath);
        metisFile.delete();
        graph.readAreas(new String(areas));
        logger.info("Areas successfully created");
        p.addProgress(0.1);
        return true;  
    }
}
