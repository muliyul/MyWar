import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import IOPackage.Console_IO;
import IOPackage.GUI_IO;
import IOPackage.IOHandler;

public class War {

    public static void main(String[] args) {
	IOHandler io = new Console_IO();
	War war = loadMenu(io);
	int choice;
	if (war != null) {
	    boolean quit = false;
	    do {
		choice = showWarMenu(io);
		quit = war.invokeChoice(choice, io);
	    } while (!quit);
	}
    }

    private static War loadMenu(IOHandler io) {
	String warName = io.getInput("Enter war name:");
	int choice = -1;
	do {
	    choice = io.getChoice("War menu", "1) Randomize",
		    "2) Parse from xml");
	} while (choice != 1 && choice != 2);
	if (choice == 1) {
	    checkDirectorys(warName);
	    List<Launcher> launchers = generateRandomLaunchers(warName);
	    List<IronDome> domes = generateRandomDomes(warName);
	    List<Artillery> artillery = generateRandomArtillery(warName);
	    return new War(warName, domes, launchers);
	} else {
	    return XMLParser.parseWar(warName, warName + ".xml");
	}
    }


    private static int showWarMenu(IOHandler io) {
	return io.getChoice("Here comes the menu!", "1) Add Missile",
		"2) Add Launcher", "3) Add Iron Dome", "4) Add Artillery",
		"5) End war and show stats");
    }

    private static void checkDirectorys(String name) {
	File logdir = new File("logs");
	if (!logdir.exists())
	    logdir.mkdir();
	File wardir = new File("logs/" + name);
	if (!wardir.exists())
	    wardir.mkdir();
    }
    
    private static List<Artillery> generateRandomArtillery(String warName) {
	List<Artillery> artillery = new ArrayList<>();
	for(int i=0;i<2;i++)
	    artillery.add(new Artillery(warName));
	return null;
    }

    private static List<IronDome> generateRandomDomes(String warName) {
	List<IronDome> domes = new ArrayList<>();
	for (int i = 0; i < 3; i++)
	    domes.add(new IronDome(warName));
	return domes;
    }

    private static List<Launcher> generateRandomLaunchers(String warName) {
	List<Launcher> launchers = new ArrayList<>();
	Random r = new Random();
	for (int i = 0; i < 3; i++)
	    launchers.add(new Launcher(warName));
	for (int i = 0; i < 7; i++) {
	    Missile m = new Missile(warName);
	    int launchNum = r.nextInt(launchers.size());
	    m.setLauncher(launchers.get(launchNum));
	    launchers.get(launchNum).addMissile(m);
	}
	return launchers;
    }

    private Logger logger;
    private List<IronDome> domes;
    private List<Launcher> launchers;
    private List<Artillery> artillery;
    private String name;
    private int missilesFired;
    private int missilesIntercepted;
    private int totalDamage;
    private int launchersIntercepted;
    private IronDome mostSuccessfulIronDome;
    private Launcher mostSuccessfulLauncher;
    private Artillery mostSuccessfulArtillery;

    public War(String warName, List<IronDome> domes, List<Launcher> launchers) {
	this.domes = domes;
	this.launchers = launchers;
	this.name = warName;
	this.missilesFired = 0;
	this.missilesIntercepted = 0;
	this.launchersIntercepted = 0;
	try {
	    this.logger = Logger.getLogger(warName + "");
	    logger.setUseParentHandlers(false);
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
	for(Artillery a : artillery)
	    a.start();
	System.out.println(this.name + " is active!\n");
    }

    private void end() {
	for (IronDome d : domes)
	    d.Stop();
	for (Launcher l : launchers)
	    l.Stop();
	for(Artillery a : artillery)
	    a.Stop();

    }

    private boolean invokeChoice(int choice, IOHandler io) {
	boolean correctInput = false;
	do {
	    try {
		switch (choice) {
		case 1: {
		    addMissile(io);
		    break;
		}
		case 2: {
		    addLauncher(io);
		    break;
		}
		case 3: {
		    addIronDome(io);
		    break;
		}
		case 4: {
		    addArtillery(io);
		    break;
		}
		case 5: {
		    end();
		    showStatistics(io);
		    return false;
		}
		}
	    } catch (IllegalArgumentException e) {
		correctInput = false;
	    }
	} while (!correctInput);
	return true;
    }

    private void addMissile(IOHandler io) {
	Launcher selectedLauncher = null;
	Launcher[] availableLaunchers = (Launcher[]) launchers.toArray();
	String[] availableLaunchersStrings = (String[]) launchers.toArray();
	int choice = io.getChoice("Enter launcher to add to:",
		availableLaunchersStrings);
	if (choice < 0 || choice > availableLaunchers.length)
	    throw new IllegalArgumentException(
		    "Please choose from selected values");
	selectedLauncher = availableLaunchers[choice - 1];
	Missile.Destination[] destinations = Missile.Destination.values();
	String[] destinationStrings = new String[destinations.length];
	for (int i = 0; i < destinations.length; i++) {
	    destinationStrings[i] = destinations[i].toString();
	}
	int destindex = io.getChoice("Enter destination:", destinationStrings);
	if (destindex < 0 || destindex > destinations.length)
	    throw new IllegalArgumentException(
		    "Please choose from selected values");
	int launchTime = io.getInt("Enter launch time:");
	if (launchTime < 0)
	    throw new IllegalArgumentException("Please enter positive values");
	int flyTime = io.getInt("Enter fly time:");
	if (flyTime <= 0)
	    throw new IllegalArgumentException("Please enter positive values");
	selectedLauncher.addMissile(new Missile(destinations[destindex],
		launchTime, flyTime, selectedLauncher));
    }

    private void addLauncher(IOHandler io) {
	String id = io.getInput("Enter id:");
	Launcher.State[] states = Launcher.State.values();
	String[] statesStrings = new String[states.length];
	for (int i = 0; i < states.length; i++) {
	    statesStrings[i] = states[i].toString();
	}
	int stateindex = io.getChoice("Choose state:", statesStrings);
	if (stateindex < 0 || stateindex > states.length)
	    throw new IllegalArgumentException(
		    "Please choose from selected values");
	launchers.add(new Launcher(name, id, states[stateindex]));
	if (io.yesNo("Do you wish to add missiles?"))
	    addMissile(io);
    }

    private void addIronDome(IOHandler io) {
	String id = io.getInput("Enter id:");
	domes.add(new IronDome(name, id));
    }

    private void addArtillery(IOHandler io) {
	String id = io.getInput("Enter id:");
	artillery.add(new Artillery(name, id));
    }

    private void showStatistics(IOHandler io) {

	for (IronDome d : domes) {
	    missilesIntercepted += d.getMissilesIntercepted();
	}

	for (Launcher l : launchers) {
	    missilesFired += l.getMissilesFired();
	    if (l.getLState() == Launcher.State.DESTROYED)
		launchersIntercepted++;
	}
	
	io.showMessege(this.toString());
    }

    @Override
    public String toString() {
	return name + " has caused " + totalDamage + WarFormatter.EOL
		+ "Missiles fired: " + missilesFired + WarFormatter.EOL
		+ "Missiles intercepted: " + missilesIntercepted
		+ WarFormatter.EOL + "Launchers intercepted: "
		+ launchersIntercepted + WarFormatter.EOL;
    }
}
