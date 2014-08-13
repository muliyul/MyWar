package war.Core;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import war.IO.Console_IO;
import war.IO.IOHandler;
import war.Utils.WarFormatter;
import war.Utils.XMLParser;

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
		} else if(choice==2) {
			return XMLParser.parseWar(warName, "config.xml");
		} else return null;
	}

	private int showWarMenu(IOHandler io) {
		return io.getChoice("Here comes the menu!", "1) Add Missile",
				"2) Add Launcher", "3) Add Iron Dome", "4) Add Artillery",
				"5) Show Inventory",isActive ? "6) End war and show stats" : "6) Start war");
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
	public static final Object Stats_Tracker = new Object();
	private static final Random random = new Random();

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
					showInventory(io);
					break;
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
		for (IronDome id : domes){
			io.showMessege(id + "");
		}
		io.showMessege("Artillery: " + artillery.size());
		for (Artillery a : artillery){
			io.showMessege(a + "");
		}
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
		String dest = io.getInput("Enter destination:");
		int launchTime = io.getInt("Enter launch time:");
		if (launchTime < 0)
			throw new IllegalArgumentException("Please enter positive values");
		int flyTime = io.getInt("Enter fly time:");
		if (flyTime <= 0)
			throw new IllegalArgumentException("Please enter positive values");
		int damage = io.getInt("Enter damage:");
		selectedLauncher.addMissile(new Missile(name, dest, launchTime,
				flyTime, damage));
	}

	private void addLauncher(IOHandler io) {
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
	}

	private void addIronDome(IOHandler io) {
		String id = io.getInput("Enter id:");
		domes.add(new IronDome(name, id));
	}

	private void addArtillery(IOHandler io) {
		String id = io.getInput("Enter id:");
		Artillery.Type[] typelist = Artillery.Type.values();
		artillery.add(new Artillery(name, id, typelist[random
				.nextInt(typelist.length)]));
	}

	private void showStatistics(IOHandler io) {

		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		io.showMessege("Waiting for all threads to die for accurate results");
		threads.removeIf(new Predicate<Thread>() {
			public boolean test(Thread t) {
				return t.toString().contains("system")
						|| t.toString().contains("main");
			}
		});

		double percent = 0;
		int i = 0;
		try {
			for (Thread t : threads) {
				t.join();
				percent = 100 * (i++) / threads.size();
				io.showProgressBar(percent);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (IronDome d : domes) {
			System.out.println(d);
			missilesIntercepted += d.getMissilesIntercepted();
		}

		for (Launcher l : launchers) {
			System.out.println(l);
			missilesFired += l.getMissilesFired();
			if (l.getLState() == Launcher.State.DESTROYED)
				launchersIntercepted++;
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
