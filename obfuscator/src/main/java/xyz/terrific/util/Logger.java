package xyz.terrific.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author TerrificTable
 * @since 2/4/23
 *
 * TODO: make log overloads get the caller automatically instead of using `Anonymous.class`
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Logger {
    private static Logger instance;

    private PrintWriter logWriter;


    private Logger() {
        String date = new SimpleDateFormat("dd-MM-yyyy hh-mm").format(new Date(System.currentTimeMillis()));
        File logFolder = new File("logs/");
        if (!logFolder.exists() && !logFolder.mkdirs()) {
            System.err.println("Failed to create logs folder");
            return;
        }

        File logFile = new File("logs/" + date + ".log");
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Failed to create log file");
            return;
        }

        try {
            logWriter = new PrintWriter(logFile);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to open PrintWriter to log file");
        }
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }


    public void info(Class<?> caller, String message) {
        logWriter.println(getDate() + " (" + caller.getSimpleName() + ")  INFO: " + message);
        logWriter.flush();
    }
    public void info(String message) {
        info(Anonymous.class, message);
    }

    public void warning(Class<?> caller, String message) {
        logWriter.println(getDate() + " (" + caller.getSimpleName() + ") WARNING: " + message);
        logWriter.flush();
    }
    public void warning(String message) {
        warning(Anonymous.class, message);
    }

    public void error(Class<?> caller, String message) {
        logWriter.println(getDate() + " (" + caller.getSimpleName() + ") Errror: " + message);
        logWriter.flush();
    }
    public void error(String message) {
        warning(Anonymous.class, message);
    }



    public void close() {
        logWriter.close();
    }


    private String getDate() {
        return "[" + new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis())) + "]";
    }

    private static class Anonymous {}
}
