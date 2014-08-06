import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IronDome {
    private static int idGenerator = 100;
    private String id;
    private Radar radar;
    private Logger logger;

    public IronDome(String warName) {
	radar = new Radar(this);
	logger = Logger.getLogger(warName);
	id = "IronDome-" + (idGenerator++);
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

    public void intercept(Missile m) {
	new Thread(new Runnable() {
	    public void run() {
		launchCounterMissile(m);
	    }
	}).start();
    }

    private void launchCounterMissile(Missile m) {
	try {
	    Thread.sleep((long) (200+(Math.random() * 15 * 1000)));
	    if(!m.isAlive())
		logFailedInterception(m);
	    else{
		if(m.getMState()==Missile.State.FLYING){
		    m.destruct();
		    logInterception(m);
		}
	    }
	} catch (InterruptedException e) {
	}
    }

    private void logInterception(Missile m) {
	logger.log(Level.INFO, this + " has intercepted " + m, this);
    }

    private void logFailedInterception(Missile m) {
	logger.log(Level.SEVERE, this + " has failed to intercepted " + m, this);
    }

    public void start() {
	radar.start();
    }

    @Override
    public String toString() {
	return id;
    }

    public void Stop() {
	radar.Stop();
    }
}
