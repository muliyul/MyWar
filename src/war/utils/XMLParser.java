package war.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import war.core.Target;
import war.core.War;
import war.core.enemy.Launcher;
import war.core.enemy.Missile;
import war.core.friendly.Artillery;
import war.core.friendly.IronDome;
import war.io.IOHandler;

public class XMLParser {

	/**
	 * Parses an entire war from XML configuration file.
	 * 
	 * @param name
	 *            - The war's name.
	 * @param filePath
	 *            - The XML's full file path.
	 * @param io
	 * @return
	 */
	public static War parseWar(String name, String filePath, IOHandler io) {
		List<IronDome> domes = null;
		List<Launcher> launchers = null;
		List<Artillery> artillery = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(filePath));
			launchers = parseLaunchers(name,
					doc.getElementsByTagName("launcher"));
			domes = parseDomes(name,
					doc.getElementsByTagName("missileDestructors"), launchers);
			artillery = parseArtillery(name,
					doc.getElementsByTagName("missileLauncherDestructors"),
					launchers);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			System.out.println("Can't parse " + filePath + "!");
			return null;
		}

		return new War(name, domes, launchers, artillery, io);
	}

	/**
	 * Parses all the launchers in the given XML nodelist.
	 * 
	 * @param warName
	 *            - The war's name.
	 * @param launcherslist
	 *            - Nodelist containing "launcher" tags.
	 * @return List of launchers.
	 */
	private static List<Launcher> parseLaunchers(String warName,
			NodeList launcherslist) {
		Vector<Launcher> v = new Vector<>();
		int numberOfLaunchers = launcherslist.getLength();
		for (int i = 0; i < numberOfLaunchers; i++) {
			Node launcher = launcherslist.item(i);
			Element l = (Element) launcher;
			String lid = l.getAttribute("id");
			boolean isHidden = (l.getAttribute("isHidden").toLowerCase()
					.equals("true")) ? true : false;
			Launcher ml = new Launcher(warName, lid,
					isHidden ? Launcher.State.HIDDEN : Launcher.State.ACTIVE);
			NodeList missilelist = l.getElementsByTagName("missile");
			ml.setMissiles(readMissiles(warName, missilelist, ml.getLaunchpad()));
			v.add(ml);
		}
		return v;
	}

	/**
	 * Parses the missiles in the given XML nodelist.
	 * 
	 * @param warName
	 *            - The war's name.
	 * @param missilelist
	 *            - Nodelist containing "missile" tags.
	 * @param launchpad
	 *            - The launcher's launchpad associated with all the missiles.
	 * @return List of missiles.
	 */
	private static List<Missile> readMissiles(String warName,
			NodeList missilelist, Semaphore launchpad) {
		Vector<Missile> v = new Vector<>();
		int numberOfMissiles = missilelist.getLength();
		for (int j = 0; j < numberOfMissiles; j++) {
			Element m = (Element) missilelist.item(j);
			String id = m.getAttribute("id");
			String destination = m.getAttribute("destination");
			int launchTime = Integer.valueOf(m.getAttribute("launchTime"));
			int flyTime = Integer.valueOf(m.getAttribute("flyTime"));
			int damage = Integer.valueOf(m.getAttribute("damage"));
			Missile mis;
			v.add(mis = new Missile(warName, id, destination, launchTime,
					flyTime, damage));
			mis.setLaunchpad(launchpad);
		}
		return v;
	}

	/**
	 * Parses all the Iron-Domes in the given XML nodelist.
	 * 
	 * @param warName
	 *            - The war's name.
	 * @param domelist
	 *            - Nodelist containing "missileDestructor" tags.
	 * @param launchers
	 *            - The launchers, to assign missile targets.
	 * @return List of Iron-Domes.
	 */
	private static List<IronDome> parseDomes(String warName, NodeList domelist,
			List<Launcher> launchers) {
		Vector<IronDome> v = new Vector<>();
		String targetId = null;
		int destructTime = 0;
		Node mdList = domelist.item(0);
		NodeList ironDomeList = ((Element) mdList)
				.getElementsByTagName("destructor");
		int numberOfIronDomes = ironDomeList.getLength();
		for (int i = 0; i < numberOfIronDomes; i++) {
			Node iron = ironDomeList.item(i);
			Element irond = (Element) iron;
			String id = irond.getAttribute("id");
			IronDome ironD = new IronDome(warName, id);
			NodeList targetedMissile = irond
					.getElementsByTagName("destructdMissile");
			int numberOfTargets = targetedMissile.getLength();
			for (int j = 0; j < numberOfTargets; j++) {
				Node target = targetedMissile.item(j);
				Element targetM = (Element) target;
				targetId = targetM.getAttribute("id");
				destructTime = Integer.valueOf(targetM
						.getAttribute("destructAfterLaunch"));
				Missile m = findMissile(targetId, launchers);
				ironD.assignTarget(new Target(warName, m, destructTime, ironD));
			}
			v.add(ironD);

		}
		return v;
	}

	/**
	 * Finds a missile's reference by matching id's.
	 * 
	 * @param id
	 *            - The id of the missile to look up.
	 * @param launchers
	 *            - List of launchers containing missiles.
	 * @return The missile, if found. Null otherwise.
	 */
	private static Missile findMissile(String id, List<Launcher> launchers) {
		for (Launcher l : launchers) {
			List<Missile> missiles = l.getMissiles();
			for (Missile m : missiles) {
				if (m.toString().toLowerCase().equals(id.toLowerCase())) {
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * Parses all the Artillery in the given XML nodelist.
	 * 
	 * @param warName
	 *            - The war's name.
	 * @param artillerylist
	 *            - Nodelist containing "missileLauncherDestructor" tags.
	 * @param launchers
	 *            - The launchers, to assign launcher targets.
	 * @return List of Iron-Domes.
	 */
	private static List<Artillery> parseArtillery(String warName,
			NodeList artillerylist, List<Launcher> launchers) {
		Vector<Artillery> artillery = new Vector<>();
		Node ldlist = artillerylist.item(0);
		NodeList launcherDestructors = ((Element) ldlist)
				.getElementsByTagName("destructor");
		int numberOfLauncherDestructor = launcherDestructors.getLength();
		String targetId;
		int destructTime;
		for (int i = 0; i < numberOfLauncherDestructor; i++) {
			Node ld = launcherDestructors.item(i);
			Element eld = (Element) ld;
			String type = eld.getAttribute("type");
			Artillery destructor = new Artillery(warName,
					Artillery.Type.valueOf(type.toUpperCase()));
			NodeList targetedLaunchers = eld
					.getElementsByTagName("destructedLanucher");
			int numberOfTargets = targetedLaunchers.getLength();
			for (int j = 0; j < numberOfTargets; j++) {
				Node target = targetedLaunchers.item(j);
				Element targetL = (Element) target;
				targetId = targetL.getAttribute("id");
				destructTime = Integer.valueOf(targetL
						.getAttribute("destructTime"));
				destructor.addTarget(new Target(warName, findLauncher(targetId,
						launchers), destructTime, destructor));
			}

			artillery.add(destructor);
		}

		return artillery;
	}

	/**
	 * Finds a launcher's reference by matching id's.
	 * 
	 * @param id
	 *            - The id of the missile to look up.
	 * @param launchers
	 *            - List of launchers.
	 * @return The launcher, if found. Null otherwise.
	 */
	private static Launcher findLauncher(String targetId,
			List<Launcher> launchers) {
		for (Launcher l : launchers) {
			if (l.toString().toLowerCase().equals(targetId.toLowerCase()))
				return l;
		}
		return null;
	}

}
