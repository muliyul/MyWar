import java.io.IOException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Missile extends Thread implements Destructable {
    public enum Destination {
	BEER_SHEVA, ASHDOD, ASHKELON, TEL_AVIV, RAMAT_GAN, KIRYAT_EKRON
    }

    private static int idGenerator = 100;
    private String id;
    private Destination dest;
    private long launchTime;
    private long flyTime;
    private Launcher launcher;
    private Logger logger;

    public Missile(Destination dest, int launchTime, int flyTime,
	    Launcher launcher) {
	this.dest = dest;
	this.launchTime = launchTime * 1000;
	this.flyTime = flyTime * 1000;
	this.launcher = launcher;
	this.id = "M-" + (idGenerator++);
    }

    public Missile(String warName) {
	Destination[] dArr = Destination.values();
	Random r = new Random();
	dest = dArr[r.nextInt(dArr.length)];
	launchTime = 50 + r.nextInt(2 * 1000);
	flyTime = 15 * 1000 + r.nextInt(15 * 1000);
	logger = Logger.getLogger(warName);
	id = "M-" + (idGenerator++);
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
	try {
	    synchronized (launcher) {
		launch();
	    }
	} catch (InterruptedException e) {
	    War.showMessege("FATAL SYSTEM ERROR!");
	    e.printStackTrace();
	    return;
	}
	try {
	    fly();
	} catch (InterruptedException e) {
	}
    }

    private void launch() throws InterruptedException {
	sleep(launchTime);
	logger.log(Level.WARNING,this + " has launched from " + launcher + "!", this);
    }

    private void fly() throws InterruptedException {
	sleep(flyTime);
    }

    @Override
    public void destruct() {
	interrupt();
    }

    public String getDestination() {
	return dest.toString();
    }

    public void setLauncher(Launcher launcher) {
	this.launcher = launcher;
    }
    
    @Override
    public String toString() {
	return id;
    }

}
