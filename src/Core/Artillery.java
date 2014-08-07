package Core;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utils.ObjectFilter;
import Utils.WarFormatter;

public class Artillery extends Thread implements Detector {
    private static int idGenerator = 100;
    private String id;
    private boolean isRunning;
    private Logger logger;
    private int launchersIntercepted;

    public Artillery(String warName, String id) {
	this.id = id;
	this.logger = Logger.getLogger(warName);
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

    public Artillery(String warName) {
	this.id = "A-" + (idGenerator++);
	this.logger = Logger.getLogger(warName);
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

    @Override
    public void onDetect(Object o) {
	Launcher l = (Launcher) o;
	try {
	    Thread.sleep((long) (200 + (Math.random() * 15 * 1000)));
	    if (!l.isAlive() || l.getLState() == Launcher.State.HIDDEN)
		logFailedInterception(l, l.getLState());
	    else {
		if (l.getLState() == Launcher.State.ACTIVE) {
		    l.destruct();
		    logInterception(l);
		}
	    }
	} catch (InterruptedException e) {
	}
    }

    private void logInterception(Launcher l) {
	logger.log(Level.INFO, this + " has intercepted " + l, this);
	synchronized (War.COUNT_LOCK) {
	    launchersIntercepted++;
	}
    }

    private void logFailedInterception(Launcher l, Launcher.State state) {
	if (state == Launcher.State.ACTIVE)
	    logger.log(Level.WARNING, this + " has failed to intercept " + l,
		    this);
	else
	    logger.log(Level.WARNING, this + " has failed to intercept " + l
		    + " because its " + state.toString(), this);
    }

    public void Stop() {
	isRunning = false;
    }

    @Override
    public void run() {
	isRunning = true;
	while (isRunning) {
	    Set<Thread> threads = Thread.getAllStackTraces().keySet();
	    Iterator<Thread> it = threads.iterator();
	    while (it.hasNext()) {
		Thread t = it.next();
		if (t instanceof Launcher && t.isAlive()) {
		    onDetect(t);
		}
		try {
		    sleep(3000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    @Override
    public String toString() {
	return id;
    }
}
