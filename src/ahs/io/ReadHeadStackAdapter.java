package ahs.io;

import ahs.io.ReadHeadAdapter.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ReadHeadStackAdapter<$T> implements ReadHead<$T> {
	public ReadHeadStackAdapter(TranslatorStack<ReadableByteChannel,$T> $ts) {
		this.$pipe = new Pipe<$T>();
		this.$pump = new PumpT();
		this.$ts = $ts;
	}
	
	private final TranslatorStack<ReadableByteChannel,$T>	$ts;
	private final PumpT					$pump;
	private final Pipe<$T>					$pipe;
	private Listener<ReadHead<$T>>				$el;
	private ExceptionHandler<IOException>			$eh;
	
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
	 * we will never add data to the readable buffer). it will halt all future
	 * pumping.
	 */
	private void baseEof() {
		try {
			close();	// this is likely redundant, but can't hurt.
		} catch (IOException $e) {
			$e.printStackTrace();
		}
		
		$pipe.SRC.close();	// this transparently handles interruption of any still-blocking reads as well as return of the final readAll.
		
		// give our listener a chance to notice our closure.  (pipe doesn't know our listener.)  (our isClosed method refers to pipe, which already considers itself completely closed.)
		Listener<ReadHead<$T>> $dated_el = $el;
		if ($dated_el != null) $dated_el.hear(this);
	}
	
	
	//ATTN: when we rewrite this pump to service us now, it will act by going all the way to the BOTTOM of the stack,
	//  then handing things up to the translation stack,
	//  then taking what the translation stack returned and putting into the buffer pipe if it's not null.
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
					//ATTN: this is changing.  NOT all exceptions will cause this.  translation will not; ioe from the channel itself will.
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
				if ($dated_el != null) $dated_el.hear(ReadHeadStackAdapter.this);
			}
		}
	}
	
	// both the first entry in a TranslatorStack as well as the entire stack itself should be able to implement this, really.
	public static interface ChunkBuilder<$CHUNK> extends Translator<ReadableByteChannel,$CHUNK> {
		/**
		 * Read as much as currently possible from the ByteChannel. If pleased
		 * with the data obtained in this read (along with other data that may be
		 * buffered from preceeding reads), return a chunk to be passed up to the
		 * next layer of either translation or buffering; if not yet enough data
		 * to make a full chunk, return null; if weird data, throw exceptions.
		 * 
		 * @param $bc
		 *                a ReadableByteChannel in non-blocking mode.
		 * @return a chunk if possible; null otherwise.
		 * @throws TranslationException
		 *                 in case of data not conforming to protocol
		 * @throws EOFException
		 *                 in case of the ByteChannel becoming closed at a point
		 *                 not expected by the protocol
		 * @throws IOException
		 *                 in case of general errors from the ByteChannel
		 */
		public $CHUNK translate(ReadableByteChannel $bc) throws TranslationException;
		// okay, so the crux here is that errors from the underlying channel should (arguably, perhaps) be going somewhere else.
		// which meeans that the chunk builder itself should only really ever be offered data.
	}
	
	/**
	 * Hides all exceptions from the client, rerouting them elsewhere.
	 * 
	 * @author hash
	 *
	 */
	private static class Wrapp implements ReadableByteChannel {
		public Wrapp(ReadableByteChannel $bc, ExceptionHandler<IOException> $eh) {
			$bc = $bc;
			$eh = $eh;
		}
		
		private ExceptionHandler<IOException>	$eh;
		private ReadableByteChannel		$bc;
		
		public void close() {
			try {
				$bc.close();
			} catch (IOException $ioe) {
				$eh.hear($ioe);
			}
		}
		
		public boolean isOpen() {
			return $bc.isOpen();
		}
		
		public int read(ByteBuffer $dst) {
			try {
				return $bc.read($dst);
			} catch (IOException $ioe) {
				$eh.hear($ioe);
				return 0;	// -1 ...?  i guess it probably doesn't matter, since the exception handler ought to be stopping futher reads from getting pumped anyway.	
			}
		}
	}
}
