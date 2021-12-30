package fr.ostix.worldCreator.core.resourcesProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GLRequestQueue {

    private List<GLRequest> requestQueue = new ArrayList<>();

    public synchronized void addRequest(GLRequest request) {
        this.requestQueue.add(request);
    }

    public synchronized void addAllRequest(GLRequest... request) {
        this.requestQueue.addAll(Arrays.asList(request));
    }



    public synchronized GLRequest acceptNextRequest() {
        return this.requestQueue.remove(0);
    }

    public synchronized boolean hasRequests() {
        return !this.requestQueue.isEmpty();
    }
}
