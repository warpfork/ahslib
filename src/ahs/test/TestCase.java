package ahs.test;

import ahs.log.*;
import ahs.test.TestCaseRetro.*;
import ahs.util.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class TestCase implements Runnable {
	/**
	 * 
	 * @param $log
	 *                Fatal failures that cause the entire case to fail to complete
	 *                are logged at ERROR level; failed assertions without a unit are
	 *                logged at WARN level; units that pass as logged at INFO leve,
	 *                and confirmations of individually passed assertions (if enabled)
	 *                are logged at DEBUG level.
	 * @param $enableConfirmation
	 */
	public TestCase(Logger $log, boolean $enableConfirmation) {
		this.$log = $log;
		this.$confirm = $enableConfirmation;
	}
	
	public void run() {
		List<Unit> $units = getUnits();	// list is assumed immutable on pain of death or idiocy
		for (int $i = 0; $i < $units.size(); $i++) {
			Unit $unit = $units.get($i);
			
			try {
				resetFailures();
				$unit.call();
				//TODO log INFO
			} catch (Throwable $e) {
				if ($unit.expectExceptionType() != null) {
					// some kind of exception was expected.
					if ($unit.expectExceptionType().isAssignableFrom($e.getClass())) {
						// and it was this kind that was expected, so this is good.
						//TODO log INFO
					} else {
						// and it wasn't this kind.  this represents fatal failure.
						//TODO log ERROR
						//TODO die
						break;
					}
				} else {
					// no exception was expected.  any exception represents fatal failure.
					//TODO log ERROR
					$log.error(this.getClass(), "fatal exception",  $e);
					//TODO die
					break;
				}
			}
		}
	}
	
	private final Logger		$log;
	private boolean			$confirm;
	private int			$failures;
	
	public abstract List<Unit> getUnits();
	
	/**
	 * Each Unit in a TestCase contains a coherent set of assertions (or just one
	 * assertion) preceeded by code to set up the test. The class name of an instance
	 * of Unit is used when logging the successful passing of a Unit and so use of
	 * anonymous subclasses of Unit is not advised.
	 */
	public abstract class Unit implements Callable<Object> {
		/**
		 * If this returns null, any exception thrown from the {@link #call()}
		 * method results in failure of the Unit and aborting of all further Units
		 * in the entire Case. Otherwise, if this method is overriden to return a
		 * type, an exception <i>must</i> be thrown from the call method that is
		 * instanceof that type, or the Unit fails and all further Units in the
		 * entire Case are aborted.
		 */
		public <$T extends Exception> Class<$T> expectExceptionType() { return null; }
		
		public void breakIfFailed() throws AssertionFailed {
			if ($failures > 0) throw new AssertionFailed("breaking: "+$failures+" failures.");
		}
	}
	
	
	
	private void resetFailures() {
		$failures = 0;
	}
	
	// using autoboxing on primitives as much as these message functions do bothers me but it does save me a helluva lot of lines of code here and i am assuming you're not using any assertions inside of terribly tight loops (or if you are, you're eiter not using confirmation or not failing hundreds of thousands of times).
	// it might be a clarity enhancement to do quotation marks around the actual and expected values depending on type, though, which i don't do right now.
	static String messageFail(String $label, Object $expected, Object $actual) {
		if ($label == null)
			return "assertion \"" + $label + "\" failed -- expected " + $expected + " != " + $actual + " actual.";
		else
			return "assertion failed -- expected " + $expected + " != " + $actual + " actual.";
	}
	static String messagePass(String $label, Object $expected, Object $actual) {
		if ($label == null)
			return "assertion \"" + $label + "\" passed -- expected " + $expected + " == " + $actual + " actual.";
		else
			return "assertion passed -- expected " + $expected + " == " + $actual + " actual.";
	}	// i'm not recycling code in the above two because i think someday i might do some alignment stuff, in which case the above become more complicated cases.
	static String messageFail(String $label, String $message) {
		if ($label == null)
			return "assertion \"" + $label + "\" failed -- " +$message;
		else
			return "assertion failed -- " + $message;
	}
	static String messagePass(String $label, String $message) {
		if ($label == null)
			return "assertion \"" + $label + "\" passed -- " + $message;
		else
			return "assertion passed -- " + $message;
	}
	// note that failure messages get wrapped in exceptions and then given to the logger (with a constant message of "assertion failed")
	//  whereas success messages get passed to the logger as actual messages (with no exception attached).
	//   this... might be a poor inconsistency, since i could see wanting to be able to report line numbers of successes outloud as well. 
	
	
	
	
	
	////////////////
	//  BOOLEAN
	////////////////
	public boolean assertTrue(boolean $bool) {
		return assertEquals(null, true, $bool);
	}
	public boolean assertFalse(boolean $bool) {
		return assertEquals(null, false, $bool);
	}
	public boolean assertEquals(boolean $expected, boolean $actual) {
		return assertEquals(null, $expected, $actual);
	}
	public boolean assertTrue(String $message, boolean $bool) {
		return assertEquals($message, true, $bool);
	}
	public boolean assertFalse(String $message, boolean $bool) {
		return assertEquals($message, false, $bool);
	}
	public boolean assertEquals(String $message, boolean $expected, boolean $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, $expected, $actual)));
			return false;
		}
		if ($confirm) $log.debug(this.getClass(), messagePass($message, $expected, $actual));
		return true;
	}
	
	
	////////////////
	//  Object
	////////////////
	public boolean assertSame(Object $expected, Object $actual) {
		return assertSame(null, $expected, $actual);
	}
	public boolean assertSame(String $message, Object $expected, Object $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, $expected, $actual)));
			return false;
		}
		if ($confirm) $log.debug(this.getClass(), messagePass($message, $expected, $actual));
		return true;
	}
	public boolean assertNull(Object $actual) {
		return assertSame(null, null, $actual);
	}
	public boolean assertNull(String $message, Object $actual) {
		return assertSame($message, null, $actual);
	}
	public boolean assertEquals(Object $expected, Object $actual) {
		return assertEquals(null, $expected, $actual);
	}
	public boolean assertEquals(String $message, Object $expected, Object $actual) {
		if (!$expected.equals($actual)) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, $expected, $actual)));
			return false;
		}
		if ($confirm) $log.debug(this.getClass(), messagePass($message, $expected, $actual));
		return true;
	}
	public boolean assertInstanceOf(Class<?> $klass, Object $obj) {
		return assertInstanceOf(null, $klass, $obj);
	}
	public boolean assertInstanceOf(String $message, Class<?> $klass, Object $obj) {
		if ($obj == null) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, "null is never an instance of anything, and certainly not "+$klass+".")));
			return false;
		}
		try {
			$klass.cast($obj);
			if ($confirm) $log.debug(this.getClass(), messagePass($message, $obj.getClass().getCanonicalName()+"\" is an instance of \""+$klass.getCanonicalName()+"\""));
			return true;
		} catch (ClassCastException $e) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, $e.getMessage()+".")));
			return false;
		}
	}
	
	
	////////////////
	//  String
	////////////////
	// there's not actually a dang thing special about these, i just want the api itself to reassure developers that yes, strings can be asserted on and nothing weird happens.
	public boolean assertEquals(String $expected, String $actual) {
		return assertEquals(null, (Object)$expected, (Object)$actual);
	}
	public boolean assertEquals(String $message, String $expected, String $actual) {
		return assertEquals($message, (Object)$expected, (Object)$actual);
	}
	
	
	////////////////
	//  INT
	////////////////
	public boolean assertEquals(int $expected, int $actual) {
		return assertEquals(null, $expected, $actual);
	}
	public boolean assertEquals(String $message, int $expected, int $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.warn(this.getClass(), new AssertionFailed(messageFail($message, $expected, $actual)));
			return false;
		}
		if ($confirm) $log.debug(this.getClass(), messagePass($message, $expected, $actual));
		return true;
	}
	
	
	////////////////
	//  BYTE
	////////////////
	public boolean assertEquals(byte[] $a, byte[] $b) {
		return assertEquals(Arr.toString($a),Arr.toString($b));
	}
	
	
	////////////////
	//  CHAR
	////////////////
	public boolean assertEquals(char[] $a, char[] $b) {
		return assertEquals(Arr.toString($a),Arr.toString($b));
	}
	
	
	
	
	
	private static class AssertionFailed extends Error {
		public AssertionFailed() { super(); }
		public AssertionFailed(String $arg0) { super($arg0); }
		public AssertionFailed(Throwable $arg0) { super($arg0); }
		public AssertionFailed(String $arg0, Throwable $arg1) { super($arg0, $arg1); }
	}
	
	// Note!  You cannot make methods like:
//	assertNotEquals(byte[] $a, byte[] $b) {
//		return !assertEquals($a, $b);
	// because they'll still do the failure count and the log messages backwards inside.
	
	//future work:
	//   i think it should be more or less possible to provide an interface to retrofit ahs TestCase to JUnit, which would be handy for folks that like the ability integrate JUnit with eclipse plugins or the like.
}