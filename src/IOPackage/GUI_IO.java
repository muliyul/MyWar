package IOPackage;
import javax.swing.JOptionPane;


public class GUI_IO implements IOHandler{

    @Override
    public void showMessege(String s) {
	JOptionPane.showMessageDialog(null, s);
    }

    @Override
    public String getInput(String s) {
	return JOptionPane.showInputDialog(s);
    }

    @Override
    public int getInt(String s) {
	return Integer.parseInt(JOptionPane.showInputDialog(s));
    }

    @Override
    public void showMessege(String... strings) {
	String raw="";
	for(int i=0;i<strings.length;i++)
	    raw+=strings[i]+'\n';
	JOptionPane.showMessageDialog(null, raw);
    }

    @Override
    public int getChoice(String... strings) {
	String raw="";
	for(int i=0;i<strings.length;i++)
	    raw+=strings[i]+'\n';
	return Integer.parseInt(JOptionPane.showInputDialog(null, raw));
    }
    
}
