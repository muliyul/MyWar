package war.IO;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GUI_IO implements IOHandler{
    private JFrame progressBar;
    
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

    @Override
    public void showProgressBar(double percent) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		if(progressBar==null)
		    progressBar=getProgressBar(percent);
		else
		    updateProgressBar(percent);
	    }
	});
	
    }

    protected void updateProgressBar(double percent) {
	progressBar.setTitle(percent+"%");
	progressBar.paintAll(progressBar.getGraphics());
	if(percent>=89)
	    progressBar.dispose();
    }

    private JFrame getProgressBar(double percent) {
	JFrame frame=new JFrame(percent+"%");
	frame.setSize(400, 200);
	frame.setLocationRelativeTo(null);
	frame.setLayout(new BorderLayout());
	JPanel mainPanel=new JPanel(){
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawRect(15, 15, getWidth()-30, getHeight()-30);
	        g.fillRect(15, 15, (int)((getWidth()-30)*percent), getHeight()-30);
	    }
	};
	frame.add(mainPanel,BorderLayout.CENTER);
	frame.setVisible(true);
	frame.toFront();
	return frame;
    }

}
