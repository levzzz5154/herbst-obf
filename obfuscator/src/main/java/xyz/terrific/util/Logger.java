package xyz.terrific.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author TerrificTable
 * @since 2/4/23
 *
 * TODO: make log overloads get the caller automatically instead of using `Anonymous.class`
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class Logger {
    private static Logger instance;

    private PrintWriter logWriter;
    private boolean stdout;
    private boolean anonymousCaller;


    /**
     * Create logfile and initialize logWriter
     */
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

    /**
     * Singleton
     * @return returns this class instance (singleton)
     */
    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }


    /**
     * initialize Logger and check if logger failed to initialize
     * @return state of success (true if check was successful, otherwise false)
     */
    public boolean initialize(boolean stdout, boolean anonymousCaller) {
        this.stdout = stdout;
        this.anonymousCaller = anonymousCaller;
        return logWriter != null;
    }


    public void raw(Object... message) {
        Object[] args = new Object[0];
        if (message.length > 1) {
            args = Arrays.copyOfRange(message, 1, message.length);
        }

        String str = getDate() + " " + String.format((String) message[0], args);
        logWriter.println(str);
        logWriter.flush();
        if (stdout) System.out.println(str);
    }

    /**
     * info
     *
     * @param caller String name of class calling function
     * @param message message to log
     */
    public void info(String caller, Object... message) {
        Object[] args = new Object[0];
        if (message.length > 1) {
            args = Arrays.copyOfRange(message, 1, message.length);
        }

        String str = getDate() + " (" + caller + ")  Info: " + String.format((String) message[0], args);
        logWriter.println(str);
        logWriter.flush();
        if (stdout) System.out.println(str);
    }

    /**
     * info
     *
     * @param caller class calling this function
     * @param message message to log
     */
    public void info(Class<?> caller, Object... message) {
        info(caller.getName(), message);
    }
    /**
     * info
     *
     * @param message message to log
     */
    public void info(Object... message) {
        if (anonymousCaller) {
            info(Anonymous.class, message);
        } else {
            info(getCaller(0).getClassName(), message);
        }
    }

    /**
     * warning
     *
     * @param caller String name of caller class calling function
     * @param message message to log
     */
    public void warning(String caller, Object... message) {
        Object[] args = new Object[0];
        if (message.length > 1) {
            args = Arrays.copyOfRange(message, 1, message.length);
        }

        String str = getDate() + " (" + caller + ")  Warning: " + String.format((String)message[0], args);
        logWriter.println(str);
        logWriter.flush();
        if (stdout) System.out.println(str);
    }

    /**
     * warning
     *
     * @param caller class calling this function
     * @param message message to log
     */
    public void warning(Class<?> caller, Object... message) {
        warning(caller.getName(), message);
    }

    /**
     * warning
     *
     * @param message message to log
     */
    public void warning(Object... message) {
        if (anonymousCaller) {
            warning(Anonymous.class, message);
        } else {
            warning(getCaller(0).getClassName(), message);
        }
    }

    /**
     * error
     *
     * @param caller String name of caller class calling function
     * @param message message to log
     */
    public void error(String caller, Object... message) {
        Object[] args = new Object[0];
        if (message.length > 1) {
            args = Arrays.copyOfRange(message, 1, message.length);
        }

        String str = getDate() + " (" + caller + ")  Error: " + String.format((String) message[0], args);
        logWriter.println(str);
        logWriter.flush();
        if (stdout) System.err.println(str);
    }

    /**
     * error
     *
     * @param caller class calling this function
     * @param message message to log
     */
    public void error(Class<?> caller, Object... message) {
        error(caller.getName(), message);
    }

    /**
     * error
     *
     * @param message message to log
     */
    public void error(Object... message) {
        if (anonymousCaller) {
            error(Anonymous.class, message);
        } else {
            error(getCaller(0).getClassName(), message);
        }
    }


    /**
     * Close PrintWriter (called on shutdown)
     */
    public void close() {
        logWriter.close();
    }


    private static StackTraceElement[] getCallers() {
        return Thread.currentThread().getStackTrace();
    }
    private static StackTraceElement getCaller(int index) {
        return getCallers()[index + 4];
    }


    /**
     * Get date
     *
     * @return current time like: "[hh:mm:ss]"
     */
    private String getDate() {
        return "[" + new SimpleDateFormat("hh:mm:ss").format(new Date(System.currentTimeMillis())) + "]";
    }

    /**
     * (probably temporary) class
     */
    private static class Anonymous {}
}
