package Utils;
import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import Core.Artillery;
import Core.IronDome;
import Core.Launcher;
import Core.War;

public class XMLParser {

    public static War parseWar(String filePath,String name) {
	List<IronDome> domes = null;
	List<Launcher> launchers = null;
	List<Artillery> artillery = null;
	try {
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(new File(filePath));
	    domes=parseDomes(doc.getElementsByTagName("IronDome"));
	    launchers=parseLaunchers(doc.getElementsByTagName("Launcher"));
	    artillery=parseArtillery(doc.getElementsByTagName("Destructor"));
	} catch (Exception e) {
	}
	
	return new War(name, domes, launchers, artillery);
    }

    private static List<Artillery> parseArtillery(NodeList elementsByTagName) {
	return null;
    }

    private static List<IronDome> parseDomes(NodeList domelist) {
	
	return null;
    }
    
    private static List<Launcher> parseLaunchers(NodeList launcherslist) {

	return null;
    }
    
    

}
