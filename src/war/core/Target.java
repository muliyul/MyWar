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
     * 		  - The war's name (for logging purposes).
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
     * 		  - The war's name (for logging purposes).
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
		    Thread.sleep((int) (Math.random() * 3)); //TODO
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
		    Thread.sleep((int) (Math.random() * 3)); //TODO
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
	    logger.log(Level.INFO, i + " is trying to intercept "
		    + ((Missile) target), i);
	} else if (target instanceof Launcher) {
	    Artillery a = (Artillery) origin;
	    logger.log(Level.INFO, a.getType().toString() + " " + a
		    + " is trying to destroy " + ((Launcher) target), a);
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
		    + " has failed to intercept " + ((Missile) target), i);
	    m.getLauncher().addDamage(m.getDamage());
	} else if (target instanceof Launcher) {
	    Artillery a = ((Artillery) origin);
	    logger.log(Level.SEVERE, a.getType().toString() + " " + a
		    + " has failed to destroy " + ((Launcher) target), a);
	}
    }

    /**
     * Logs an interception of a missile or a launcher.
     */
    private void logInterception() {
        if (target instanceof Missile) {
            IronDome i = (IronDome) origin;
            logger.log(Level.INFO,
        	    i + " has intercepted " + ((Missile) target), i);
            i.incrementMissilesIntercepted();
        } else if (target instanceof Launcher) {
            Artillery a = (Artillery) origin;
            logger.log(Level.INFO, a.getType().toString() + " " + a
        	    + " has been destroyed " + ((Launcher) target), a);
            a.incrementLaunchersDestroyed();
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
	return "Target [target=" + target + ", interceptionTime="
		+ interceptionTime + ", origin=" + origin + "]";
    }

}
