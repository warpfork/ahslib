package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * This abstraction allows one to make a whole ReadHead simply by specifying some details
 * of the pump body and the close method.
 * 
 * @author hash
 * 
 */
public abstract class ReadHeadAdapter<$T> implements ReadHead<$T> {
	protected ReadHeadAdapter() {
		this.$pipe = new Pipe<$T>();
		this.$pump = new PumpT();	
	}
	
	/**
	 * This method is called once per cycle of the pump. It should read and construct
	 * a chunk from the underlying stream/channel/whatever and return it. A null
	 * return indicates EOF.
	 */
	protected abstract $T getChunk() throws IOException;
	
	private final PumpT			$pump;
	private final Pipe<$T>			$pipe;
	private Listener<ReadHead<$T>>		$el;
	private ExceptionHandler<IOException>	$eh;
	
	public Pump getPump() {
		return $pump;
	}

	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}

	public void setListener(Listener<ReadHead<$T>> $el) {
		this.$el = $el;
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
	
	/**
	 * this function is for updating our state at this level to fully closed (i.e.,
	 * after the event has propagated through the underlying stream and buffering, and
	 * we will never add data to the readable buffer).
	 */
	private void ourClose() throws IOException {
		close();	// this is likely redundant, but can't hurt.
		
		try {
			$pipe.SRC.close();	// this transparently handles interruption of any still-blocking reads as well as return of the final readAll.
		} catch (IOException $e) {
			/* this can't actually happen in a pipe */
		}
		
		// give our listener a chance to notice our closure.  (pipe doesn't know our listener.)  (our isClosed method refers to pipe, which already considers itself completely closed.)
		Listener<ReadHead<$T>> $dated_el = $el;
		if ($dated_el != null) $dated_el.hear(this);
	}
	
	
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			ExceptionHandler<IOException> $dated_eh = null;
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;

				$T $chunk = null;
				try {
					$chunk = getChunk();
				} catch (IOException $e) {
					$dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					// i guess it's somewhat debatable whether or not any exception should close... but that's what InputStream does, so i'm sticking to it for the time 
				}
				
				if ($chunk == null) {
					try {
						ourClose();	// it is in fact ok to flag ourselves as closed here, since regardless of how we got here the underlying stream is already not willing to give us more data
					} catch (IOException $e) {
						if ($dated_eh != null) $dated_eh = $eh;		// if we didn't get it earlier in this cycle, get it now; if we did, keep that one
						if ($dated_eh != null) $dated_eh.hear($e);	// now tell 'em
					}
				} else {
					// wrap it up and enqueue to the buffer
					// readers will immediately Notice the new data due to the pipe's internal semaphore doing its job
					try {
						$pipe.SINK.write($chunk);
					} catch (IOException $e) {
						/* this can't actually happen in a pipe */
					}
					
					// signal that we got a new chunk in
					Listener<ReadHead<$T>> $dated_el = $el;
					if ($dated_el != null) $dated_el.hear(ReadHeadAdapter.this);
				}
			}
		}
	}
	
}
