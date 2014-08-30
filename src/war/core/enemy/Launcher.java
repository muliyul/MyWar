package war.core.enemy;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import war.core.Destructable;
import war.core.War;
import war.utils.ObjectFilter;
import war.utils.WarFormatter;

public class Launcher extends Thread implements Destructable {
	public enum State {
		ACTIVE, HIDDEN, DESTROYED
	}

	private String id;
	private List<Missile> missiles;
	private boolean isRunning;
	private Logger logger;
	private State state;
	private int missilesFired;
	private int totalDamage;
	private Semaphore launchpad;

	/**
	 * 
	 * @param warName
	 *            - Name of war (for logging purposes).
	 * @param id
	 *            - The id of the launcher.
	 * @param state
	 *            - The state of the launcher (DESTROYED is possible but has no
	 *            use).
	 */
	public Launcher(String warName, String id, State state) {
		this.missiles = new Vector<>();
		this.state = state;
		this.id = id;
		this.missilesFired = 0;
		this.launchpad = new Semaphore(1, true);
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



	/**
	 * Adds a missile to the launcher. Thread-safe method.
	 * 
	 * @param m
	 *            - Missile to add.
	 */
	public synchronized void addMissile(Missile m) {
		missiles.add(0, m);
		m.setLauncher(this);
		m.setLaunchpad(launchpad);
		this.notifyAll();
	}

	@Override
	public void run() {
		logLStart();
		isRunning = true;
		if (missiles.size() > 0) {
			for (int i = 0; i < missiles.size(); i++) {

				missiles.get(i).start();
			}
		}

		synchronized (this) {
			try {
				while (isRunning && this.state != State.DESTROYED) {
					this.wait();
					missiles.get(0).start();
				}
			} catch (InterruptedException e) { // catch interrupt when the launcher is destroyed to end thread

			}
		}
	}

	private void logLStart() {
		logger.log(Level.INFO, this + " has started", this);		
	}


	/**
	 * Increase the number of missiles fired so far. Thread-safe.
	 */
	protected synchronized void incrementMissilesFired() {
		missilesFired++;
	}

	/**
	 * 
	 * @return List of the missiles.
	 */
	public List<Missile> getMissiles() {
		return missiles;
	}

	/**
	 * Sets the list of the missiles to be used.
	 * 
	 * @param missiles
	 */
	public void setMissiles(List<Missile> missiles) {
		this.missiles = missiles;
		for (Missile m : missiles) {
			m.setLauncher(this);
			m.setLaunchpad(launchpad);
		}
	}

	/**
	 * Terminates the thread.
	 */
	public void Stop() {
		isRunning = false;
		this.interrupt();
	}

	@Override
	public String toString() {
		return this.id;
	}

	/**
	 * 
	 * @return Launcher's state.
	 */
	public State getLState() {
		return state;
	}

	public int getMissilesFired() {
		return missilesFired;
	}

	/**
	 * Adds damage to the total damage dealt by this launcher.
	 * 
	 * @param damage
	 */
	public synchronized void addDamage(int damage) {
		this.totalDamage += damage;
	}

	public int getTotalDamage() {
		return totalDamage;
	}

	/**
	 * Interrupts the launcher,
	 */
	public void destruct() {
		this.state = State.DESTROYED;
		isRunning=false;
		for(Missile m : missiles ){
			logMissileDestructBeforeLaunch(m);
		}
		this.interrupt();

	}

	private void logMissileDestructBeforeLaunch(Missile m) {
		logger.log(Level.SEVERE, m + " was destructed before launch becuse his launcher "+ this + " was destroyed", m);

	}

	/**
	 * Sets the launcher's state to visible/active.
	 */
	protected synchronized void setVisible() {
		if (state != Launcher.State.DESTROYED)
			state = State.ACTIVE;
	}

	/**
	 * Sets the launcher's state to hidden.
	 */
	protected synchronized void setHidden() {
		if (state != Launcher.State.DESTROYED)
			state = State.HIDDEN;
	}

	/**
	 * Removes the missile from the missiles list.
	 * 
	 * @param missile
	 */
	protected synchronized void remove(Missile missile) {
		missiles.remove(missile);
	}

	public Semaphore getLaunchpad() {
		return launchpad;
	}
}
