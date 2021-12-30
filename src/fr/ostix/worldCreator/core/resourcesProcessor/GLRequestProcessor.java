package fr.ostix.worldCreator.core.resourcesProcessor;

public class GLRequestProcessor {
    private static final int MAX_REQUEST_TIME = 12;
    private static GLRequestQueue requestQueue = new GLRequestQueue();
    private static boolean isRunning = false;
    private static boolean forceRequest = false;

    public synchronized static void sendRequest(GLRequest... request) {
        requestQueue.addAllRequest(request);
    }

    public synchronized static void executeRequest() {
        float remainingTime = MAX_REQUEST_TIME * 1_000_000;
        long start = System.nanoTime();
        isRunning = true;
        while (requestQueue.hasRequests()) {
            requestQueue.acceptNextRequest().execute();
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

    public synchronized static boolean isRunning() {
        return isRunning;
    }
}
