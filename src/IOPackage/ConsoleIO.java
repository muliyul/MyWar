package IOPackage;
import java.io.PrintStream;
import java.util.Scanner;


public class ConsoleIO implements IOHandler {
    private Scanner in=new Scanner(System.in);
    private PrintStream out=System.out;

    @Override
    public void showMessege(String s) {
	out.print(s);
    }

    @Override
    public String getInput(String s) {
	out.println(s);
	return in.next();
    }

    @Override
    public int getInt(String s) {
	out.println(s);
	return in.nextInt();
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
