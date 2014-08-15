package war.io;

import java.io.PrintStream;
import java.util.Scanner;

import war.utils.WarFormatter;

public class Console_IO implements IOHandler {
    private Scanner in = new Scanner(System.in);
    private PrintStream out = System.out;

    @Override
    public void showMessege(String s) {
	out.println(s);
    }

    @Override
    public String getInput(String s) {
	out.println(s);
	return in.nextLine();
    }

    @Override
    public int getInt(String s) {
	out.println(s);
	return in.nextInt();
    }

    @Override
    public void showMessege(String... strings) {
	String raw = "";
	for (int i = 0; i < strings.length; i++)
	    raw += strings[i] + '\n';
	out.println(raw);
    }

    @Override
    public int getChoice(String... strings) {
	showMessege(strings);
	return in.nextInt();
    }

    @Override
    public void showMessege(String head, String[] options) {
	String raw = "" + head + '\n';
	for (int i = 0; i < options.length; i++)
	    raw += (i + 1) + ") " + options[i] + '\n';
	out.println(raw);
    }

    @Override
    public int getChoice(String head, String[] options) {
	String raw = "" + head + WarFormatter.EOL;
	for (int i = 0; i < options.length; i++)
	    raw += (i + 1) + ") " + options[i] + '\n';
	out.println(raw);
	return in.nextInt();
    }

    @Override
    public boolean yesNo(String s) {
	return getChoice(s, new String[] { "Yes", "No" }) == 1;
    }

    @Override
    public void flushBuffers() {
	out.flush();
	in.nextLine();
    }

    @Override
    public void showProgressBar(double percent) {
	showMessege(percent + "%");
    }

    @Override
    public void showError(String s) {
	System.err.println(s);
    }

}
