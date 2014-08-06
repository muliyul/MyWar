package IOPackage;

public interface IOHandler {
    public void showMessege(String s);
    public void showMessege(String ... strings);
    public String getInput(String s);
    public int getInt(String s);
    public int getChoice(String ... strings);
}
