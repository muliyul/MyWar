
public class IronDome{
    private Radar radar;

    public IronDome() {
	radar=new Radar(this);
    }

    public void intercept(Missile m) {
	new Thread(new Runnable() {
	    public void run() {
		launchCounterMissile();
		m.destruct();
	    }
	}).start();
    }

    private void launchCounterMissile() {
	try {
	    Thread.sleep(90*1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void start() {
	radar.start();
    }

}
