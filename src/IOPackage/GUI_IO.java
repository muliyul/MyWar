package IOPackage;
import javax.swing.JOptionPane;


public class GUI_IO implements IOHandler{

    @Override
    public void showMessege(String s) {
	JOptionPane.showMessageDialog(null, s);
    }

    @Override
    public String getInput(String s) {
	return JOptionPane.showInputDialog(null,s);
    }

    @Override
    public int getInt(String s) {
	return Integer.parseInt(JOptionPane.showInputDialog(null,s));
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

    @Override
    public void showMessege(String head, String[] options) {
	String raw="";
	raw+=head;
	for(int i=1;i<options.length;i++)
	    raw+=(i+1)+") "+options[i]+'\n';
	showMessege(raw);
    }

    @Override
    public int getChoice(String head, String[] options) {
	String raw="";
	raw+=head;
	for(int i=1;i<options.length;i++)
	    raw+=(i+1)+") "+options[i]+'\n';
	return Integer.parseInt(JOptionPane.showInputDialog(null, raw)); 
    }

    @Override
    public boolean yesNo(String s) {
	return JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(
		null, s, "Confirm dialog", JOptionPane.YES_NO_OPTION);
    }

    @Override
    public void flushBuffers() {
    }

}
