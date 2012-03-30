package kit.ral.routing;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.PairingHeap;
import kit.ral.common.Progress;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.StringUtil;
import kit.ral.common.util.Util;
import kit.ral.controller.State;
import kit.ral.map.Node;
import kit.ral.map.info.MapInfo;
import kit.ral.map.rendering.Renderer;

import org.apache.log4j.Logger;


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

    private static final int AREAS = 64;
    private static String GRAPH_FILE = "sral/graph";
    
    public static void precalculate(final Progress p) {
        finishedIds = 0;
        graph = State.getInstance().getLoadedGraph();
        inverted = graph.getInverted();
        logger.info("Starting precalculation... " + new Date());
        int procNum = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(procNum);
        Collection<Future<?>> futures = new ArrayList<Future<?>>(graph.getIDCount());
        if (doAreas(p.createSubProgress(0.01))) {
            logger.info("Starting calculation of ArcFlags  with " + procNum + " threads...");
            Util.startTimer();
            startPeriod = System.currentTimeMillis();
            startTime = startPeriod;
            for (int i = 0; i < graph.getIDCount(); i++) {
                final int currentI = i;
                futures.add(executorService.submit(new Runnable() {
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
            logger.info("Successfully created ArcFlags in " + Util.stopTimer());
        } else {
            graph.setAllArcFlags();
            logger.error("Failed to do precalculation");
        }
//        saveGraph();
        p.finish();
        logger.info("Precalculation finished. " + new Date());
        return;
    }
    
    @SuppressWarnings("unused")
    private static void saveGraph() {
        File file = new File(GRAPH_FILE);
        try {
            DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            graph.saveToOutput(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static synchronized void incrementFinishedIds() {
        finishedIds++;
        if (finishedIds % 10 == 0) {
            currentTime = System.currentTimeMillis();
            if (currentTime - startPeriod > 5000) {
                startPeriod = currentTime;
                logger.debug("Calculation of ArcFlags at " + (finishedIds * 100 / graph.getIDCount()) + "%");
                long elapsedTime = (currentTime - startTime) / 1000;
                logger.debug("Elapsed time: " + StringUtil.formatSeconds(elapsedTime, true)
                        + " - estimated time remaining: " + StringUtil.formatSeconds(
                        (long)(elapsedTime  * ((graph.getIDCount() / (double) finishedIds) - 1)), true));
            }
        }
    }
    
    private static void createFlags(int node, Progress progress) {
        logger.trace("Calculating ArcFlags for ID " + String.valueOf(node));
        byte area = graph.getAreaID(node);
        Collection<Integer> neighbors = inverted.getAllNeighbors(node);
        boolean hasNeighborInOtherArea = false;
        for (Integer neighbor : neighbors) {
            if (graph.getAreaID(neighbor) == area) {
                graph.setArcFlag(neighbor, node, area);
            } else {
                hasNeighborInOtherArea = true;
            }
        }
        if (!hasNeighborInOtherArea) {
            progress.finish();
            incrementFinishedIds();
            return;
        }
        // On further comments, see Router.fromAToB()
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false);
        Route currentPath = null;
        PairingHeap<Route> heap = new PairingHeap<Route>(new RouteComparator<Route>());
        heap.add(new Route(node, 0));
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
            if (currentNode != currentPath.getRoute().getNode()) {  // only false if we're in the first round
                graph.setArcFlag(currentNode, currentPath.getRoute().getNode(), area);
            }
            for (Integer from: inverted.getAllNeighbors(currentNode)) {
                if (area == graph.getAreaID(currentNode) && area == graph.getAreaID(from)) {
                    continue;
                }
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
                    logger.info("Metis: " + buffer);
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
                BINARY = "./lib/gpmetis";   
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
        logger.info("Areas successfully created " + new Date());
        p.addProgress(0.1);
        return true;  
    }
    
    /**
     * Draws the calculated areas on the given graphics.
     * 
     */
    public static void drawAreas(Bounds bounds, int detailLevel, Graphics graphics) {
        graph = State.getInstance().getLoadedGraph();
        int idCount = graph.getIDCount();
        Color[] colors = new Color[AREAS];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = getAreaColor(i);
        }
        MapInfo mapInfo = State.getInstance().getMapInfo();
        int size = 10;
        Bounds extendedBounds = bounds.clone().extend(size);
        size /= Projection.getZoomFactor(detailLevel);
        for (int i = 0; i < idCount; i++) {
            Node node = mapInfo.getNode(i);
            if (!node.isInBounds(extendedBounds)) {
                continue;
            }
            Coordinates localCoordinates = Renderer.getLocalCoordinates(node.getPos(),
                    bounds.getTop(), bounds.getLeft(), detailLevel);
            graphics.setColor(colors[graph.getAreaID(i)]);
            graphics.fillOval((int) localCoordinates.getLongitude() - size/2, (int) localCoordinates.getLatitude() - size/2, size, size);
        }
        ((AdjacentFieldsRoutingGraph) graph).drawArcFlags(bounds, detailLevel, graphics, 1);
//        graphics.setColor(Color.green);
//        size = 10;
//        draw(5248, bounds, detailLevel, graphics, colors, mapInfo, size, extendedBounds);
//        draw(2709, bounds, detailLevel, graphics, colors, mapInfo, size, extendedBounds);
//        graphics.setColor(Color.red);
//        draw(5349, bounds, detailLevel, graphics, colors, mapInfo, size, extendedBounds);
//        draw(5350, bounds, detailLevel, graphics, colors, mapInfo, size, extendedBounds);
    }
    
//    private static void draw(int i, Bounds bounds, int detailLevel, Graphics graphics, Color[] colors,
//            MapInfo mapInfo, int size, Bounds extendedBounds) {
//        Node node = mapInfo.getNode(i);
//        if (!node.isInBounds(extendedBounds)) {
//            return;
//        }
//        Coordinates localCoordinates = Renderer.getLocalCoordinates(node.getPos(),
//                bounds.getTop(), bounds.getLeft(), detailLevel);
//        graphics.fillOval((int) localCoordinates.getLongitude() - size/2, (int) localCoordinates.getLatitude() - size/2, size, size);
//    }
    
    public static Color getAreaColor(int i) {
        return new Color((i * 389) % 256, (i * 211) % 256, (i * 109) % 256);
    }
}
