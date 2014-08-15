package war.utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class WarFormatter extends Formatter {
    public static String EOL=System.getProperty("line.separator");
    @Override
    public String format(LogRecord record) {
	StringBuilder sb=new StringBuilder(1000);
	sb.append("D: " + LocalDate.now().getDayOfMonth() + "/"
		+ LocalDate.now().getMonthValue() + "/"
		+ LocalDate.now().getYear() + " T: "
		+ LocalDateTime.now().getHour() + ":"
		+ LocalDateTime.now().getMinute() + ":"
		+ LocalDateTime.now().getSecond() + EOL
		+ record.getMessage() + EOL + EOL);
	return sb.toString();
    }

}
