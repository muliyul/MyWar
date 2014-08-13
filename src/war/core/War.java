package war.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import war.io.Console_IO;
import war.io.IOHandler;
import war.utils.WarFormatter;
import war.utils.XMLParser;

public class War {

    public static void main(String[] args) {
	IOHandler io = new Console_IO();
	War war = loadMenu(io);
	int choice;
	if (war != null) {
	    boolean quit = false;
	    do {
		choice = war.showWarMenu(io);
		quit = war.invokeChoice(choice, io);
	    } while (!quit);
	}
    }

    private static War loadMenu(IOHandler io) {
	String warName = io.getInput("Enter war name:");
	int choice = -1;
	do {
	    choice = io.getChoice("War menu", "1) Enter manually",
		    "2) Parse from XML");
	} while (choice != 1 && choice != 2);
	checkDirectorys(warName);
	if (choice == 1) {
	    return new War(warName);
	} else if (choice == 2) {
	    return XMLParser.parseWar(warName, "config.xml");
	} else
	    return null;
    }

    private int showWarMenu(IOHandler io) {
	return io.getChoice("Here comes the menu!", "1) Add Missile",
		"2) Add Launcher", "3) Add Iron Dome", "4) Add Artillery",
		"5) Show Inventory", isActive ? "6) End war and show stats"
			: "6) Start war");
    }

    private static void checkDirectorys(String name) {
	File logdir = new File("logs");
	if (!logdir.exists())
	    logdir.mkdir();
	File wardir = new File("logs/" + name);
	if (!wardir.exists())
	    wardir.mkdir();
    }

    private Logger logger;
    private List<IronDome> domes;
    private List<Launcher> launchers;
    private List<Artillery> artillery;
    private String name;
    private boolean isActive;
    private int missilesFired;
    private int missilesIntercepted;
    private int totalDamage;
    private int launchersIntercepted;
    private IronDome mostSuccessfulIronDome;
    private Launcher mostSuccessfulLauncher;
    private Artillery mostSuccessfulArtillery;

    public War(String warName) {
	this(warName, new Vector<>(), new Vector<>(), new Vector<>());
    }

    public War(String warName, List<IronDome> domes, List<Launcher> launchers,
	    List<Artillery> artillery) {
	this.isActive = false;
	this.domes = domes;
	this.launchers = launchers;
	this.artillery = artillery;
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

    private void start(IOHandler io) {
	for (IronDome d : domes)
	    d.start();
	for (Launcher l : launchers)
	    l.start();
	for (Artillery a : artillery)
	    a.start();
	io.showMessege(this.name + " is active!\n");
    }

    private void end() {
	for (IronDome d : domes)
	    d.Stop();
	for (Launcher l : launchers)
	    l.Stop();
	for (Artillery a : artillery)
	    a.Stop();

    }

    private boolean invokeChoice(int choice, IOHandler io) {
	boolean correctInput = false;
	do {
	    try {
		switch (choice) {
		case 1: {
		    correctInput = addMissile(io);
		    break;
		}
		case 2: {
		    correctInput = addLauncher(io);
		    break;
		}
		case 3: {
		    addIronDome(io);
		    break;
		}
		case 4: {
		    correctInput = addArtillery(io);
		    break;
		}
		case 5: {
		    showInventory(io);
		    return false;
		}
		case 6: {
		    if (isActive) {
			end();
			showStatistics(io);
			return true;
		    } else {
			isActive = true;
			start(io);
			correctInput = true;
		    }
		}
		}
	    } catch (IllegalArgumentException e) {
		correctInput = false;
	    }
	} while (!correctInput);
	io.flushBuffers();
	return false;
    }

    private void showInventory(IOHandler io) {
	io.showMessege(this + " war inventory:");
	io.showMessege("Launchers: " + launchers.size());
	for (Launcher l : launchers) {
	    io.showMessege(l + ":");
	    io.showMessege(l.getMissiles().toString());
	}
	io.showMessege("Iron Domes: " + domes.size());
	for (IronDome id : domes) {
	    io.showMessege(id + "");
	}
	io.showMessege("Artillery: " + artillery.size());
	for (Artillery a : artillery) {
	    io.showMessege(a + "");
	}
    }

    private boolean addMissile(IOHandler io) {
	Launcher selectedLauncher = null;
	try {
	    Launcher[] availableLaunchers = (Launcher[]) launchers.toArray();
	    String[] availableLaunchersStrings = (String[]) launchers.toArray();
	    int choice = io.getChoice("Enter launcher to add to:",
		    availableLaunchersStrings);
	    if (choice < 0 || choice > availableLaunchers.length)
		throw new IllegalArgumentException(
			"Please choose from selected values");
	    selectedLauncher = availableLaunchers[choice - 1];
	    String dest = io.getInput("Enter destination:");
	    int launchTime = io.getInt("Enter launch time:");
	    if (launchTime < 0)
		throw new IllegalArgumentException(
			"Please enter positive values");
	    int flyTime = io.getInt("Enter fly time:");
	    if (flyTime <= 0)
		throw new IllegalArgumentException(
			"Please enter positive values");
	    int damage = io.getInt("Enter damage:");
	    selectedLauncher.addMissile(new Missile(name, dest, launchTime,
		    flyTime, damage));
	    return true;
	} catch (Exception e) {
	    return false;
	}

    }

    private boolean addLauncher(IOHandler io) {
	try {
	    String id = io.getInput("Enter id:");
	    Launcher.State[] states = { Launcher.State.ACTIVE,
		    Launcher.State.HIDDEN }; // TODO remove destroyed
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
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    private void addIronDome(IOHandler io) {
	String id = io.getInput("Enter id:");
	domes.add(new IronDome(name, id));
    }

    private boolean addArtillery(IOHandler io) {
	String id = io.getInput("Enter id:");
	Artillery.Type[] typeList = Artillery.Type.values();
	List<String> typeListStrings = new Vector<>();
	for (int i = 0; i < typeList.length; i++) {
	    typeListStrings.add(typeList[i].toString());
	}
	int choice = io.getChoice((String[]) typeListStrings.toArray());
	try {
	    artillery.add(new Artillery(name, id, typeList[choice]));
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    private void showStatistics(IOHandler io) {
	double percent = 0;
	int i = 0;
	int domesSize = domes.size();
	int launchersSize = launchers.size();
	int artillerySize = artillery.size();
	Set<Thread> threads = Thread.getAllStackTraces().keySet();
	if (domesSize > 0)
	    mostSuccessfulIronDome = domes.get(0);
	if (launchersSize > 0)
	    mostSuccessfulLauncher = launchers.get(0);
	if (artillerySize > 0)
	    mostSuccessfulArtillery = artillery.get(0);

	io.showMessege("Waiting for all threads to die for accurate results",
		"This may take a while");
	threads.removeIf(new Predicate<Thread>() {
	    public boolean test(Thread t) {
		return t.toString().contains("system")
			|| t.toString().contains("main");
	    }
	});

	try {
	    for (Thread t : threads) {
		t.join();
		percent = 100 * (i++) / threads.size();
		io.showProgressBar(percent);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	for (i = 0; i < domesSize; i++) {
	    missilesIntercepted += domes.get(i).getMissilesIntercepted();
	    if (domes.get(i).getMissilesIntercepted() > mostSuccessfulIronDome
		    .getMissilesIntercepted())
		mostSuccessfulIronDome = domes.get(i);
	}

	for (i = 0; i < launchersSize; i++) {
	    missilesFired += launchers.get(i).getMissilesFired();
	    if (launchers.get(i).getTotalDamage() > mostSuccessfulLauncher
		    .getTotalDamage())
		mostSuccessfulLauncher = launchers.get(i);
	    if (launchers.get(i).getLState() == Launcher.State.DESTROYED)
		launchersIntercepted++;
	}

	for (i = 0; i < artillerySize; i++) {
	    launchersIntercepted += artillery.get(i).getLaunchersIntercepted();
	    if (artillery.get(i).getLaunchersIntercepted() > mostSuccessfulArtillery
		    .getLaunchersIntercepted())
		mostSuccessfulArtillery = artillery.get(i);
	}

	io.showMessege(this.toString());
    }

    @Override
    public String toString() {
	return name + " has caused " + totalDamage + " damage to Israel."
		+ WarFormatter.EOL + "Missiles fired: " + missilesFired
		+ WarFormatter.EOL + "Missiles intercepted: "
		+ missilesIntercepted + WarFormatter.EOL
		+ "Launchers intercepted: " + launchersIntercepted
		+ WarFormatter.EOL;
    }
}
