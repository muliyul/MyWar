package war.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import war.core.Artillery;
import war.core.IronDome;
import war.core.Launcher;
import war.core.Missile;
import war.core.Target;
import war.core.War;

public class XMLParser {

	public static War parseWar(String name, String filePath) {
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

		return new War(name, domes, launchers, artillery);
	}

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
			Launcher ml = new Launcher(warName,
					isHidden ? Launcher.State.HIDDEN : Launcher.State.ACTIVE);
			NodeList missilelist = l.getElementsByTagName("missile");
			ml.setMissiles(readMissiles(warName,missilelist));
			v.add(ml);
		}
		return v;
	}

	private static List<Missile> readMissiles(String warName, NodeList missilelist) {
		Vector<Missile> v = new Vector<>();
		int numberOfMissiles = missilelist.getLength();
		for (int j = 0; j < numberOfMissiles; j++) {
			Element m = (Element) missilelist.item(j);
			String id = m.getAttribute("id");
			String destination = m.getAttribute("destination");
			int launchTime = Integer.valueOf(m.getAttribute("launchTime"));
			int flyTime = Integer.valueOf(m.getAttribute("flyTime"));
			int damage = Integer.valueOf(m.getAttribute("damage"));
			v.add(new Missile(warName, id, destination, launchTime, flyTime, damage));
		}
		return v;
	}

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
			IronDome ironD = new IronDome(warName,id);
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
				ironD.assignTarget(new Target(m,destructTime, ironD));
			}
			v.add(ironD);

		}
		return v;
	}

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
				destructor.addTarget(new Target(findLauncher(targetId,
						launchers), destructTime, destructor));
			}

			artillery.add(destructor);
		}

		return artillery;
	}

	private static Launcher findLauncher(String targetId,
			List<Launcher> launchers) {
		for (Launcher l : launchers) {
			if (l.toString().toLowerCase().equals(targetId.toLowerCase()))
				return l;
		}
		return null;
	}

}
