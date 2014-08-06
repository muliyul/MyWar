import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import IOPackage.IOHandler;


public class War{
    
    public static void main(String[] args) {
	int choice=io.getChoice("War menu","1) Randomize","2) Parse from xml");
	String name=io.getInput("Enter war name:");
	War war = null;
	if(choice==1){
	    int id=100;
	    Random r=new Random();
	    List<Launcher> launchers=new ArrayList<>();
	    for(Launcher l: launchers)
		l=new Launcher("L"+(id++));
	    List<Missile> missiles=new ArrayList<>();
	    for(Missile m: missiles){
		m=new Missile();
		m.setLauncher(launchers.get(r.nextInt(launchers.size())));
	    }
	    List<IronDome> domes=new ArrayList<>();
	    for(IronDome d: domes)
		d=new IronDome();
	    war=new War(name, domes, launchers);
	}
	else if(choice==2){
	    war=XMLParser.parseWar(name,name+".xml");
	}
	war.start();
    }
    
    private static IOHandler io;
    private Logger logger;
    private List<IronDome> domes;
    private List<Launcher> launchers;
    private String name;
    
    public War(String name,List<IronDome> domes,List<Launcher> launchers) {
	this.domes=domes;
	this.launchers=launchers;
	this.name=name;
	this.logger=Logger.getLogger(name+"");
    }

    private void start() {
	for(IronDome d: domes)
	    d.start();
	for(Launcher l: launchers)
	    l.start();
    }

    public static void showMessege(String string) {
	
    }
}
