package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.*;

public class StringStreamReadHead implements ReadHead<String> {
	public StringStreamReadHead(InputStream $base, Charset $cs) {
		this.$base = new BufferedReader(new InputStreamReader($base, $cs));
		this.$pump = new PumpT();
	}
	
	private final BufferedReader			$base;
	private final PumpT				$pump;
	private Listener<ReadHead<String>>		$el;
	private ExceptionHandler<IOException>		$eh;
	// buffer-related objects:
	private final ConcurrentLinkedQueue<String>	$q	= new ConcurrentLinkedQueue<String>();
	private final Object				$lfront	= new Object();		// this is the flag we wave at blocked reader threads after a write.
	private boolean					$closed	= false;
	
	/** {@inheritDoc} */
	public Pump getPump() {
		return $pump;
	}
	
	/** {@inheritDoc} */
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}

	/** {@inheritDoc} */
	public void setListener(Listener<ReadHead<String>> $el) {
		this.$el = $el;
	}
	
	
	
	/** {@inheritDoc} */
	public boolean hasNext() {
		return !$q.isEmpty();
	}
	
	/** {@inheritDoc} */
	public String readNow() {
		synchronized ($q) {
			return $q.poll();
		}
	}
	
	/** {@inheritDoc} */
	public String read() {
		String $t = readNow();
		while ($t == null)
			synchronized ($lfront) {
				if (isClosed()) return null;
				else { X.wait($lfront); $t = readNow(); }
			}
		return $t;
	}
	
	/** {@inheritDoc} */
	public String[] readAll() {
		synchronized ($lfront) {
			while (!isClosed())
				X.wait($lfront);
		}
		return $q.toArray(Primitives.EMPTY_STRING);
		// note that we don't bother clearing the q.
		//   the undefined nature of multiple calls to this method are documented in the ReadHead interface,
		//   and from a memory-conversation standpoint, i'm assuming no one is going to keep this thing around
		//    much longer after this method returns.
	}
	
	/** {@inheritDoc} */
	public String[] readAllNow() {
		// gah.  it's impossible to do this without synchronizing every fucking read everywhere.
		synchronized ($q) {
			String[] $ss = $q.toArray(Primitives.EMPTY_STRING);
			for (int $i = 0; $i < $ss.length; $i++) $q.poll();
			return $ss;
		}
		// this could have also been done by just polling until we hit the bottom,
		//  but this way seems slightly closer to the sense of immediacy that we're going for.
	}
	
	/**
	 * <p>
	 * This method has almost identical semantics to <code>readAll()</code>, except
	 * that it merges all of the String objects back into a single contiguous String
	 * before returning it, and it does not wait until the stream is closed to begin
	 * consuming reads.
	 * </p>
	 * 
	 * <p>
	 * All line breaks become the UNIX standard line break (i.e., the '\n' character),
	 * and the returned string will include a trailing line break, regardless of
	 * whether the original stream ended in one or become closed because of exception
	 * (even mid-line).
	 * </p>
	 */
	public String readCompletely() {
		StringBuilder $sb = new StringBuilder();
		while (hasNext() || !isClosed())
			$sb.append(read()).append('\n');
		return $sb.toString();
		
	}
	
	
	
	/** {@inheritDoc} */
	public boolean isClosed() {
		return $closed;
	}
	
	/** {@inheritDoc}  */
	public StringStreamReadHead close() throws IOException {
		$base.close();
		return this;
	}
	
	private class PumpT implements Pump {
		public boolean isDone() {
			return isClosed();
		}

		public void run(final int $times) {
			String $t = null;
			L1: for (int $i = 0; $i < $times; $i++) {
				if (isClosed()) break;
				
				// try to get as much as we can
				try {
					$t = $base.readLine();
					if ($t == null) {	// EOF
						$closed = true;
						break L1;	// harder!  Avoid adding an extra zero-len bb to q.
					}
				} catch (IOException $e) {
					ExceptionHandler<IOException> $dated_eh = $eh;
					if ($dated_eh != null) $dated_eh.hear($e);
					$closed = true;
				}
				
				// wrap it up and enqueue to the buffer
				$q.add($t);
				
				// signal that we got a new chunk in
				Listener<ReadHead<String>> $dated_el = $el;
				if ($dated_el != null) $dated_el.hear(StringStreamReadHead.this);
				
				X.notifyAll($lfront);
			}
		}
	}
}
