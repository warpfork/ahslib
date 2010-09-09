package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.util.*;

public class ReadHeadTranslator<$FROM, $TO> implements ReadHead<$TO> {
	public ReadHeadTranslator(ReadHead<$FROM> $bottom, Translator<$FROM,$TO> $middle) {
		
	}
	
	private ReadHead<$FROM>			$bottom;
	private Translator<$FROM,$TO>		$middle;
	private ExceptionHandler<IOException>	$eh;
	
	public Pump getPump() {
		return null;
	}
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		//TODO shit.  read the contract of this function again.  exceptions are supposed to be reported from the PUMP THREAD.
		// i HAVE been doing it wrong.  the buffer needs to be at the top layer and all of the readhead implementations i've made so far need to be made more piecewise (and probably just made by cute factory methods in some handy class).
	}
	
	public void setListener(Listener<ReadHead<$TO>> $el) {
		//TODO
	}
	
	public $TO read() {
		//TODO
		return null;
	}
	
	public $TO readNow() {
		//TODO
		return null;
	}
	
	public boolean hasNext() {
		//TODO
		return false;
	}
	
	public List<$TO> readAll() {
		//TODO
		return null;
	}
	
	public List<$TO> readAllNow() {
		//TODO
		return null;
	}
	
	public boolean isClosed() {
		return $bottom.isClosed();
	}
	
	public void close() throws IOException {
		$bottom.close();
	}
}
