package war.Core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Target{
	private static Logger logger = Logger.getLogger("WarLogger");
	private Thread target;
	private Object origin;
	private int interceptionTime;

	public Target(Missile m, int interceptionTime, IronDome ironD) {
		this.target = m;
		this.origin = ironD;
		this.interceptionTime = interceptionTime;
	}

	public Target(Launcher ml, int destroyTime, Artillery ld) {
		this.target = ml;
		this.origin = ld;
		this.interceptionTime = destroyTime;
	}

	public void intercept() {
		if (target instanceof Missile) {
			try {
				Thread.sleep(interceptionTime * 1000);
				synchronized (origin) {
					logInterceptionTry();
					Missile m = ((Missile) target);
					if (target.isAlive()
							&& m.getMState() == Missile.State.FLYING) {
						target.interrupt();
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
			} catch (InterruptedException e) {
			}
			synchronized (origin) {
				if (ml.getLState() == Launcher.State.ACTIVE) {
					ml.intercept();
					logInterception();
				} else {
					logFailedInterception();
				}
			}
		}

	}

	private void logInterceptionTry() {
		if (target instanceof Missile) {
			logger.log(Level.INFO, ((IronDome) origin)
					+ " is trying to intercept " + ((Missile) target), origin);
		} else if (target instanceof Launcher) {
			logger.log(Level.INFO, ((Artillery) origin)
					+ " is trying to intercept " + ((Launcher) target), origin);
		}
	}

	private void logInterception() {
		if (target instanceof Missile) {
			logger.log(Level.INFO, ((IronDome) origin) + " has intercepted "
					+ ((Missile) target), origin);
		} else if (target instanceof Launcher) {
			logger.log(Level.INFO, ((Artillery) origin) + " has intercepted "
					+ ((Launcher) target), origin);
		}
	}

	private void logFailedInterception() {
		if (target instanceof Missile) {
			logger.log(Level.SEVERE, "Iron dome " + ((IronDome) origin)
					+ " has failed to intercept " + ((Missile) target), origin);
		} else if (target instanceof Launcher) {
			logger.log(Level.SEVERE, ((Artillery) origin)
					+ " has failed to intercept " + ((Launcher) target), origin);
		}
	}

	public int getInterceptionTime() {
		return interceptionTime;
	}

	public Thread getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "Target [target=" + target + ", interceptionTime="
				+ interceptionTime + "]";
	}

}
