package us.exultant.ahs.test;

import us.exultant.ahs.log.*;
import us.exultant.ahs.util.*;

/**
 * <p>
 * I believe JUnit is fundamentally flawed in several of its design choices. Most
 * significantly of these is that it delays reporting of exceptions it catches and errors
 * it detects until normal program termination. This is ridiculous, as it actually
 * severely impedes the detection of errors serious enough to disrupt program flow out of
 * normal bounds that result in timely termination.
 * </p>
 * 
 * <p>
 * Behold, a replacement: this class simply passes errors and exceptions it encounters on
 * to a logger. Exceptions that bubble out of a test stop that test and all subsequent
 * tests; asserts failed do not unless it is explicitly requested that they do so (this
 * allows a series assertions to be used to report on the state of several critical
 * variables at once so that the developer can easily get a more complete picture of the
 * system state before aborting the test).
 * </p>
 * 
 * <p>
 * Units of a test will not be multithreaded. I believe that if a project's tests benefit
 * from mulithreading, it is the designer that should declare this, and is not for the
 * testing system to presume. For the same reason, the ordering of execution of tests is
 * clear and respected, because often a set of tests progresses from one state to the next
 * and thus order of execution is important. These are two choices again firmly at odds
 * with the design philosophy behind JUnit.
 * </p>
 * 
 * <p>
 * If "confirmation" is enabled, all assertions with a message will emit an INFO-level
 * message when the assertion passes correctly.
 * </p>
 * 
 * @author hash
 * @deprecated This system is being replaced by {@link TestCase}, in which the concrete subclasses
 *             provide a list of runnable units instead of simply overriding the entire
 *             runTests method, which will allow the abstract TestCase system to take more
 *             observance of the delination between units without assuming compliance from
 *             concrete subclasses.
 */
// there's also a ton of confirms missing here and just an incredibly shitty lack of internal abstraction
@Deprecated()
public abstract class TestCaseRetro implements Runnable {
	public TestCaseRetro(Logger $log, boolean $enableConfirmation) {
		this.$log = $log;
		this.$confirm = $enableConfirmation;
//		resetFailures();
	}
	
	public void run() {
		try {
			runTests();
		} catch (Exception $e) {
			$log.error(this.getClass(), "fatal exception",  $e);
		}
	}
	
	private final Logger		$log;
	private boolean			$confirm;
	private static final String	DEF	= "assertion failed";
	private int			$failures;
	
	protected abstract void runTests() throws Exception;
	
	
	
	
	// i wish java had function pointers sometimes.  this is one of those times.  i could just actually have the try block in here so the class given actually matters.  maybe i'll implement that in the next generation tester when i'm actually using a Callable or something.
	public void exceptionExpected(Class<? extends Throwable> $c) {
		$failures++;
		$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected an "+$c.getCanonicalName()+" to be thrown."));
	}
	
	////////////////
	//  BOOLEAN
	////////////////
	public boolean assertTrue(boolean $bool) {
		return assertEquals(true, $bool);
	}
	public boolean assertFalse(boolean $bool) {
		return assertEquals(false, $bool);
	}
	public boolean assertEquals(boolean $expected, boolean $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		} return true;
	}
	public boolean assertEquals(String $message, boolean $expected, boolean $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		}
		if ($confirm) $log.info(this.getClass(), "assertion \"" + $message + "\" passed -- expected " + $expected + " == " + $actual + " actual.");
		return true;
	}
	
	
	////////////////
	//  Object
	////////////////
	public boolean assertNull(Object $x) {
		if ($x != null) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected null != "+$x+" actual."));
			return false;
		} return true;
	}
	
	public boolean assertEquals(Object $expected, Object $actual) {
		if (!$expected.equals($actual)) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		} return true;
	}
	public boolean assertEquals(String $message, Object $expected, Object $actual) {
		if (!$expected.equals($actual)) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		}
		if ($confirm) $log.info(this.getClass(), "assertion \"" + $message + "\" passed -- expected " + $expected + " == " + $actual + " actual.");
		return true;
	}
	
	public boolean assertInstanceOf(Class<?> $klass, Object $obj) {
		if ($obj == null) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- null is never an instance of anything, and certainly not "+$klass+"."));
			return false;
		}
		try {
			$klass.cast($obj);
			return true;
		} catch (ClassCastException $e) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- "+$e.getMessage()+"."));
			return false;
		}
	}
	public boolean assertInstanceOf(String $message, Class<?> $klass, Object $obj) {
		if ($obj == null) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- null is never an instance of anything, and certainly not "+$klass+"."));
			return false;
		}
		try {
			$klass.cast($obj);
			return true;
		} catch (ClassCastException $e) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- "+$e.getMessage()+"."));
			return false;
		}
	}
	
	
	////////////////
	//  String
	////////////////
	public boolean assertEquals(String $expected, String $actual) {
		if (!$expected.equals($actual)) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		} return true;
	}
	public boolean assertEquals(String $message, String $expected, String $actual) {
		if (!$expected.equals($actual)) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		}
		if ($confirm) $log.info(this.getClass(), "assertion \"" + $message + "\" passed -- expected " + $expected + " == " + $actual + " actual.");
		return true;
	}
	
	
	////////////////
	//  INT
	////////////////
	public boolean assertEquals(int $expected, int $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		} return true;
	}
	public boolean assertEquals(String $message, int $expected, int $actual) {
		if ($expected != $actual) {
			$failures++;
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- expected "+$expected+" != "+$actual+" actual."));
			return false;
		}
		if ($confirm) $log.info(this.getClass(), "assertion \"" + $message + "\" passed -- expected " + $expected + " == " + $actual + " actual.");
		return true;
	}
	
	
	////////////////
	//  BYTE
	////////////////
	public boolean assertEquals(byte[] $a, byte[] $b) {
		return assertEquals(Arr.toString($a),Arr.toString($b));
	}
	
	public boolean assertNotEquals(byte[] $a, byte[] $b) {
		return !assertEquals($a, $b);
	}
	
	
	
	public boolean assertEquals(char[] $a, char[] $b) {
		return assertEquals(Arr.toString($a),Arr.toString($b));
	}
	
	
	
	
	
	private static class AssertionFailedError extends Error {
		public AssertionFailedError() { super(); }
		public AssertionFailedError(String $arg0) { super($arg0); }
		public AssertionFailedError(Throwable $arg0) { super($arg0); }
		public AssertionFailedError(String $arg0, Throwable $arg1) { super($arg0, $arg1); }
	}
}
