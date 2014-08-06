import java.util.Random;

public class Missile extends Thread implements Destructable {
    public enum Destination {
	BEER_SHEVA, ASHDOD, ASHKELON, TEL_AVIV, RAMAT_GAN, KIRYAT_EKRON
    }

    private Destination dest;
    private long launchTime;
    private long flyTime;
    private Launcher launcher;

    public Missile(Destination dest,int launchTime,int flyTime,Launcher launcher){
	this.dest=dest;
	this.launchTime=launchTime*1000;
	this.flyTime=flyTime*1000;
	this.launcher=launcher;
    }
    
    public Missile() {
	Destination[] dArr = Destination.values();
	Random r = new Random();
	dest = dArr[r.nextInt(dArr.length)];
	launchTime = 50 + r.nextInt(2 * 1000);
	flyTime = 15 * 1000 + r.nextInt(90 * 1000);
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

}
