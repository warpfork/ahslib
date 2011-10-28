/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.test.junit;

import us.exultant.ahs.util.*;

import junit.framework.*;

public class JUnitTestCase extends junit.framework.TestCase {
	public JUnitTestCase() {
		super();
	}
	
	public JUnitTestCase(String $name) {
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
