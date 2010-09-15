package ahs.test;

import ahs.log.*;

/**
 * I believe JUnit is fundamentally flawed in several of its design choices. Most
 * significantly of these is that it delays reporting of exceptions it catches and errors
 * it detects until normal program termination. This is ridiculous, as it actually
 * severely impedes the detection of errors serious enough to disrupt program flow out of
 * normal bounds that result in timely termination.
 * 
 * Behold, a replacement: this class simply passes errors and exceptions it encounters on
 * to a logger. Exceptions that bubble out of a test stop that test and all subsequent
 * tests; asserts failed do not.
 * 
 * @author hash
 * 
 */
public abstract class TestCase implements Runnable {
	public TestCase(Logger $log) {
		this.$log = $log;
	}
	
	public void run() {
		try {
			runTests();
		} catch (Exception $e) {
			$log.error(this.getClass(), $e);
		}
	}
	
	private final Logger $log;
	
	protected abstract void runTests() throws Exception;
	
	public void assertEquals(int $expected, int $actual) {
		if ($expected != $actual) $log.error(this.getClass(), new AssertionFailedError());
	}
	
	public void assertEquals(String $message, int $expected, int $actual) {
		if ($expected != $actual) $log.error(this.getClass(), new AssertionFailedError($message));
	}
	
	
	
	private static class AssertionFailedError extends Error {
		public AssertionFailedError() { super(); }
		public AssertionFailedError(String $arg0) { super($arg0); }
		public AssertionFailedError(Throwable $arg0) { super($arg0); }
		public AssertionFailedError(String $arg0, Throwable $arg1) { super($arg0, $arg1); }
	}
}
