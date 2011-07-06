package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.util.*;

public abstract class WriteHeadAdapter<$T> implements WriteHead<$T> {
	protected WriteHeadAdapter() {
		$pipe = new Pipe<$T>();
		$eh = null;
	}
	
	protected final Pipe<$T>		$pipe;
	private ExceptionHandler<IOException>	$eh;
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}
	
	public void write($T $chunk) throws IOException {
		$pipe.SINK.write($chunk);
	}
	
	public void writeAll(Collection<? extends $T> $chunks) throws IOException {
		$pipe.SINK.writeAll($chunks);
	}
	
	public boolean hasRoom() {
		return $pipe.SINK.hasRoom();
	}
	
	public boolean isClosed() {
		return $pipe.SINK.isClosed();
	}
	
	public abstract void close() throws IOException;	// is abstract because you probably want to close any underlying channels, then have the pump close the pipe close itself when appropriate.
	
	protected void handleException(IOException $e) {
		ExceptionHandler<IOException> $dated_eh = $eh;
		if ($dated_eh != null) $dated_eh.hear($e);		
	}
}
