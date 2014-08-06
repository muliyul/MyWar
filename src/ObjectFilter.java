import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ObjectFilter implements Filter {
	private Object o;

	public ObjectFilter(Object o) {
		this.o = o;
	}

	@Override
	public boolean isLoggable(LogRecord rec) {
		if (rec.getParameters() != null) {
			for(Object obj: rec.getParameters()){
				if(o==obj){
					return true;
				}
			}
		} 
		return false;
	}

}
