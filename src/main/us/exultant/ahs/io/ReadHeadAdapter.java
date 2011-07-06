package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.util.*;

/**
 * Provides a ReadHead backed by a buffer which is only writable by subclasses and exposes
 * a Pump and option to designate ExceptionHandler. (It is highly similar to a Teamster,
 * except it also allows the overriding of some special functions needed by the relatively
 * "low-level" code that operates directly with java nio.)
 * 
 * @author hash
 * 
 * @param <$T>
 */
public abstract class ReadHeadAdapter<$T> implements ReadHead<$T> {
	protected ReadHeadAdapter() {
		$pipe = new Pipe<$T>();
		$eh = null;
	}
	
	protected final Pipe<$T>		$pipe;
	private ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	public void setListener(Listener<ReadHead<$T>> $el) {
		$pipe.SRC.setListener($el);
	}
	
	public $T read() {
		return $pipe.SRC.read();
	}
	
	public $T readNow() {
		return $pipe.SRC.readNow();
	}
	
	public boolean hasNext() {
		return $pipe.SRC.hasNext();
	}
	
	public List<$T> readAll() {
		return $pipe.SRC.readAll();
	}
	
	public List<$T> readAllNow() {
		return $pipe.SRC.readAllNow();
	}
	
	public boolean isClosed() {
		return $pipe.SRC.isClosed();
	}
	
	public abstract void close() throws IOException;	// is abstract because you probably want to close any underlying channels, then have the pump close the pipe close itself when appropriate.
	
	protected void handleException(IOException $e) {
		ExceptionHandler<IOException> $dated_eh = $eh;
		if ($dated_eh != null) $dated_eh.hear($e);		
	}
}
