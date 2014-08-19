package war.io;

/**
 * Classes implement this to handle Input/Output information more easily.
 * @author Muli
 *
 */
public interface IOHandler {
    /**
     * Shows a message to the user.
     * @param s - String to display.
     */
    public void showMessege(String s);

    /**
     * Shows a message to the user.
     * @param strings - Array of strings to display
     */
    public void showMessege(String... strings);

    /**
     * Shows a message to the user and expects an input.
     * @param s - String to display.
     * @return String containing the input.
     */
    public String getInput(String s);

    /**
     * Shows a messege to the user and expects an Integer input.
     * @param s - String to display.
     * @return Integer from the input.
     */
    public int getInt(String s);

    /**
     * Shows a menu.
     * @param head - The head of the menu.
     * @param options - Array of strings containing options.
     */
    public void showMessege(String head, String[] options);
    /**
     * Shows an error message to the user.
     * @param s - Error message to display.
     */
    public void showError(String s);

    /**
     * Shows a menu.
     * @param head - The head of the menu.
     * @param options - Array of strings containing options.
     * @return Integer equal to selected option index.
     */
    public int getChoice(String head, String[] options);

    /**
     * Shows a Yes/No message.
     * @param s - The question in string format. 
     * @return boolean representing the answer - true for "Yes", false for "No".
     */
    public boolean yesNo(String s);
    
    /**
     * Some Input/Output systems require this.
     */
    public void flushBuffers();
    
    /**
     * Display progress bar.
     * @param percent
     */
    public void showProgressBar(double percent);
}
