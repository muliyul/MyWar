package war.core.friendly;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import war.core.Target;
import war.utils.ObjectFilter;
import war.utils.WarFormatter;

public class IronDome extends Thread{
	private static int idGenerator = 100;
	private String id;
	private Logger logger;
	private int missilesIntercepted;
	private List<Target> targets;
	private boolean isRunning;

	/**
	 * 
	 * @param warName - The war's name (for logging purposes).
	 * @param id - The Iron-Dome's ID.
	 */
	public IronDome(String warName, String id) {
		this.id = id;
		this.logger = Logger.getLogger(warName);
		this.missilesIntercepted = 0;
		this.targets = new Vector<>();
		this.isRunning = false;
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

	/**
	 * 
	 * @param warName - The war's name (for logging purposes).
	 */
	public IronDome(String warName) {
		this(warName,"IronDome" + (idGenerator++));
	}
	
	@Override
	public void run() {
	    isRunning = true;
	    while(isRunning){
		if(targets.size()>0)
		    targets.remove(0).intercept();
	    }
	}

	@Override
	public String toString() {
		return id;
	}
	
	public int getMissilesIntercepted() {
		return missilesIntercepted;
	}

	
	public synchronized void assignTarget(Target target) {
		targets.add(target);
	}

	public synchronized void incrementMissilesIntercepted() {
	    missilesIntercepted++;
	}

	public void Stop() {
	    isRunning = false;
	}
}
