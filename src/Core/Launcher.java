package Core;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utils.ObjectFilter;
import Utils.WarFormatter;

public class Launcher extends Thread implements Destructable {
    public enum State {
	ACTIVE, HIDDEN, DESTROYED
    }

    private static int idGenerator = 100;
    private String id;
    private List<Missile> missiles;
    private boolean isRunning;
    private Logger logger;
    private State state;
    private int missilesFired;
    private int totalDamage;

    public Launcher(String warName, String id, State state) {
	this.missiles = new ArrayList<>();
	this.state = state;
	missilesFired = 0;
	try {
	    this.logger = Logger.getLogger(warName + "");
	    FileHandler fh = new FileHandler("logs/" + warName + "/" + id
		    + ".log");
	    fh.setFilter(new ObjectFilter(this));
	    fh.setFormatter(new WarFormatter());
	    logger.addHandler(fh);
	} catch (SecurityException | IOException e) {
	    e.printStackTrace();
	}
    }

    public Launcher(String warName) {
	this.id = "L-" + (idGenerator++);
	this.missiles = new ArrayList<>();
	missilesFired = 0;
	try {
	    this.logger = Logger.getLogger(warName + "");
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

    public synchronized void addMissile(Missile m) {
	missiles.add(m);
	m.setLauncher(this);
    }

    @Override
    public void destruct() {
	interrupt();
    }

    @Override
    public void run() {
	isRunning = true;
	this.state = State.ACTIVE;
	while (isRunning && state == State.ACTIVE) {
	    synchronized (this) {
		if (missiles.size() > 0) {
		    Missile m = missiles.remove(0);
		    m.start();
		    logLaunch(m);
		}
	    }
	}
    }

    private void logLaunch(Missile m) {
	logger.log(Level.WARNING, m + " has been launched from " + this, this);
	synchronized (War.COUNT_LOCK) {
	    missilesFired++;
	}
    }

    public void Stop() {
	isRunning = false;
    }

    @Override
    public String toString() {
	return this.id;
    }

    public State getLState() {
	return state;
    }

    public int getMissilesFired() {
	return missilesFired;
    }

    public void addDamage(int damage) {
	this.totalDamage += damage;
    }

    public int getTotalDamage() {
	return totalDamage;
    }
}
