package ahs.test;

import ahs.log.*;

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
 * tests; asserts failed do not.
 * </p>
 * 
 * <p>
 * If "confirmation" is enabled, all assertions with a message will emit an INFO-level
 * message when the assertion passes correctly.
 * </p>
 * 
 * @author hash
 * 
 */
public abstract class TestCase implements Runnable {
	public TestCase(Logger $log, boolean $enableConfirmation) {
		this.$log = $log;
		this.$confirm = $enableConfirmation;
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
	
	protected abstract void runTests() throws Exception;
	
	public boolean assertEquals(int $expected, int $actual) {
		if ($expected != $actual) {
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion failed -- "+$expected+" != "+$actual+"."));
			return false;
		} return true;
	}
	
	public boolean assertEquals(String $message, int $expected, int $actual) {
		if ($expected != $actual) {
			$log.error(this.getClass(), DEF, new AssertionFailedError("assertion \""+$message+"\" failed -- "+$expected+" != "+$actual+"."));
			return false;
		}
		if ($confirm) $log.info(this.getClass(), "assertion \"" + $message + "\" passed -- " + $expected + " == " + $actual + ".");
		return true;
	}
	
	
	
	private static class AssertionFailedError extends Error {
		public AssertionFailedError() { super(); }
		public AssertionFailedError(String $arg0) { super($arg0); }
		public AssertionFailedError(Throwable $arg0) { super($arg0); }
		public AssertionFailedError(String $arg0, Throwable $arg1) { super($arg0, $arg1); }
	}
}
