package fr.ostix.worldCreator.creator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Workspace {

    private Logger log = Logger.getLogger("Workspace");
    public void save() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
            log.log(Level.WARNING, "cannot export entity", e);
        }
    }


    public void open(){

    }
}
