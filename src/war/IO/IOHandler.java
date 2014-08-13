package war.IO;

public interface IOHandler {
    public void showMessege(String s);
    public void showMessege(String... strings);
    public String getInput(String s);
    public int getInt(String s);
    public int getChoice(String... strings);
    public void showMessege(String head, String[] options);
    public int getChoice(String head, String[] options);
    public boolean yesNo(String string);
    public void flushBuffers();
    public void showProgressBar(double percent);
}
