package ahs.test;

import ahs.util.*;
import ahs.sh.base.*;

public class ShArgsTest extends TestCase {
	public static void testBlockingRead() {
		final ShStream $x = new ShStream();
		$x.write("asdf");
		assertEquals("asdf", $x.read());
		assertEquals(null, $x.readNow());	// nuthin' there.
		assertEquals(null, $x.readNow());	// nuthin' there.
		assertEquals(null, $x.readNow());	// nuthin' there.
		long $start = X.time();
		new Thread(new Runnable() { public void run() { X.chill(500); $x.write("qwer"); $x.write((String)null); } }).start();
		assertEquals("qwer", $x.read());	// this should block while the other thread is chilling.
		long $spent = X.time() - $start;
		X.saye("spent "+$spent+"ms in testBlockingRead -- "+($spent-500)+"ms longer than ideal.");
		if ($spent < 500) fail();
		// FUNNY: JUnit test can actually report less than 500ms being spent on a test that succeeded.  JUnit timing == SUCK.
		assertEquals(null, $x.readNow());	// nuthin' there.
		assertEquals(null, $x.read());		// the stream is closed.
		assertEquals(null, $x.read());		// the stream is closed.
	}

	public static void testTwoThreads() {
		final int LEN = 200000;
		final ShStream $x = new ShStream();
		new Thread(new Runnable() { public void run() { for (int $i = 0; $i < LEN; $i++) $x.write($i+""); } }).start();
		for (int $i = 0; $i < LEN; $i++)
			assertEquals($i+"", $x.read());
	}
	
	@SuppressWarnings("all")	// tell the implicit cast of null to... well, whatever it gets cast to... in the call to write to stfu and do it.  and if anyone can find a more specific annotation that fixes this, please tell me and god bless you.
	/**
	 * This will hang if it's gonna fail; otherwise it should finish as close to instantly as you care to consider.
	 */
	public static void testThatGoatFuckingTypecast() {
		final ShStream $x = new ShStream();
		$x.write("asdf");
		assertEquals("asdf", $x.read());
		$x.write(null);
		assertEquals(null, $x.read());		// the stream is closed.
	}
	
	public static void testTwoThreadsAndClose() {
		testTwoThreadsAndClose(2000000,100);
	}
	
	@SuppressWarnings("all")
	public static void testTwoThreadsAndClose(final int CYCLES, final int CHILL) {
		try {
			final ShStream $x = new ShStream();
			new Thread(new Runnable() { public void run() { for (int $i = 0; $i < CYCLES; $i++) $x.write($i+""); X.chill(CHILL); $x.write(null); } }).start();
			for (int $i = 0; $i < CYCLES; $i++)
				assertEquals($i+"", $x.read());
			// there can be a tricky race condition here if the ShArgs code doesn't signal stream closing correctly to the blocking read call.
			assertEquals(null, $x.read());		// the stream is closed.
		} catch (X.CryException $e) {
			fail($e.toString());
		}
	}
	
	public static void testTwoThreadsAndCloseManyTimes() {
		int $max = 10000;
		for (int $i = 1; $i <= $max; $i++) {
			testTwoThreadsAndClose(100,1);
			if ($i % 25 == 0) X.saye($i+"");
		}
	}
	
	
	
}
