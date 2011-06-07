package us.exultant.ahs.io;

import us.exultant.ahs.util.*;
import us.exultant.ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * This abstraction allows one to make a whole ReadHead simply by specifying some details
 * of the pump body and the close method; however, it's nowhere near as pleasant to use as
 * simply interfacing with ReadHeadAdapter via TranslatorStack when it comes to creating
 * protocol stacks, and for that reason its use is not recommended (in particular,
 * creating one implementor of this class does not allow one to recycle code should one
 * need to create another protocol layer unless one is comfortable with needless usage of
 * multiple buffers and resulting convolutions of thread management).
 * 
 * @author hash
 * 
 */
@Deprecated()
public abstract class ReadHeadAdapterSimple<$T> implements ReadHead<$T> {
	protected ReadHeadAdapterSimple() {
		this.$pipe = new Pipe<$T>();
		this.$pump = new PumpT();	
	}
	
	/**
	 * This method is called once per cycle of the pump. If possible, it should read
	 * and construct a chunk from the underlying stream/channel/whatever and return
	 * it. A null return does NOT necessarily indicate EOF, since subclasses based on
	 * non-blocking IO schemes might be pumped and yet not have enough data available
	 * to formulate a whole chunk.
	 */
	protected abstract $T getChunk() throws IOException;
	
	/**
	 * The subclass should call this method when it encounters a situation that makes
	 * it want to stop reading (such as EOF or an exception (which should also have
	 * been thrown from the getChunk method)) while in the middle of executing the
	 * getChunk() method from a pumping thread. This adapter will then change its
	 * state to reflect this and halt further pumping.
	 */
	protected void baseEof() {
		ourClose();	// it is in fact ok to flag ourselves as closed here, since regardless of how we got here the underlying stream is already not willing to give us more data
	}
	
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
	private void ourClose() {
		try {
			close();	// this is likely redundant, but can't hurt.
		} catch (IOException $e) {
			/* what could we possibly do with this? and i don't want to break out of the rest of this function. */
			$e.printStackTrace();
		}
		
		$pipe.SRC.close();	// this transparently handles interruption of any still-blocking reads as well as return of the final readAll.
		
		// give our listener a chance to notice our closure.  (pipe doesn't know our listener.)  (our isClosed method refers to pipe, which already considers itself completely closed.)
		Listener<ReadHead<$T>> $dated_el = $el;
		if ($dated_el != null) $dated_el.hear(this);
	}
	
	
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				$T $chunk = null;
				try {
					$chunk = getChunk();
				} catch (IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					
					// i guess it's somewhat debatable whether or not any exception should close... but that's what InputStream does, so i'm sticking to it for the time
					baseEof();
					break;
				}
				
				
				// if we have no chunk and we weren't hit by an IOException, then it's either
				//    - just a non-blocking dude who doesn't have enough bytes for a semantic chunk, or
				//    - a blocking dude who is at EOF and should have already signalled as much.
				if ($chunk == null) {
					// we're not necessarily done with this channel, but we don't want to spin on it any more right now.
					break;
				}
				
				// we have a chunk; wrap it up and enqueue to the buffer
				// readers will immediately Notice the new data due to the pipe's internal semaphore doing its job
				$pipe.SINK.write($chunk);
				
				// signal that we got a new chunk in
				Listener<ReadHead<$T>> $dated_el = $el;
				if ($dated_el != null) $dated_el.hear(ReadHeadAdapterSimple.this);
			}
		}
	}
}
