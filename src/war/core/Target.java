package war.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import war.core.enemy.Launcher;
import war.core.enemy.Missile;
import war.core.friendly.Artillery;
import war.core.friendly.IronDome;

public class Target {
	private Logger logger;
	private Thread target;
	private Object origin;
	private int interceptionTime;

	/**
	 * @param warName
	 *            - The war's name (for logging purposes).
	 * @param m
	 *            - Missile to target.
	 * @param interceptionTime
	 *            - Time to destroy missile.
	 * @param ironD
	 *            - Iron-Dome in charge of interception.
	 */
	public Target(String warName, Missile m, int interceptionTime,
			IronDome ironD) {
		this(warName);
		this.target = m;
		this.origin = ironD;
		this.interceptionTime = interceptionTime;
	}

	/**
	 * @param warName
	 *            - The war's name (for logging purposes).
	 * @param ml
	 *            - Launcher to target.
	 * @param interceptionTime
	 *            - Time to destroy launcher.
	 * @param ironD
	 *            - Artillery in charge of interception.
	 */
	public Target(String warName, Launcher ml, int destroyTime, Artillery ld) {
		this(warName);
		this.target = ml;
		this.origin = ld;
		this.interceptionTime = destroyTime;
	}

	public Target(String warName) {
		this.logger = Logger.getLogger(warName);
	}

	/**
	 * Intercepts a missile or a launcher.
	 */
	public void intercept() {
		if (target instanceof Missile) {
			try {
				Thread.sleep(interceptionTime * 1000);
				synchronized (origin) {
					logInterceptionTry();
					Thread.sleep((1 + (int) (Math.random() * 3)) * 1000); // TODO
					Missile m = ((Missile) target);
					if (target.isAlive()
							&& m.getMState() == Missile.State.FLYING) {
						m.destruct();
						logInterception();
					} else {
						logFailedInterception();
					}
				}

			} catch (InterruptedException e) {
			}
		} else if (target instanceof Launcher) {
			Launcher ml = (Launcher) target;
			try {
				Thread.sleep(interceptionTime * 1000);
				synchronized (origin) {
					logInterceptionTry();
					Thread.sleep((1 + (int) (Math.random() * 3)) * 1000); // takes between 1-3 seconds to try to destroy a launcher		
					if (ml.getLState() == Launcher.State.ACTIVE) {
						ml.destruct();
						logInterception();
					} else {
						logFailedInterception();
					}
				}
			} catch (InterruptedException e) {
			}
		}

	}

	/**
	 * Logs an attempt to intercept missile or launcher.
	 */
	private void logInterceptionTry() {
		if (target instanceof Missile) {
			IronDome i = (IronDome) origin;
			Missile m = (Missile) target;
			logger.log(Level.INFO, i + " is trying to intercept " + m, i);
		} else if (target instanceof Launcher) {
			Artillery a = (Artillery) origin;
			Launcher l = (Launcher) target;
			logger.log(Level.INFO, a.getType().toString() + " " + a
					+ " is trying to destroy " + l, a);
		}
	}

	/**
	 * Logs an interception of a missile or a launcher.
	 */
	private void logInterception() {
		if (target instanceof Missile) {
			IronDome i = (IronDome) origin;
			Missile m = (Missile) target;
			Launcher l = m.getLauncher();
			logger.log(Level.INFO, i + " has intercepted " + m, new Object[] {
					m, i, l });
			i.incrementMissilesIntercepted();
		} else if (target instanceof Launcher) {
			Artillery a = (Artillery) origin;
			Launcher l = (Launcher) target;
			logger.log(Level.INFO, a.getType().toString() + " " + a
					+ " destroyed " + l, new Object[] { a, l });
			a.incrementLaunchersDestroyed();
		}
	}

	/**
	 * Logs a failed interception of a missile or a launcher.
	 */
	private void logFailedInterception() {
		if (target instanceof Missile) {
			IronDome i = (IronDome) origin;
			Missile m = (Missile) target;
			logger.log(Level.SEVERE, "Iron dome " + i
					+ " has failed to intercept " + m, i);
			m.getLauncher().addDamage(m.getDamage());
		} else if (target instanceof Launcher) {
			Artillery a = ((Artillery) origin);
			Launcher l = (Launcher) target;
			if (l.getLState() != Launcher.State.DESTROYED){
				logger.log(Level.SEVERE, a.getType().toString() + " " + a
						+ " has failed to destroy " + l, a);
			} else {
				logger.log(Level.SEVERE, l + " has already been destroyed" 
						, a);
			}
		}
	}

	public int getInterceptionTime() {
		return interceptionTime;
	}

	public Thread getTarget() {
		return target;
	}

	/**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return target.toString();
	}

}
