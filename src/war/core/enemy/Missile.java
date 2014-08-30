package war.core.enemy;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import war.core.Destructable;
import war.utils.ObjectFilter;
import war.utils.WarFormatter;

public class Missile extends Thread implements Destructable {
	public enum State {
		LAUNCHING, FLYING, HIT, INTERCEPTED
	}

	private static int idGenerator = 100;
	private State state;
	private String id;
	private int damage;
	private String dest;
	private long launchTime;
	private long flyTime;
	private Launcher launcher;
	private Logger logger;
	private Semaphore launchpad;
	private boolean last;

	/**
	 * 
	 * @param warName - War's name for logging purpose.
	 * @param dest - Destination in string format.
	 * @param launchTime - The time to countdown before launch (in seconds).
	 * @param flyTime - The time of flight (in seconds)
	 * @param damage - The damage the missile will deal if it hits the target.
	 */
	public Missile(String warName, String dest, int launchTime, int flyTime,
			int damage) {
		this(warName, "M" + (idGenerator++), dest, launchTime, flyTime, damage);
	}

	/**
	 * 
	 * @param warName - War's name for logging purpose.
	 * @param id - The missile's ID.
	 * @param dest - Destination in string format.
	 * @param launchTime - The time to countdown before launch (in seconds).
	 * @param flyTime - The time of flight (in seconds)
	 * @param damage - The damage the missile will deal if it hits the target.
	 */
	public Missile(String warName, String id, String dest, int launchTime,
			int flyTime, int damage) {
		this.dest = dest;
		this.launchTime = launchTime * 1000;
		this.flyTime = flyTime * 1000;
		this.id = id;
		this.damage = damage;
		logger = Logger.getLogger(warName);
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
	public void run() {
		logMStart();

		try {
			state = State.LAUNCHING;
			launchpad.acquire(); //Acquire launching permission from launcher	
			launch();
		} catch (InterruptedException e1) {
			// should never get this exception
			
		} 

		if (launcher.getLState() != Launcher.State.DESTROYED){
			if (launcher.getLState() == Launcher.State.HIDDEN) {
				new Thread() { //New thread for changing launcher's state.
					public void run() {
						launcher.setVisible();
						try {
							sleep((3 + (int) (Math.random() * 6))*1000); //Set visible for random amount of time (3-6 seconds)
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						launcher.setHidden();
					};
				}.start();
			}
			try {
				state = State.FLYING;
				launcher.incrementMissilesFired();
				fly();
				logHit();	    
				state = State.HIT;
			} catch (InterruptedException e) {
				state = State.INTERCEPTED;
			}
			launcher.remove(this);
		}

		launchpad.release(); //Release launchpad for next missile.
	}

	private void logMStart() {
		logger.log(Level.INFO, this + " has started", this);	
	}

	private void launch() throws InterruptedException {
		sleep(launchTime);
	}

	private void fly() throws InterruptedException {
		logger.log(Level.WARNING, this + " has been launched from " + launcher
				+ " to " + dest + "!", new Object[] { this, launcher });
		sleep(flyTime);
	}

	private void logHit() {
		logger.log(Level.SEVERE, this.id + " has hit " + dest + "!" + " ("
				+ damage + ")", new Object[]{ this, launcher });
	}

	@Override
	public void destruct() {
		interrupt();
	}

	public String getDestination() {
		return dest;
	}

	public void setLauncher(Launcher launcher) {
		this.launcher = launcher;
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * 
	 * @return The missile's current state
	 */
	public State getMState() {
		return state;
	}




	public int getDamage() {
		return damage;
	}

	public Launcher getLauncher() {
		return launcher;
	}

	public void setLaunchpad(Semaphore launchpad) {
		this.launchpad = launchpad;
	}

	protected void setLast() {
		last=true;
	}
}
