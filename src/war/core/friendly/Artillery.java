package war.core.friendly;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import war.core.Target;
import war.utils.ObjectFilter;
import war.utils.WarFormatter;

public class Artillery extends Thread {
    public enum Type {
	SHIP, PLANE
    }

    private static int idGenerator = 100;
    private String id;
    private Type type;
    private boolean isRunning;
    private Logger logger;
    private Vector<Target> targets;
    private int launchersIntercepted;

    /**
     * 
     * @param warName - The war's name (for logging purposes).
     * @param id - The artillery's ID.
     * @param type - The artillery's type.
     */
    public Artillery(String warName, String id, Type type) {
	this.id = id;
	this.logger = Logger.getLogger(warName);
	this.type = type;
	this.targets = new Vector<>();
	this.launchersIntercepted = 0;
	try {
	    FileHandler fh = new FileHandler("logs/" + warName + "/" + id
		    + ".log");
	    fh.setFilter(new ObjectFilter(this));
	    fh.setFormatter(new WarFormatter());
	    logger.addHandler(fh);
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * 
     * @param warName - The war's name (for logging purposes).
     * @param type - The artillery's type.
     */
    public Artillery(String warName, Type type) {
	this(warName, "A" + (idGenerator++), type);
    }

    /**
     * Terminate the thread.
     */
    public void Stop() {
	isRunning = false;
    }

    @Override
    public void run() {
	isRunning = true;
	while (isRunning) {
	    if (targets.size() > 0)
		targets.remove(0).intercept();
	}
    }

    @Override
    public String toString() {
	return id;
    }

    /**
     * Adds a target to the artillery. Thread-safe.
     * @param t - The target.
     */
    public synchronized void addTarget(Target t) {
	targets.add(t);
    }

    public Type getType() {
	return type;
    }

    /**
     * Increments the number of launchers Intercepted by this artillery.
     */
    public synchronized void incrementLaunchersDestroyed() {
	launchersIntercepted++;
    }
    
    public int getLaunchersIntercepted() {
	return launchersIntercepted;
    }
}
