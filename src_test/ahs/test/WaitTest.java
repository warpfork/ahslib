package ahs.test;

import ahs.util.*;

public class WaitTest extends JUnitTestCase {
	public void doLoop(final int $pause, final int $times, final int $error) {
		long $start = X.time();
		for (int $i = 0; $i < $times; $i++)
			X.chill($pause);
		assertEquals($start+($pause*$times), X.time(), $error);
	}
	
	public void testA() {
		doLoop(50,10,10);
	}
	
	public void testA2() {
		doLoop(50,10,3);
	}
	
	public void testA3() {
		doLoop(50,10,1);
	}
	
	public void testB() {
		doLoop(20,20,2);
	}
	
	public void testB10() {
		for (int $i = 0; $i < 10; $i++)
			doLoop(20,20,2);
	}
	
	public void testC() {
		doLoop(500,10,5);
	}
}
