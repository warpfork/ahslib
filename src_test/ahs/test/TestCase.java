package ahs.test;

import ahs.util.*;

import junit.framework.*;

public class TestCase extends junit.framework.TestCase {
	public TestCase() {
		super();
	}
	
	public TestCase(String $name) {
		super($name);
	}
	
	
	public void assertInstanceOf(Class<?> $klass, Object $obj) {
		if ($obj == null)
			fail("null is never an instance of anything.");
		try {
			$klass.cast($obj);
		} catch ( ClassCastException $e ) {
			fail($e.getMessage());
		}
	}
	
	
	
	
//	public void assertEquals(byte[] $ba, byte[] $bb) {
//		if ($ba.length != $bb.length) fail();
//		for (int $i = 0; $i < $ba.length; $i++)
//			assertEquals($ba[$i],$bb[$i]);
//	}
	public void assertEquals(byte[] $a, byte[] $b) {
		assertEquals(Arr.toString($a),Arr.toString($b));
	}
	public void assertEquals(char[] $a, char[] $b) {
		assertEquals(Arr.toString($a),Arr.toString($b));
	}
	
	public void assertNotEquals(byte[] $a, byte[] $b) {
		try {
			assertEquals($a, $b);
		} catch (AssertionFailedError $e) { /* Good, we wanted that */ return; }
		fail("want not eq");
	}
}
