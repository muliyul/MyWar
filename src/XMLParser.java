import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLParser {

    public static War parseWar(String filePath,String name) {
	List<IronDome> domes = null;
	List<Launcher> launchers = null;
	try {
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(new File(filePath));
	    domes=parseDomes(doc.getElementsByTagName("IronDome"));
	    launchers=parseLaunchers(doc.getElementsByTagName("Launcher"));
	} catch (Exception e) {
	}
	
	return new War(name, domes, launchers);
    }

    private static List<IronDome> parseDomes(NodeList domelist) {
	
	return null;
    }
    
    private static List<Launcher> parseLaunchers(NodeList launcherslist) {

	return null;
    }
    
    

}
