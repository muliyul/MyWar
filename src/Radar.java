import java.util.Iterator;
import java.util.Set;

public class Radar extends Thread implements Detector {
    private Object owner;
    private boolean isRunning;

    public Radar(Object o) {
	owner = o;
	isRunning=true;
    }

    @Override
    public void run() {
	while (isRunning) {
	    Set<Thread> threads = Thread.getAllStackTraces().keySet();
	    Iterator<Thread> it = threads.iterator();
	    while (it.hasNext()) {
		Thread t = it.next();
		if (t instanceof Missile) {
		    onDetect(t);
		}
	    }
	}
    }

    @Override
    public synchronized void onDetect(Object o) {
	IronDome id = (IronDome) owner;
	Missile m = (Missile) o;
	id.intercept(m);
    }

    public void Stop() {
	isRunning=false;
    }

}
