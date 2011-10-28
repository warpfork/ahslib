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

package us.exultant.ahs.test;

import us.exultant.ahs.util.*;
import us.exultant.ahs.test.junit.*;
import java.nio.charset.*;

/**
 * Functional tests to prove basic things about the java language that may seem intuitive,
 * but are still nice to have once demonstrated clearly once and a while as good ol'
 * sanity checks when you've been debugging something that <i>seems</i> simple for endless
 * hours.
 */
public class SanityTest extends JUnitTestCase {
	//CONCLUSION: breaking a loop means the incrementor is not excuted that last time.
	//CONCLUSION: the incrementor is only executed when the end of the loop body is reached.
	//OUTPUT: after loop: 4\nafter loop: 6\n
	public void testForLoop() {
		int $i;
		for ($i = 0; $i < 6; $i++) {
			if ($i == 4) break;
		}
		X.say("after loop: "+$i);
		
		for ($i = 0; $i < 6; $i++) {}
		X.say("after loop: "+$i);
	}
	
	
	
	
	
	//CONCLUSION: primitive arrays outside a function can show mutations made inside a function they were passed to.
	public void testPrimitiveArrayMutability() {
		String[] $x = new String[] { "asdf", "qwer" };
		assertTrue(Arr.equals(new String[] { "asdf", "qwer" }, $x));
		helper_testPrimitiveArrayMutability($x);
		assertTrue(Arr.equals(new String[] { "asdf", "zxcv" }, $x));
	}
	public void helper_testPrimitiveArrayMutability(String[] $in) {
		$in[1] = "zxcv";
	}
	
	
	
	
	
	//CONCLUSION: so basically... yes, if you pass an array into a varargs method, that external array is STILL mutable from inside the method.
	public void testVarArgsMutability() {
		assertTrue(Arr.equals(new String[] { "asdf", "qwer" }, helper_testVarArgsMutability_doNothing("asdf", "qwer")));
		assertTrue(Arr.equals(new String[] { "asdf", "zxcv" }, helper_testVarArgsMutability_mutate("asdf", "qwer")));
		
		String[] $x = new String[] { "asdf", "qwer" };
		assertTrue(Arr.equals(new String[] { "asdf", "zxcv" }, helper_testVarArgsMutability_mutate($x)));
		assertTrue(Arr.equals(new String[] { "asdf", "zxcv" }, $x));
	}
	public String[] helper_testVarArgsMutability_doNothing(String... $ins) {
		return $ins;
	}
	public String[] helper_testVarArgsMutability_mutate(String... $ins) {
		$ins[1] = "zxcv";
		return $ins;
	}
	
	
	
	
	
	private static class Blah {}
	private static class Blag extends Blah {}
	private static class Sup<$WHAT extends Blah> {}
	private static class Sub<$WHAT extends Blah> extends Sup<$WHAT> {}
	public void testGenericUpsubAntefiling() {	// incredibly enough, that name is sort of apt.
		  Sup<Blah> $a = new Sup<Blah>();	// no cast:	WIN
		  Sup<Blag> $d = new Sup<Blag>();	// no cast:	WIN
		  Sup<Blah> $b = new Sub<Blah>();	// up cast out:	WIN
		  Sup<Blag> $c = new Sub<Blag>();	// up cast out:	WIN
		//Sup<Blah> $e = new Sup<Blag>();	// up cast in:	FAIL	"type mismatch: cannot convert ..."
		//Sub<Blah> $e = new Sub<Blag>();	// up cast in:	FAIL	"type mismatch: cannot convert ..."
		  // and no, putting a cast explicitly in parens doesn't help either.
	}
	
	
	
	
	
	public void testVomitCharsetsAvailable() {
		X.say("==charsets available==");
		for (String $n : Charset.availableCharsets().keySet())
			X.say("\t"+$n);
		X.say("==end charsets==");
	}
	
	
	
	
	
	//CONCLUSION: within a single function, if arguments have to be evaluated recursively, it goes left to right
	public void testEvalOrderInNestedCalls() {
		int $x = 0;
		testEvalOrderInNestedCalls_helper($x++,$x++);
	}
	private void testEvalOrderInNestedCalls_helper(int $a, int $b) {
		assertEquals(0, $a);
		assertEquals(1, $b);
	}
	
	
	
	
	
	//CONCLUSION: yes, you CAN select which method to use by limiting the type information of its arguments.
	//   ...you can do a LOT of magic with interfaces here if you're into that sort of thing.
	public void testArgsOverriding() {
		OverrideSub $x = new OverrideSub();
		assertEquals(1, OverrideChooser.yayy($x));
		assertEquals(0, OverrideChooser.yayy((OverrideSup)$x));
	}
	private class OverrideSub extends OverrideSup {}
	private class OverrideSup {}
	private static class OverrideChooser {
		public static int yayy(OverrideSup $sup) { return 0; }
		public static int yayy(OverrideSub $sup) { return 1; }
	}
}
