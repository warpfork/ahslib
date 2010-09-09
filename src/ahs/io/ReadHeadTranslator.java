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
		//TODO
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
