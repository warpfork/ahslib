package ahs.test;

import ahs.json.*;
import ahs.util.*;

import java.nio.charset.*;

import junit.framework.*;

import static java.lang.Math.*;

/**
 * Functional tests to prove basic things about the java language that may seem intuitive,
 * but are still nice to have once demonstrated clearly once and a while as good ol'
 * sanity checks when you've been debugging something that <i>seems</i> simple for endless
 * hours.
 */
public class Sanity extends TestCase {
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
	}
	
	
	
	
	public void testVomitCharsetsAvailable() {
		X.say("==charsets available==");
		for (String $n : Charset.availableCharsets().keySet())
			X.say("\t"+$n);
		X.say("==end charsets==");
	}
	
	
	
	
	public void testBytesAsAscii() {
		byte[] $bats = new byte[(int)pow(2,8)];
		for (byte $i = Byte.MIN_VALUE; $i < Byte.MAX_VALUE; $i++)
			$bats[$i-Byte.MIN_VALUE] = $i;
		$bats[Byte.MAX_VALUE-Byte.MIN_VALUE] = Byte.MAX_VALUE;	// off by one and overflow causes infinity if done in loop.
		X.saye(Arr.toString($bats));
		
		try {
			assertEquals($bats, new String($bats, Strings.ASCII).getBytes(Strings.ASCII));	// nope.  negative bytes change to 63.
			fail("we actually expected this to fail, yo");
		} catch (AssertionFailedError $e) { /* k */ }
		
		char[] $char = new char[(int)pow(2,8)];
		for (int $i = 0; $i <= Byte.MAX_VALUE-Byte.MIN_VALUE; $i++)
			$char[$i] = (char)$bats[$i];
		X.saye(Arr.toString($char));
		
		char[] $char2 = new char[(int)pow(2,8)];
		new String($char).getChars(0, $char.length, $char2, 0);
		assertEquals($char, $char2);
		
		// CRITIAL DETAIL:
		//   THIS IS -STILL- DOING IT WRONG.
		//   Those negative bytes are getting cast way up into the FFxx range of unicode
		//      which is a very scary place to be.
		//      and also not very helpful in trying to avoid encoding expansion.
	}
	
	//CONCLUSION: stay the fuck away from hakx.
	public void testByteMadnessInJson() {
		char[] $char = new char[Byte.MAX_VALUE-Byte.MIN_VALUE+1];
		for (int $i = 0; $i <= Byte.MAX_VALUE-Byte.MIN_VALUE; $i++)
			$char[$i] = (char)$i;
		X.saye(Arr.toString($char));
		byte[] $bats = new byte[(int)pow(2,8)];
		for (byte $i = Byte.MIN_VALUE; $i < Byte.MAX_VALUE; $i++)
			$bats[$i-Byte.MIN_VALUE] = $i;
		$bats[Byte.MAX_VALUE-Byte.MIN_VALUE] = Byte.MAX_VALUE;
		
		JSONObject $jo = new JSONObject();
		$jo.put("k",new String($char));
		String $jos = $jo.toString();
		X.say($jos);
		X.say("bytes spent on json body string with hakx: "+($jos.length()-8));
		X.say("bytes spent on json body string with b64:  "+(new JSONObject(null, null, $bats).toString().length()-8));
		X.say("number of goddamn bytes in the real world: "+(Byte.MAX_VALUE-Byte.MIN_VALUE+1));
	}
	
	
	
	
	
	public void testEvalOrderInNestedCalls() {
		int $x = 0;
		testEvalOrderInNestedCalls_helper($x++,$x++);
	}
	private void testEvalOrderInNestedCalls_helper(int $a, int $b) {
		assertEquals(0, $a);
		assertEquals(1, $b);
	}
}
