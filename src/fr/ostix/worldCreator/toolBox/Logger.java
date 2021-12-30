package fr.ostix.worldCreator.toolBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Logger {
    private static final ArrayList<String> logLines = new ArrayList<>();

    private static final File logFile = new File("D:/Projet LWJGL/3D/Projet_1/log", "log.txt");

    public static void log(String msg) {
        String log = getTime() + getThread() + msg;
        System.out.println(log);
        logLines.add(log);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(logFile));
            bw.write(log);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void warn(String msg){
        String log = getTime() + "[WARN]" + getThread() + msg;
        System.out.println(log);
        logLines.add(log);
    }

    public static void err(String msg, Exception e) {
        String log = getTime() + "[ERROR]" + getThread() + msg + " " + e.getMessage();
        System.err.println(log);
//        logLines.add(log);
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter("D:/Projet LWJGL/3D/Projet_1/log/log.txt"));
//            bw.write(log);
//            bw.close();
//            throw e;
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
    }

    public static void err(String msg) {
        String log = getTime() + "[ERROR]" + getThread() + msg;
        System.err.println(log);
        logLines.add(log);
 //       BufferedWriter bw = null;
//        try {
//            bw = new BufferedWriter(new FileWriter("D:/Projet LWJGL/3D/Projet_1/log/log.txt"));
//            bw.write(log);
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public synchronized static String getThread() {
        String threadName = Thread.currentThread().getName();
        return "[" + threadName + "]";
    }

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(cal.getTime()) + "]";
    }

    public static String getLines() {
        StringBuilder s = new StringBuilder();
        for (String logLine : logLines) {
            s.append("\n").append(logLine);
        }
        return s.toString();
    }
}
