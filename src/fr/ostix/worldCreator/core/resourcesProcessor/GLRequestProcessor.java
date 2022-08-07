package fr.ostix.worldCreator.core.resourcesProcessor;

import java.util.*;
import java.util.concurrent.*;

public class GLRequestProcessor {
    private static final int MAX_REQUEST_TIME = 12;
    private static GLRequestQueue requestQueue = new GLRequestQueue();
    private static ConcurrentLinkedQueue<GLRequest> requestQueue2 = new ConcurrentLinkedQueue<>();
    private static boolean isRunning = false;
    private static boolean forceRequest = false;

    public synchronized static void sendRequest(GLRequest... request) {
        requestQueue.addAllRequest(request);
        requestQueue2.addAll(Arrays.asList(request));
    }

    public synchronized static void executeRequest() {
        float remainingTime = MAX_REQUEST_TIME * 1_000_000;
        long start = System.nanoTime();
        isRunning = true;
        while (!requestQueue2.isEmpty()) {
            requestQueue2.poll().execute();
//            requestQueue.acceptNextRequest().execute();
            long end = System.nanoTime();
            long timeTaken = end - start;
            remainingTime -= (float) timeTaken;
            start = end;
            if (!forceRequest && remainingTime < 0.0F) {
                break;
            }
        }
        isRunning = false;
    }

    public synchronized static void forceRequest() {
        forceRequest = true;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
