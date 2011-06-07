package us.exultant.ahs.util;

import us.exultant.ahs.io.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Low cohesion in abundance.
 * 
 * Or, "loco", if you prefer.
 * 
 * 74A.
 * 
 * @author hash
 *
 */
public class X {
	public static final PrintStream silent = new PrintStream(new OutputStreamDiscard());
	public static final Runtime runtime = Runtime.getRuntime();
	
	//@SuppressWarnings("unchecked")
	//public static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(0));
	
	
	
	/**
	 * Shorthand for System.out.println(String).
	 */
	public static void say(String $s) {
		System.out.println($s);
	}

	/**
	 * Writes a line to stdout, prefixing it with the id number of the current thread.
	 */
	public static void sayt(String $s) {
		System.out.println("T"+Thread.currentThread().getId()+":\t"+$s);
	}
	
	/**
	 * Shorthand for System.err.println(String).
	 */
	public static void saye(String $s) {
		System.err.println($s);
	}
	
	/**
	 * Writes a line to stderr, prefixing it with the id number of the current thread.
	 */
	public static void sayet(String $s) {
		System.err.println("T"+Thread.currentThread().getId()+":\t"+$s);
	}
	
	/**
	 * Shorthand for System.out.println(Strings.toHex(byte[])).
	 */
	public static void say(byte[] $s) {
		System.out.println(Strings.toHex($s));
	}
	
	/**
	 * Shorthand for System.err.println(Strings.toHex(byte[])).
	 */
	public static void saye(byte[] $s) {
		System.err.println(Strings.toHex($s));
	}
	
	/**
	 * @return ms
	 */
	public static long time() {
		return System.currentTimeMillis();
	}
	
	/**
	 * <p>
	 * If something throws an exception that you really rather wouldn't even
	 * acknowledge the possibility of, just invoke this in the catch clause. It uses
	 * the ishy exception as the source for a runtime exception.
	 * </p>
	 * 
	 * @param $e
	 *                Exception that made us cry.
	 */
	public static void cry(Throwable $e) {
		throw new CryException($e);
	}
	
	/**
	 * @see #cry(Throwable)
	 */
	public static void cry() {
		throw new CryException();
	}
	
	/**
	 * @see #cry(Throwable)
	 */
	public static void cry(String $m, Throwable $e) {
		throw new CryException($m, $e);
	}
	
	/**
	 * @see #cry(Throwable)
	 */
	public static void cry(String $m) {
		throw new CryException($m);
	}
	
	private static class CryException extends Error {
		public CryException() { super(); }
		public CryException(String $message, Throwable $cause) { super($message, $cause); }
		public CryException(String $message) { super($message); }
		public CryException(Throwable $cause) { super($cause); }
	}
	
	public static String toString(Throwable $t) {
		ByteArrayOutputStream $baos = new ByteArrayOutputStream();
		PrintStream $ps = new PrintStream($baos);
		$t.printStackTrace($ps);
		return $baos.toString();
		// this could also be done with something more like
		//	StringWriter writer = new StringWriter();
		//	$t.printStackTrace(new PrintWriter(writer));
		// relative speed unknown; either way we're ignoring the locality of charset issue
		// but for once i think that's fine in this context.
	}
	
	
	public static void chill(final long $ms) {
		chillUntil(X.time()+$ms);
	}
	public static void chillUntil(final long $end) {
		try {
			chillUntilInterruptably($end);
		} catch (InterruptedException $e) {
			cry($e);	// i just don't feel like writing a try-catch block outside of this guy, really.
		}
	}
	public static void chillInterruptably(final long $ms) throws InterruptedException {
		chillUntilInterruptably(X.time()+$ms);
	}
	public static void chillUntilInterruptably(final long $end) throws InterruptedException {
		long $dist = $end - X.time();
		if ($dist <= 0) return; // without wasting time allocating a temporary object and synchronizing on it
		
		final Object $x = new Object();
		synchronized ($x) {
			do {
				$x.wait($dist);
				$dist = $end - X.time();
			} while ($dist > 0);
		}	
	}
	
	public static void wait(final Object $sync) {
		synchronized ($sync) {
			try {
				$sync.wait();
			} catch (InterruptedException $e) {
				cry($e);
			}
		}
	}
	
	public static void notifyAll(final Object $sync) {
		synchronized ($sync) {
			$sync.notifyAll();
		}	
	}
	
	

	/**
	 * as per runtime.exec, but waits for completion and returns the exit value of the
	 * process.
	 * 
	 * @param cmdarray
	 * @param envp
	 * @param dir
	 * @return the exit value of the process, or Integer.MIN_VALUE in the case of
	 *         IOExceptions.
	 */
	public static int exec(String[] cmdarray, String[] envp, File dir) {
		try {
			Process $p = runtime.exec(cmdarray, envp, dir);
			$p.waitFor();
			return $p.exitValue();
		} catch (IOException $e) {
			return Integer.MIN_VALUE;
		} catch (InterruptedException $e) {
			$e.printStackTrace();
			return Integer.MIN_VALUE;
		}
	}
}
