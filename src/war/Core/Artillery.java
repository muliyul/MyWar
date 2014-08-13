package war.Core;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import war.Core.Artillery.Type;
import war.Utils.ObjectFilter;
import war.Utils.WarFormatter;

public class Artillery extends Thread {
	public enum Type {
		SHIP, PLANE
	}

	private static int idGenerator = 100;
	private String id;
	private Type type;
	private boolean isRunning;
	private Logger logger;
	private Vector<Target> targets;
	private int launchersIntercepted;

	public Artillery(String warName, String id, Type type) {
		this.id = id;
		this.logger = Logger.getLogger(warName);
		this.type = type;
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

	public Artillery(String warName, Type type) {
		this(warName, "A-" + (idGenerator++), type);
	}

	public void Stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			if(targets.size()>0)
				targets.remove(0).intercept();
		}
	}

	@Override
	public String toString() {
		return id;
	}
	
	public synchronized void addTarget(Target t){
		targets.add(t);
	}
}
