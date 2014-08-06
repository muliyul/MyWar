import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import IOPackage.ConsoleIO;
import IOPackage.GUI_IO;
import IOPackage.IOHandler;

public class War {

    public static void main(String[] args) {
	IOHandler io = new ConsoleIO();
	int choice = io.getChoice("War menu", "1) Randomize",
		"2) Parse from xml");
	String warName = io.getInput("Enter war name:");
	War war = null;
	if (choice == 1) {
	    checkDirectorys(warName);
	    List<Launcher> launchers = generateRandomLaunchers(warName);
	    List<IronDome> domes = generateRandomDomes(warName);
	    war = new War(warName, domes, launchers);
	} else if (choice == 2) {
	    war = XMLParser.parseWar(warName, warName + ".xml");
	}
	try {
	    war.start();
	} catch (NullPointerException e) {
	}
	do {
	    choice = war.showWarMenu(io);
	    invokeChoice(choice);
	} while (choice != 6);
	try {
	    war.end();
	    war.showStatistics(io);
	} catch (NullPointerException e) {
	}
    }

    private static void invokeChoice(int choice) {

    }

    private void end() {
	for (IronDome d : domes)
	    d.Stop();
	for (Launcher l : launchers)
	    l.Stop();

    }

    private static void checkDirectorys(String name) {
	File logdir = new File("logs");
	if (!logdir.exists())
	    logdir.mkdir();
	File wardir = new File("logs/" + name);
	if (!wardir.exists())
	    wardir.mkdir();
    }

    private static List<IronDome> generateRandomDomes(String warName) {
	List<IronDome> domes = new ArrayList<>(1);
	for (int i=0;i<3;i++)
	    domes.add(new IronDome(warName));
	return domes;
    }

    private static List<Launcher> generateRandomLaunchers(String warName) {
	List<Launcher> launchers = new ArrayList<>();
	Random r = new Random();
	for (int i=0;i<5;i++)
	    launchers.add(new Launcher(warName));
	for (int i=0;i<15;i++) {
	    Missile m=new Missile(warName);
	    int launchNum=r.nextInt(launchers.size());
	    m.setLauncher(launchers.get(launchNum));
	    launchers.get(launchNum).addMissile(m);
	}
	return launchers;
    }

    private Logger logger;
    private List<IronDome> domes;
    private List<Launcher> launchers;
    private String name;

    public War(String warName, List<IronDome> domes, List<Launcher> launchers) {
	this.domes = domes;
	this.launchers = launchers;
	this.name = warName;
	try {
	    this.logger = Logger.getLogger(warName + "");
	    logger.setUseParentHandlers(false);
	    logger.setLevel(Level.FINEST);
	    FileHandler fh = new FileHandler("logs/" + warName + "/" + warName
		    + ".log");
	    fh.setFormatter(new WarFormatter());
	    logger.addHandler(fh);
	} catch (SecurityException | IOException e) {
	    e.printStackTrace();
	}
    }

    private void start() {
	for (IronDome d : domes)
	    d.start();
	for (Launcher l : launchers)
	    l.start();
	System.out.println(this.name + " is active!");
    }

    private void showStatistics(IOHandler io) {

    }

    private int showWarMenu(IOHandler io) {
	return io
		.getChoice("Here comes menu!", "1) Tada", "2) Tadi", "3) Tade");
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
}
