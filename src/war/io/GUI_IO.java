package war.io;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import war.utils.WarFormatter;

public class GUI_IO implements IOHandler {
    private ProgressBarWindow pbw;

    @Override
    public void showMessege(String s) {
	JOptionPane.showMessageDialog(null, s);
    }

    @Override
    public void showMessege(String... strings) {
	StringBuilder sb = new StringBuilder(1000);
	for (String s : strings) {
	    sb.append(s + WarFormatter.EOL);
	}
	showMessege(sb.toString());
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
    public void showMessege(String head, String[] options) {
	StringBuilder sb = new StringBuilder(1000);
	int i = 1;
	sb.append(head + WarFormatter.EOL);
	for (String s : options) {
	    sb.append((i++) + ") " + s + WarFormatter.EOL);
	}
	showMessege(sb.toString());
    }

    @Override
    public void showError(String s) {
	JOptionPane w = new JOptionPane(s, JOptionPane.ERROR_MESSAGE);
	w.setVisible(true);
    }

    @Override
    public int getChoice(String head, String[] options)
	    throws NumberFormatException {
	StringBuilder sb = new StringBuilder(1000);
	int i = 1;
	sb.append(head + WarFormatter.EOL);
	for (String s : options) {
	    sb.append((i++) + ") " + s + WarFormatter.EOL);
	}
	try {
	    return Integer.parseInt(getInput(sb.toString()));
	} catch (NumberFormatException e) {
	    return 0;
	}
    }

    @Override
    public boolean yesNo(String s) {
	if (JOptionPane.showConfirmDialog(null, s, "Awaiting input",
		JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	    return true;
	return false;
    }

    @Override
    public void flushBuffers() {
    }

    @Override
    public void showProgressBar(double percent) {
	if (pbw == null) {
	    pbw = new ProgressBarWindow();
	} else {
	    if (percent <= 100){
		pbw.setValue(percent);
		pbw.setVisible(true);
	    }
	    else
		pbw.setVisible(false);
	}
    }

    static class ProgressBarWindow extends JFrame {
	private static ProgressBarWindow pbw;
	private JProgressBar pb;

	private ProgressBarWindow() {
	    super("Waiting for threads");
	    pb = new JProgressBar();
	    add(pb);
	    setLocationRelativeTo(null);
	    setSize(450, 100);
	    setVisible(true);
	}

	public void setValue(double percent) {
	    pb.setValue((int) percent);
	}
    }
}
