package war.core;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import war.core.enemy.Launcher;
import war.core.enemy.Missile;
import war.core.friendly.Artillery;
import war.core.friendly.IronDome;
import war.io.Console_IO;
import war.io.IOHandler;
import war.utils.NoLauncherAvailableException;
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
				choice = war.showWarMenu(io, war.name);
				quit = war.invokeChoice(choice, io);
			} while (!quit);
		}
	}

	/**
	 * Shows up a load war menu.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return new initialized war object or null of an error occurred.
	 */
	private static War loadMenu(IOHandler io) {
		io.showMessege("******************************\n");
		io.showMessege(" Welcome to the War Simulator\n");
		io.showMessege("******************************\n");
		String warName = io.getInput("Enter war name:");
		int choice = -1;
		do {
			choice = io.getChoice("\nWar menu", new String[] {
					"Enter manually", "Parse from XML" });
		} while (choice != 1 && choice != 2);
		checkDirectorys(warName);
		if (choice == 1) {
			return new War(warName);
		} else if (choice == 2) {
			return XMLParser.parseWar(warName, "config.xml");
		} else
			return null;
	}

	/**
	 * Checks if the directory tree for log files exists, if not - creates them
	 * as following: ../logs/warName/
	 * 
	 * @param warName
	 *            - war's name.
	 */
	private static void checkDirectorys(String warName) {
		File logdir = new File("logs");
		if (!logdir.exists())
			logdir.mkdir();
		File wardir = new File("logs/" + warName);
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
		this(warName, new Vector<IronDome>(), new Vector<Launcher>(),
				new Vector<Artillery>());
	}

	/**
	 * 
	 * @param warName
	 *            - The name for the war.
	 * @param domes
	 *            - List of iron domes.
	 * @param launchers
	 *            - List of launchers.
	 * @param artillery
	 *            - List of artillery.
	 */
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
			// logger.setUseParentHandlers(false);
			FileHandler fh = new FileHandler("logs/" + warName + "/" + warName
					+ ".log");
			fh.setFormatter(new WarFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the specific war's menu.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return An integer containing the selection.
	 */
	private int showWarMenu(IOHandler io, String warName) {
		return io.getChoice("\nHere comes the menu for " + warName + " !",
				new String[] { "Add missile", "Add launcher", "Add Iron-Dome",
				"Add artillery", "Add target to destroy",
				"Show inventory",
				isActive ? "End war and show stats" : "Start war" });
	}

	/**
	 * Initiates the war starting all cached threads.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 */
	private void start(IOHandler io) {
		for (IronDome d : domes)
			d.start();
		for (Launcher l : launchers)
			l.start();
		for (Artillery a : artillery)
			a.start();
		io.showMessege(this.name + " is active!\n");
	}

	/**
	 * Terminates all the ongoing threads (besides missiles). In other words -
	 * stops launching and intercepting.
	 */
	private void end() {
		for (IronDome d : domes)
			d.Stop();
		for (Launcher l : launchers)
			l.Stop();
		for (Artillery a : artillery)
			a.Stop();

	}

	/**
	 * Invokes methods regarding user's choice.
	 * 
	 * @param choice
	 *            - The index of the chosen option.
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return boolean containing the user's willingness to keep the war
	 *         running.
	 */
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
					correctInput = addIronDome(io);
					break;
				}
				case 4: {
					correctInput = addArtillery(io);
					break;
				}
				case 5: {
					correctInput = addTarget(io);
					break;
				}
				case 6: {
					showInventory(io);
					return false;
				}
				case 7: {
					if (isActive) {
						end();
						showStatistics(io);
						return true;
					} else {
						if(launchers.size() == 0){
							io.showError("Please insert at least 1 launcher before starting the war");
							return false;
						}
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
		return false;
	}

	/**
	 * Shows current war object's inventory (Launchers and their missiles, Iron
	 * domes and artillery).
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 */
	private void showInventory(IOHandler io) {
		io.showMessege(name + " war inventory:");
		io.showMessege("Launchers:");
		for (Launcher l : launchers) {
			if (l.getLState() != Launcher.State.DESTROYED)
				io.showMessege(l
						+ (l.getMissiles().size() > 0 ? ": missiles"
								+ l.getMissiles().toString() : ""));
		}
		io.showMessege("Iron Domes:");
		for (IronDome id : domes) {
			io.showMessege(id
					+ (id.getTargets().size() > 0 ? ": targets"
							+ id.getTargets().toString() : ""));
		}
		io.showMessege("Artillery:");
		for (Artillery a : artillery) {
			io.showMessege(a
					+ (a.getTargets().size() > 0 ? ": targets"
							+ a.getTargets().toString() : ""));
		}
	}

	/**
	 * Prompts a user for adding a missile and receiving the input.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return boolean regarding the correctness of the input.
	 */
	private boolean addMissile(IOHandler io) {
		Launcher selectedLauncher = null;
		try {
			List<Launcher> availableLaunchers = new Vector<>();
			for(Launcher l : launchers){
				if(l.getLState() != Launcher.State.DESTROYED){
					availableLaunchers.add(l);
				}
			}
			String[] availableLaunchersStrings = new String[availableLaunchers.size()];
			for (int i = 0; i < availableLaunchers.size(); i++) {
				availableLaunchersStrings[i] = availableLaunchers.get(i).toString();
			}
			if (availableLaunchersStrings.length != 0) {
				int choice = io.getChoice("Enter launcher to add to:",
						availableLaunchersStrings);
				if (choice < 0 || choice > availableLaunchersStrings.length)
					throw new IllegalArgumentException(
							"Please choose from selected values");
				selectedLauncher = availableLaunchers.get(choice - 1);
				io.flushBuffers();
				String dest = io.getInput("Enter destination:");
				int flyTime = io.getInt("Enter fly time:");
				if (flyTime <= 0)
					throw new IllegalArgumentException(
							"Please enter positive values");
				int damage = io.getInt("Enter damage:");
				selectedLauncher.addMissile(new Missile(name, dest, 0, flyTime,
						damage));
			} else
				throw new NoLauncherAvailableException();
			return true;
		} catch (NoLauncherAvailableException e) {
			io.showError("No launchers available!\nPlease add one first");
			return true;
		} catch (IllegalArgumentException e) {
			io.showError("Illegal argument - try again");
			return false;
		}
	}

	/**
	 * Prompts a user for adding a launcher and receiving the input.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return boolean regarding the correctness of the input.
	 */
	private boolean addLauncher(IOHandler io) {
		io.flushBuffers();
		try {
			String id = io.getInput("Enter id:");
			Launcher.State[] states = { Launcher.State.ACTIVE,
					Launcher.State.HIDDEN };
			String[] statesStrings = new String[states.length];
			for (int i = 0; i < states.length; i++) {
				statesStrings[i] = states[i].toString();
			}
			int stateindex = io.getChoice("Choose state:", statesStrings);
			if (stateindex < 0 || stateindex > states.length)
				throw new IllegalArgumentException(
						"Please choose from selected values");
			Launcher l;
			launchers.add(l=new Launcher(name, "L" + id, states[stateindex - 1]));
			if(isActive)
				l.start();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Prompts a user for adding an Iron-Dome and receiving the input.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return boolean regarding the correctness of the input.
	 */
	private boolean addIronDome(IOHandler io) {
		io.flushBuffers();
		String id = io.getInput("Enter id:");
		IronDome i;
		domes.add(i=new IronDome(name, "D" + id));
		if(isActive)
			i.start();
		return true;
	}

	/**
	 * Prompts a user for adding an Artillery and receiving the input.
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 * @return boolean regarding the correctness of the input.
	 */
	private boolean addArtillery(IOHandler io) {
		io.flushBuffers();
		String id = io.getInput("Enter id:");
		Artillery a;
		Artillery.Type[] typeList = Artillery.Type.values();
		String[] typeListStrings = new String[typeList.length];
		int choice;
		for (int i = 0; i < typeList.length; i++) {
			typeListStrings[i] = typeList[i].toString();
		}
		choice = io.getChoice("Select type:", typeListStrings);
		try {
			a = new Artillery(name, id, typeList[choice - 1]);
		} catch (Exception e) {
			return false;
		}
		artillery.add(a);
		if(isActive)
			a.start();
		return true;
	}

	/**
	 * 
	 * @param io
	 * @return
	 */
	private boolean addTarget(IOHandler io) {
		int selection = io.getChoice("Add target to existing:", new String[] {
				"Iron-Dome", "Artillery" });
		int domesSize = domes.size();
		int artillerySize = artillery.size();
		int i = 0;
		List<Missile> availableMissiles = new Vector<>();
		String[] optionStrings;
		IronDome selectedDome;
		Missile selectedMissile;
		Artillery selectedArtillery;
		Launcher selectedLauncher;
		if (selection < 1 || selection > 2) {
			throw new IllegalArgumentException();
		} else {
			if (selection == 1) { // handles missile targets
				optionStrings = new String[domesSize];
				for (i = 0; i < optionStrings.length; i++) {
					optionStrings[i] = domes.get(i).toString();
				}
				selection = io.getChoice("Select Iron-Dome to add to:",
						optionStrings);
				selectedDome = domes.get(selection - 1);
				for (Launcher l : launchers) {
					for (Missile m : l.getMissiles()) {
						if (m.getMState() == Missile.State.FLYING)
							availableMissiles.add(m);
					}
				}
				optionStrings = new String[availableMissiles.size()];
				i = 0;
				if (availableMissiles.size() == 0) {
					io.showError("There are no missiles to target right now");
					return true;
				} else {
					for (Missile m : availableMissiles) {
						optionStrings[i++] = m.toString();
					}
					selection = io.getChoice(
							"Select missile to assign as target:",
							optionStrings);
					selectedMissile = availableMissiles.get(selection - 1);
					selectedDome.assignTarget(new Target(name, selectedMissile,
							0, selectedDome));
				}
			} else if (selection == 2) { // handles launcher targets
				optionStrings = new String[artillerySize];
				for (i = 0; i < optionStrings.length; i++) {
					optionStrings[i] = artillery.get(i).toString();
				}
				selection = io.getChoice("Select artillery to add target to:",
						optionStrings);
				selectedArtillery = artillery.get(selection - 1);
				optionStrings = new String[launchers.size()];
				i = 0;
				if (launchers.size() == 0) {
					io.showError("There are no launchers to target right now");
					return true;
				} else {
					List<Launcher> availableLaunchers = new Vector<>();
					for(Launcher l : launchers){
						if(l.getLState() != Launcher.State.DESTROYED){
							availableLaunchers.add(l);
						}
					}
					optionStrings = new String[availableLaunchers.size()];
					for (Launcher l : availableLaunchers) {
						optionStrings[i++] = l.toString();
					}
					selection = io.getChoice(
							"Select launcher to assign as target:",
							optionStrings);
					selectedLauncher = availableLaunchers.get(selection
							- 1);
					selectedArtillery.addTarget(new Target(name,
							selectedLauncher, 0, selectedArtillery));
				}
			}
		}
		io.showMessege("Target Was Added Successfully!");
		return true;
	}

	/**
	 * Shows the statistics for current war object
	 * 
	 * @param io
	 *            - Object that implements IOHandler.
	 */
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

		// TODO Can create problem simulating multiple wars.
		io.showMessege("Waiting for all threads to die for accurate results",
				"This may take a while");
		threads.removeIf(new Predicate<Thread>() {
			public boolean test(Thread t) {
				return t.toString().contains("system")
						|| t.toString().contains("main") || !t.isAlive();
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

		//Start of actual statistics
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
			totalDamage += launchers.get(i).getTotalDamage();
		}

		for (i = 0; i < artillerySize; i++) {
			if (artillery.get(i).getLaunchersIntercepted() > mostSuccessfulArtillery
					.getLaunchersIntercepted())
				mostSuccessfulArtillery = artillery.get(i);
		}

		io.showMessege(this.toString());
	}

	@Override
	public String toString() {
		try {
			String stats = name + " has caused " + totalDamage + " damage to Israel."
						   + WarFormatter.EOL + "Missiles fired: " + missilesFired
						   + WarFormatter.EOL + "Missiles intercepted: "
						   + missilesIntercepted + WarFormatter.EOL
						   + "Launchers intercepted: " + launchersIntercepted
						   + WarFormatter.EOL + "Most Successful Iron Dome: "
						   + mostSuccessfulIronDome + WarFormatter.EOL
						   + "Missiles intercepted: "
						   + mostSuccessfulIronDome.getMissilesIntercepted()
						   + WarFormatter.EOL + "Most Successful Artillery: "
						   + mostSuccessfulArtillery + WarFormatter.EOL
						   + "Launchers destroyed: "
						   + mostSuccessfulArtillery.getLaunchersIntercepted()
						   + WarFormatter.EOL + "Most Successful Launcher: "
						   + mostSuccessfulLauncher + WarFormatter.EOL
						   + "Missiles fired: "
						   + mostSuccessfulLauncher.getMissilesFired()
						   + WarFormatter.EOL + "Damage done: "
						   + mostSuccessfulLauncher.getTotalDamage();
			logStats(stats);
			return stats;
		} catch (NullPointerException e) {
			String stats = name + " has caused " + totalDamage + " damage to Israel."
						   + WarFormatter.EOL + "Missiles fired: " + missilesFired
						   + WarFormatter.EOL + "Missiles intercepted: "
						   + missilesIntercepted + WarFormatter.EOL
						   + "Launchers intercepted: " + launchersIntercepted;
			logStats(stats);
			return stats;
		}
	}
	private void logStats(String stats){
		logger.log(Level.INFO, "War "+ name + " has ended"+ WarFormatter.EOL + "\nWar Statistics:" + WarFormatter.EOL + stats, this);
	}
}
