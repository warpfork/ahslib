package us.exultant.ahs.util.date;

import java.text.*;

public class IstdDateFormat extends SimpleDateFormat {
	public IstdDateFormat() {
		super("EEE, dd MMM yyyy HH:mm:ss zzz");
	}
	
	public static final IstdDateFormat INSTANCE = new IstdDateFormat();
}
