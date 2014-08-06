import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import IOPackage.GUI_UI;
import IOPackage.IOHandler;

public class War {

    public static void main(String[] args) {
	io = new GUI_UI();
	int choice = io.getChoice("War menu", "1) Randomize",
		"2) Parse from xml");
	String name = io.getInput("Enter war name:");
	War war = null;
	if (choice == 1) {
	    checkDirectorys(name);
	    List<Launcher> launchers = generateRandomLaunchers(name);
	    List<IronDome> domes = generateRandomDomes(name);
	    war = new War(name, domes, launchers);
	} else if (choice == 2) {
	    war = XMLParser.parseWar(name, name + ".xml");
	}
	try {
	    war.start();
	} catch (NullPointerException e) {
	}
	do {
	    choice = war.showWarMenu();
	    invokeChoice(choice);
	} while (choice != 6);
	try {
	    war.end();
	    war.showStatistics();
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

    private static List<IronDome> generateRandomDomes(String name) {
	List<IronDome> domes = new ArrayList<>(3);
	for (int i=0;i<3;i++)
	    domes.add(new IronDome(name));
	return domes;
    }

    private static List<Launcher> generateRandomLaunchers(String name) {
	List<Launcher> launchers = new ArrayList<>();
	Random r = new Random();
	for (int i=0;i<5;i++)
	    launchers.add(new Launcher(name));
	List<Missile> missiles = new ArrayList<>();
	for (int i=0;i<50;i++) {
	    Missile m=new Missile(name);
	    m.setLauncher(launchers.get(r.nextInt(launchers.size())));
	    missiles.add(m);
	}
	return launchers;
    }

    private static IOHandler io;
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
	    FileHandler fh = new FileHandler("logs/" + warName + "/" + warName
		    + ".log");
	    fh.setFormatter(new WarFormatter());
	    logger.addHandler(fh);
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void start() {
	for (IronDome d : domes)
	    d.start();
	for (Launcher l : launchers)
	    l.start();
    }

    public static void showMessege(String string) {

    }

    private void showStatistics() {

    }

    private int showWarMenu() {
	return io
		.getChoice("Here comes menu!", "1) Tada", "2) Tadi", "3) Tade");
    }
}
