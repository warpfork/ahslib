package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class ReadHeadStreamToByteBuffer implements ReadHead<ByteBuffer> {
	public ReadHeadStreamToByteBuffer(InputStream $base, int $blockSize) {
		this.$base = $base;
		this.$blockSize = $blockSize;
		this.$pipe = new Pipe<ByteBuffer>();
		this.$pump = new PumpT();
	}
	
	private final InputStream			$base;
	private final int				$blockSize;
	private final PumpT				$pump;
	private final Pipe<ByteBuffer>			$pipe;
	private Listener<ReadHead<ByteBuffer>>		$el;
	private ExceptionHandler<IOException>		$eh;
	
	public Pump getPump() {
		return $pump;
	}

	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}

	public void setListener(Listener<ReadHead<ByteBuffer>> $el) {
		this.$el = $el;
	}
	
	public ByteBuffer read() {
		return $pipe.SRC.read();
	}
	
	public ByteBuffer readNow() {
		return $pipe.SRC.readNow();
	}
	
	public boolean hasNext() {
		return $pipe.SRC.hasNext();
	}
	
	public List<ByteBuffer> readAll() {
		return $pipe.SRC.readAll();
	}
	
	public List<ByteBuffer> readAllNow() {
		return $pipe.SRC.readAllNow();
	}
	
	public boolean isClosed() {
		return $pipe.SRC.isClosed();
	}
	
	public void close() throws IOException {
		$base.close();
	}
	
	/**
	 * this function is for updating our state at this level to fully closed (i.e.,
	 * after the event has propagated through the underlying stream and buffering, and
	 * we will never add data to the readable buffer).
	 */
	private void ourClose() throws IOException {
		$base.close();	// this is likely redundant, but can't hurt.
		
		try {
			$pipe.SRC.close();	// this transparently handles interruption of any still-blocking reads as well as return of the final readAll.
		} catch (IOException $e) {
			/* this can't actually happen in a pipe */
		}
		
		// give our listener a chance to notice our closure.  (pipe doesn't know our listener.)  (our isClosed method refers to pipe, which already considers itself completely closed.)
		Listener<ReadHead<ByteBuffer>> $dated_el = $el;
		if ($dated_el != null) $dated_el.hear(this);
	}
	
	/**
	 * <p>
	 * This method has almost identical semantics to <code>readAll()</code> (and uses
	 * that method internally, so the same caveats about multiple invocations apply),
	 * except that it merges all of the ByteBuffers back into a single buffer before
	 * returning it. More memory- and copy-efficient implementations should probably
	 * be sought if you intend to simply read a complete file all in one go, but this
	 * is tolerable for extremely simple applications that are not concerned about
	 * such resource bounds.
	 * </p>
	 */
	public ByteBuffer readCompletely() {
		List<ByteBuffer> $bla = readAll();
		ByteBuffer $v = ByteBuffer.allocate($blockSize * $bla.size());
		Iterator<ByteBuffer> $itr = $bla.iterator();
		while ($itr.hasNext()) {
			$v.put($itr.next());	// calling rewind on $bb would be redundant; we know where it came from after all.
			$itr.remove();		// let it become GC'able as soon as possible
		}
		$v.flip();
		return $v;
	}
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}
		
		public synchronized void run(final int $times) {
			byte[] $bats;
			int $actualSizeRead, $currentSum;
			boolean $die = false;
			ExceptionHandler<IOException> $dated_eh = null;
			for (int $i = 0; $i < $times; $i++) {
				if (isDone()) break;
				
				// try to get as much as we can
				$bats = new byte[$blockSize];
				$currentSum = 0;
				try {
					while ($currentSum < $blockSize) {
						$actualSizeRead = $base.read($bats, $currentSum, $blockSize-$currentSum);
						if ($actualSizeRead == -1) {	// EOF
							$die = true;
							break;
						} else
							$currentSum += $actualSizeRead;
					}
				} catch (IOException $e) {
					$dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					$die = true;
					// i guess it's somewhat debatable whether or not any exception should close... but that's what InputStream does, so i'm sticking to it for the time 
				}
				
				// wrap it up and enqueue to the buffer
				// readers will immediately Notice the new data due to the pipe's internal semaphore doing its job
				if ($currentSum > 0) try {
					$pipe.SINK.write(ByteBuffer.wrap($bats, 0, $currentSum));
				} catch (IOException $e) {
					/* this can't actually happen in a pipe */
				}
				
				// signal that we got a new chunk in
				Listener<ReadHead<ByteBuffer>> $dated_el = $el;
				if ($dated_el != null) $dated_el.hear(ReadHeadStreamToByteBuffer.this);
				
				// this is roughly atomic in that no other writes should be able to happen before this since we're the only thread pumping
				if ($die) try {
					ourClose();	// it is in fact ok to flag ourselves as closed here, since regardless of how we got here the underlying stream is already not willing to give us more data
				} catch (IOException $e) {
					if ($dated_eh != null) $dated_eh = $eh;		// if we didn't get it earlier in this cycle, get it now; if we did, keep that one
					if ($dated_eh != null) $dated_eh.hear($e);	// now tell 'em
				}
			}
		}
	}
}
