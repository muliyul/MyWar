import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Launcher extends Thread implements Destructable{
    private static int idGenerator=100;
    private String id;
    private List<Missile> missiles;
    private boolean isRunning;
    private Logger logger;

    public Launcher(String warName) {
	this.id="L-"+ (idGenerator++);
	missiles=new ArrayList<>();
	try{
	    this.logger=Logger.getLogger(warName+"");
	    FileHandler fh = new FileHandler("logs/" + warName + "/" + id
		    + ".log");
	    fh.setFormatter(new WarFormatter());
	    logger.addHandler(fh);
	}catch(SecurityException e){
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public synchronized void addMissile(Missile m){
	missiles.add(m);
	m.setLauncher(this);
    }

    @Override
    public void destruct() {
	interrupt();
    }

    @Override
    public void run() {
	isRunning=true;
	while(isRunning){
	    synchronized(this){
		if(missiles.size()>0){
		    Missile m=missiles.remove(0);
		    m.start();
		    logLaunch(m);
		}
	    }
	}
    }

    private void logLaunch(Missile m) {
	logger.log(Level.WARNING, m + " has been launched from " + this,this);
    }

    public void Stop() {
	isRunning=false;
    }
}
