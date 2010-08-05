package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.concurrent.*;

/**
 * <p>
 * If multiple threads simultaneously request reads, fair ordering is not guaranteed in
 * this implementation.
 * </p>
 * 
 * <p>
 * All ByteBuffer objects returned via the various "read" methods return a ByteBuffer of
 * containing a number of byte specified by the "block size" parameter in the constructor,
 * with the notable exception of the final ByteBuffer returned when this stream is closed,
 * which may have a number of bytes ranging from 1 to blocksize inclusive. All
 * ByteBuffers are guaranteed to be backed by an array. All ByteBuffers may be expected to
 * have their position set to zero and their mark to by undefined.
 * </p>
 * 
 * <p>
 * If this implementation of a ReadHead is to be used upon an InputStream from a file on a
 * local storage medium, it is advised that care be taken when choosing how to run the
 * pump. If concerned about the maximum amount of memory to be used on buffering when
 * dealing with large files, the pump should be run only in small increments with read
 * operations keeping pace.
 * </p>
 */
public class StreamReadHead implements ReadHead<ByteBuffer> {
	// some things about the ConcurrentLinkedQueue implementation are inopportune.
	// in particular, the fact that the toArray() method COPIES EVERYTHING.
	//   it's not as bad as it could be; at least it's a shallow copy.
	//   using large block sizes can limit its impact significantly.
	
	public StreamReadHead(InputStream $base, int $blockSize) {
		this.$base = $base;
		this.$blockSize = $blockSize;
		this.$pump = new PumpT();
	}
	
	private final InputStream			$base;
	private final int				$blockSize;
	private final PumpT				$pump;
	private Listener<ReadHead<ByteBuffer>>		$el;
	private ExceptionHandler<IOException>		$eh;
	// buffer-related objects:
	private final ConcurrentLinkedQueue<ByteBuffer>	$q	= new ConcurrentLinkedQueue<ByteBuffer>();
	private final Object				$lfront	= new Object();		// this is the flag we wave at blocked reader threads after a write.
	private boolean					$closed	= false;
	
	/* IMPLEMENTATION DETAILS
	 * ---------------------------------------
	 * 
	 * Reads synchronize on the $w object -- this is their flag; it is notified whenever the buffer has new data available.
	 * 
	 * Getting data from the underlying stream synchronizes somewhere else; it by design it shouldn't need to compete with buffer reads at all.
	 * Operations like setInterruptHandler and setEventListener have to do with the operations powering the underlying stream, and so share syncs with those (and not reads).
	 * 
	 */
	
	/** {@inheritDoc} */
	public Pump getPump() {
		return $pump;
	}
	
	/** {@inheritDoc} */
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}

	/** {@inheritDoc} */
	public void setListener(Listener<ReadHead<ByteBuffer>> $el) {
		this.$el = $el;
	}
	
	
	
	/** {@inheritDoc} */
	public boolean hasNext() {
		return !$q.isEmpty();
	}
	
	/** {@inheritDoc} */
	public ByteBuffer readNow() {
		synchronized ($q) {
			return $q.poll();
		}
	}
	
	/** {@inheritDoc} */
	public ByteBuffer read() {
		ByteBuffer $t = readNow();
		while ($t == null)
			synchronized ($lfront) {
				if (isClosed()) return null;
				else { X.wait($lfront); $t = readNow(); }
			}
		return $t;
	}
	
	/** {@inheritDoc} */
	public ByteBuffer[] readAll() {
		synchronized ($lfront) {
			while (!isClosed())
				X.wait($lfront);
		}
		return $q.toArray(Primitives.EMPTY_BYTEBUFFER);
		// note that we don't bother clearing the q.
		//   the undefined nature of multiple calls to this method are documented in the ReadHead interface,
		//   and from a memory-conversation standpoint, i'm assuming no one is going to keep this thing around
		//    much longer after this method returns.
	}	
	
	/** {@inheritDoc} */
	public ByteBuffer[] readAllNow() {
		// gah.  it's impossible to do this without synchronizing every fucking read everywhere.
		synchronized ($q) {
			ByteBuffer[] $bbs = $q.toArray(Primitives.EMPTY_BYTEBUFFER);
			for (int $i = 0; $i < $bbs.length; $i++) $q.poll();
			return $bbs;
		}
		// this could have also been done by just polling until we hit the bottom,
		//  but this way seems slightly closer to the sense of immediacy that we're going for.
	}
	
	/**
	 * <p>
	 * This method has almost identical semantics to <code>readAll()</code>, except
	 * that it merges all of the ByteBuffers back into a single buffer before
	 * returning it (and like the contract of <code>readAll()</code>, it is undefined
	 * if invoked more than once, since it also clears the internal queue in a
	 * non-atomic fashion).
	 * </p>
	 * 
	 * <p>
	 * More memory- and copy-efficient implementations should probably be sought if
	 * you intend to simply read a complete file all in one go, but this is tolerable
	 * for extremely simple applications that are not concerned about such resource
	 * bounds.
	 * </p>
	 */
	public ByteBuffer readCompletely() {
		ByteBuffer[] $bla = readAll();
		$q.clear();
		ByteBuffer $v = ByteBuffer.allocate($blockSize * $bla.length);
		for (int $i = 0; $i < $bla.length; $i++) {
			$v.put($bla[$i]);	// calling rewind on $bb would be redundant; we know where it came from after all.
			$bla[$i] = null;	// let it become GC'able as soon as possible
		}
		$v.flip();
		return $v;
	}
	
	
	
	/** {@inheritDoc} */
	public boolean isClosed() {
		return $closed;
	}
	
	/** {@inheritDoc}  */
	public void close() throws IOException {
		$base.close();
		// note that we do NOT tell the pump to stop: the pump should keep pumping the underlying stream
		//  until the stream throws errors, or we might lose things in the abyss between buffers.
	}
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}

		public void run(final int $times) {
			byte[] $bats;
			int $actualSizeRead, $currentSum;
			L1: for (int $i = 0; $i < $times; $i++) {
				if (isClosed()) break;
				
				// try to get as much as we can
				$bats = new byte[$blockSize];
				$currentSum = 0;
				try {
					while ($currentSum < $blockSize) {
						$actualSizeRead = $base.read($bats, $currentSum, $blockSize-$currentSum);
						if ($actualSizeRead == -1) {	// EOF
							$closed = true;
							if ($currentSum == 0) {
								X.notifyAll($lfront);	// we actually still need to fire this so that if anyone is blocking for more data that isn't coming they get released.
								break L1;	// harder!  Avoid adding an extra zero-len bb to q.
							}
							break;
						}
						$currentSum += $actualSizeRead;
					}
				} catch (IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					$closed = true;
				}
				
				// wrap it up and enqueue to the buffer
				$q.add(ByteBuffer.wrap($bats, 0, $currentSum));
				
				// signal that we got a new chunk in
				Listener<ReadHead<ByteBuffer>> $dated_el = $el;
				if ($dated_el != null) $dated_el.hear(StreamReadHead.this);
				
				X.notifyAll($lfront);
			}
		}
	}
}
