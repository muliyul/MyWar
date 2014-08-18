package war.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import war.core.enemy.Launcher;
import war.core.enemy.Missile;
import war.core.friendly.Artillery;
import war.core.friendly.IronDome;

public class Target {
    private static Logger logger = Logger.getLogger("WarLogger");
    private Thread target;
    private Object origin;
    private int interceptionTime;

    /**
     * 
     * @param m - Missile to target
     * @param interceptionTime - Time to destroy missile
     * @param ironD - Iron-Dome in charge of interception
     */
    public Target(Missile m, int interceptionTime, IronDome ironD) {
	this.target = m;
	this.origin = ironD;
	this.interceptionTime = interceptionTime;
    }
    /**
     * 
     * @param ml - Launcher to target
     * @param interceptionTime - Time to destroy launcher
     * @param ironD - Artillery in charge of interception
     */
    public Target(Launcher ml, int destroyTime, Artillery ld) {
	this.target = ml;
	this.origin = ld;
	this.interceptionTime = destroyTime;
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
		    ml.destruct();
		    logInterception();
		} else {
		    logFailedInterception();
		}
	    }
	}

    }
    
    /**
     * Logs an attempt to intercept missile or launcher.
     */
    private void logInterceptionTry() {
	if (target instanceof Missile) {
	    IronDome i = ((IronDome) origin);
	    logger.log(Level.INFO, i + " is trying to intercept "
		    + ((Missile) target), origin);
	} else if (target instanceof Launcher) {
	    Artillery a = ((Artillery) origin);
	    logger.log(Level.INFO, a.getType().toString() + " " + a
		    + " is trying to intercept " + ((Launcher) target), origin);
	}
    }

    /**
     * Logs an interception of a missile or a launcher.
     */
    private void logInterception() {
	if (target instanceof Missile) {
	    IronDome i = ((IronDome) origin);
	    logger.log(Level.INFO,
		    i + " has intercepted " + ((Missile) target), origin);
	    i.incrementMissilesIntercepted();
	} else if (target instanceof Launcher) {
	    Artillery a = ((Artillery) origin);
	    logger.log(Level.INFO, a.getType().toString() + " " + a + " has intercepted "
		    + ((Launcher) target), origin);
	    a.incrementLaunchersIntercepted();
	}
    }

    /**
     * Logs a failed interception of a missile or a launcher.
     */
    private void logFailedInterception() {
	if (target instanceof Missile) {
	    Missile m = ((Missile) target);
	    logger.log(Level.SEVERE, "Iron dome " + ((IronDome) origin)
		    + " has failed to intercept " + m , origin);
	    m.getLauncher().addDamage(m.getDamage());
	} else if (target instanceof Launcher) {
	    Artillery a = ((Artillery) origin);
	    logger.log(Level.SEVERE, a.getType().toString() + " " + a + " has failed to intercept "
		    + ((Launcher) target), origin);
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
