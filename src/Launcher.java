import java.util.ArrayList;
import java.util.List;


public class Launcher extends Thread implements Destructable{
    private String id;
    private List<Missile> missiles;
    private boolean isRunning;
    
    public Launcher(String id) {
	this.id=id;
	missiles=new ArrayList<>();
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
		missiles.remove(0).start();
	    }
	}
    }
}
