package war.core;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import war.utils.ObjectFilter;
import war.utils.WarFormatter;

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
	this.missiles = new Vector<>();
	this.state = state;
	this.id = id;
	this.missilesFired = 0;
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

    public Launcher(String warName, State state) {
	this(warName, "L" + (idGenerator++), state);
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
	while (isRunning) {
	    synchronized (this) {
		if (missiles.size() > 0) {
		    missiles.remove(0).start();
		}
	    }
	}
    }

    public synchronized void incrementMissilesFired() {
	missilesFired++;
    }

    public List<Missile> getMissiles() {
	return missiles;
    }

    public void setMissiles(List<Missile> missiles) {
	this.missiles = missiles;
	for (Missile m : missiles) {
	    m.setLauncher(this);
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

    public synchronized void addDamage(int damage) {
	this.totalDamage += damage;
    }

    public int getTotalDamage() {
	return totalDamage;
    }

    public void intercept() {
	Stop();
	state = State.DESTROYED;
    }

    public synchronized void setVisible() {
	state = State.ACTIVE;
    }

    public synchronized void setHidden() {
	state = State.HIDDEN;
    }
}
