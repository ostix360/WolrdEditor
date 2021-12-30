package fr.ostix.worldCreator.core;

import fr.ostix.worldCreator.core.resourcesProcessor.GLRequest;
import fr.ostix.worldCreator.frame.ErrorPopUp;

public class Timer {

    public static final int timeOut = 2500;

    public static void waitForRequest(GLRequest request){
        long millis = System.currentTimeMillis();
        while(!request.isExecuted()){
            //System.out.println(request.isExecuted());
            if ((System.currentTimeMillis() - millis) > timeOut){
                new ErrorPopUp("GL request failed " , request.toString());
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
