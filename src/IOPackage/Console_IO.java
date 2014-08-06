package IOPackage;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class Console_IO implements IOHandler {
    private Scanner in=new Scanner(System.in);
    private PrintStream out=System.out;

    @Override
    public void showMessege(String s) {
	out.print(s);
    }

    @Override
    public String getInput(String s) {
	out.println(s);
	out.flush();
	return in.nextLine();
    }

    @Override
    public int getInt(String s) {
	out.println(s);
	return in.nextInt();
    }

    @Override
    public void showMessege(String... strings) {
	String raw="";
	for(int i=0;i<strings.length;i++)
	    raw+=strings[i]+'\n';
	out.println(raw);
    }

    @Override
    public int getChoice(String... strings) {
	showMessege(strings);
	return in.nextInt();
    }

    @Override
    public void showMessege(String head, String[] options) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public int getChoice(String head, String[] options) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean yesNo(String string) {
	// TODO Auto-generated method stub
	return false;
    }


}
