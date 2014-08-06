package IOPackage;
import javax.swing.JOptionPane;


public class GUI_UI implements IOHandler{

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
	// TODO Auto-generated method stub
	
    }

    @Override
    public int getChoice(String... strings) {
	// TODO Auto-generated method stub
	return 0;
    }
    
}
