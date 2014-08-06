import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class WarFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
	return "D: " + LocalDate.now().getDayOfMonth() + "/"
		+ LocalDate.now().getMonthValue() + "/"
		+ LocalDate.now().getYear() + " T: "
		+ LocalDateTime.now().getHour() + ":"
		+ LocalDateTime.now().getMinute() + ":"
		+ LocalDateTime.now().getSecond() + "\r\n"
		+ record.getMessage() + "\r\n\r\n";
    }

}
