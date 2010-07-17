package ahs.sh.base;

import ahs.util.*;

import java.util.concurrent.*;
import java.util.regex.*;

// this will be super-easy to make very generic.  i'm deferring that until i come up with an idiom i like with network transports vs pipes and streams, though.
// the above tend to segment themselves nicely into halves more than anything else.
// there's constructor weirdness.
// but overall i'd say there's two generic endpoints that you can wrap around on ShStream<?>.
// TCP sockets act more like two ShStream than anything else, which is kinda silly.  obviously they remain different in that they lack actual sane message sizes, but still.
// anyway, the interface of this particular class will never shift away from String objects, obviously.  all the generics will be in ahs... either io or util.  hard to decide which.

/**
 * <p>
 * Used to convey information between "shell processes"; can be used to either provide
 * actual arguments to a command (including the command name itself with certain
 * factories), or as the pipe between commands (or commands and the terminal, or files, or
 * etc). In either case, the strings can not contain line breaks; line breaks in an output
 * stream are represented as separate string entries.
 * </p>
 * 
 * <p>
 * It's generally expected that one thread may be reading and one thread may be writing,
 * since this is a metaphor for the connection between two processes in a shell. However,
 * there is sufficient synchronization to allow pretty much any abuse.
 * </p>
 * 
 * <p>
 * In the back-end, it's pretty much just a ConcurrentQueue for String objects.
 * </p>
 * 
 * @author hash
 * 
 */
public class ShStream {
	private static final Pattern		BR	= Pattern.compile("\n", Pattern.LITERAL);
	private ConcurrentLinkedQueue<String>	$q	= new ConcurrentLinkedQueue<String>();
	private Object				$w	= new Object();	// this is the flag we wave at blocked reader threads after a write.
	private boolean				$closed	= false;	// only modify this when sync'd on $w.
													
	/**
	 * <p>
	 * Write a <code>null</code> to indicate that this output stream should be closed.
	 * Strings containing line break characters (the <code>'\n'</code> character) will
	 * be split.
	 * </p>
	 * 
	 * <p>
	 * When multiple strings (or a string that becomes split into multiple strings)
	 * are passed to this method, they are all written in order (i.e., atomically),
	 * but the first of them may be read before the last of them is entered and the
	 * write method returns (so... not atomically).
	 * </p>
	 * 
	 * <p>
	 * If the stream has been closed, future writes will fail silently. If a
	 * <code>null</code> is encountered in the middle of a single call to write (that
	 * is, with other strings both before and after the null), only the strings before
	 * the null will be entered.
	 * </p>
	 * 
	 * @param $ss
	 * @return the same object the call was invoked on (for invocation chaining).
	 */
	public ShStream write(String... $ss) {
		if ($ss == null) { close(); return this; }	// this is a strange hack to avoid odd typecasting choices made by the jvm when null is given
		synchronized (this) {	// make writes atomic at the scale of this function
			for (String $s : $ss)
				if ($s == null) {
					close();
					return this;
				} else
					for (String $x : BR.split($s))
						$q.add($x);
			X.notifyAll($w);	// notify blocked reader threads they have hope
		}
		return this;
	}
	
	/**
	 * <p>
	 * Close this stream to further writing. Read invocations blocking for any reason
	 * (and, in particular, reads blocking for stream completion) will return
	 * following invocation of this function. Only the first invocation of this
	 * function has any effect.
	 * </p>
	 * 
	 * <p>
	 * If the stream has been closed, future writes will fail silently.
	 * </p>
	 * 
	 * @return the same object the call was invoked on (for invocation chaining).
	 */
	public ShStream close() {
		if ($closed == true) return this;
		$closed = true;
		X.notifyAll($w);	// notify blocked reader threads they have hope
		return this;
	}
	
	public boolean isClosed() {
		return $closed;
	}
	
	public boolean hasNext() {
		return $q.isEmpty();
	}
	
	/**
	 * <p>
	 * Blocking read.  Elements that are read are removed from the stream.
	 * </p>
	 * 
	 * <p>
	 * If multiple threads block on this, there is no guarantee of fairness (that is,
	 * the first thread to block might not be the first thread to unblock; the order
	 * is random)... because why the hell would you have multiple threads reading from
	 * this in the first place? Otherwise, sychronity is maintained -- there will be
	 * no double-reads, null pointer exceptions, concurrent modification exceptions,
	 * or etc.
	 * </p>
	 * 
	 * @return next chunk of input
	 */
	public String read() {
		String $t = readNow();
		while ($t == null)
			synchronized ($w) {
				if (isClosed()) return null;
				else { X.wait($w); $t = readNow(); }
			}
		return $t;
	}
	
	/**
	 * Nonblocking read.  Elements that are read are removed from the stream.
	 * 
	 * @return a String if possible; null may indicate either EOF or simply nothing available at the time.
	 */
	public String readNow() {
		return $q.poll();
	}
	
	/**
	 * Blocks until the stream is closed, then returns its entire contents at once
	 * (minus, of course, any entries that have already been read).
	 */
	public String[] readAll() {
		synchronized ($w) {
			while (!isClosed())
				X.wait($w);
		}
		return (String[])$q.toArray();
	}
	
	/**
	 * Immediately returns entire contents of this stream at once (minus, of course,
	 * any entries that have already been read).
	 */
	public String[] readAllNow() {
		return (String[])$q.toArray();
		// not sure if this clears from the q like one might expect from the general contract of the other methods.
	}
}
