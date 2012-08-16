/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 * 
 * AHSlib is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation,
 * version 3 of the License, or (at the original copyright holder's option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.eon.pre.*;
import us.exultant.ahs.codec.json.*;
import us.exultant.ahs.test.*;
import java.util.*;

public class BitVectorTest extends TestCase {
	public static void main(String... $args) {
		new BitVectorTest().run();
	}
	
	public List<Unit> getUnits() {
		return Arr.asList(
				new TestBitSetStupidity(),
				new TestStringConstructor(),
				new TestBitSetStuff(),
				new TestGet(),
				new TestShift(),
				new TestUnshift(),
				new TestToByteArray(),
				new TestPop(),
				new TestPush(),
				new TestSlide(),
				new TestRslide(),
				new TestByteArrayConstructors(),
				new TestJsonSerialization()
		);
	}
	
	
	
	private class TestBitSetStupidity extends TestCase.Unit {
		public Object call() {
			BitSet $bs = new BitSet(3);
			$bs.set(2);	// so we get "001"
			assertEquals("001", new BitVector($bs).toString());
			
			// UNTRUE:
			//$bs = new BitSet(3);
			//assertEquals("000",new BitVector($bs).toString());
			
			$bs = new BitSet(15);
			$bs.set(2);
			assertEquals(false, $bs.get(900));
			assertEquals(3, $bs.length());	// length on bitset has no bearing on what length you tell it to have.
			
			return null;
		}
	}
	
	
	
	private class TestStringConstructor extends TestCase.Unit {
		public Object call() {
			trial("1");
			trial("101");
			trial("101100110010101110101001110111111101101010010100100101111101010100101");
			trial("101100");
			return null;
		}
		
		private void trial(String $s) {
			assertEquals($s, new BitVector($s).toString());
		}
	}
	
	
	
	private class TestBitSetStuff extends TestCase.Unit {
		public Object call() {
			BitSet $bs = new BitSet(3);
			$bs.set(2);	// so we get "001"
			assertEquals("001", new BitVector($bs).toString());
			
			assertEquals("{}", $bs.get(0, 2).toString());
			assertEquals("{1}", $bs.get(1, 3).toString());
			
			$bs = new BitSet(10);
			$bs.set(2);
			$bs.set(9);
			assertEquals("0010000001", new BitVector($bs).toString());
			assertEquals("{2, 9}", $bs.get(0, 10).toString());
			
			return null;
		}
	}
	
	
	
	private class TestGet extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("00100111");
			
			BitSet $bs = $bv.$bs.get(0, 3);
			assertEquals(3, $bs.length());
			
			BitVector $bv2 = $bv.get(0, 2);
			assertEquals(2, $bv2.length());
			assertEquals("00", $bv2.toString());
			
			$bv2 = $bv.get(1, 3);
			assertEquals(2, $bv2.length());
			assertEquals("01", $bv2.toString());
			
			$bv2 = $bv.get(2, 5);
			assertEquals(3, $bv2.length());
			assertEquals("100", $bv2.toString());
			
			return null;
		}
	}
	
	
	
	private class TestShift extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("001");
			assertEquals(false, $bv.shift());
			assertEquals(2, $bv.length());
			assertEquals(false, $bv.shift());
			assertEquals(true, $bv.shift());
			assertEquals(0, $bv.length());
			try {
				$bv.shift();
				breakUnit("didn't throw exception when shifting on empty");
			} catch (IndexOutOfBoundsException $e) {}
			
			$bv = new BitVector("101100110010101110101001110111111101101010010100100101111101010100101");
			BitVector $bv2 = $bv.shift(5);
			assertEquals(5, $bv2.length());
			assertEquals("10110", $bv2.toString());
			assertEquals("0110010101110101001110111111101101010010100100101111101010100101", $bv.toString());
			
			return null;
		}
	}
	
	
	
	private class TestUnshift extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("001");
			$bv.unshift(true);
			assertEquals("1001", $bv.toString());
			
			$bv = new BitVector("110010");
			$bv.unshift(true);
			$bv.unshift(false);
			$bv.unshift(true);
			$bv.unshift(false);
			$bv.unshift(false);
			assertEquals("00101110010", $bv.toString());
			
			$bv = new BitVector("110010");
			$bv.unshift("");
			assertEquals("110010", $bv.toString());
			$bv.unshift("00101");
			assertEquals("00101110010", $bv.toString());
			
			$bv = new BitVector("110010");
			$bv.unshift(new BitVector("00101"));
			assertEquals("00101110010", $bv.toString());
			
			return null;
		}
	}
	
	
	
	private class TestToByteArray extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("1");
			assertEquals(-128, $bv.toByteArray()[0]);
			
			$bv = new BitVector("00000001");
			assertEquals(1, $bv.toByteArray()[0]);
			
			$bv = new BitVector("00001011");
			assertEquals(11, $bv.toByteArray()[0]);
			
			$bv = new BitVector("0000101100000000");
			assertEquals(11, $bv.toByteArray()[0]);
			assertEquals(0, $bv.toByteArray()[1]);
			
			$bv = new BitVector("000000010000001111111111");
			assertEquals(24, $bv.length());
			assertEquals(1, $bv.toByteArray()[0]);
			assertEquals(3, $bv.toByteArray()[1]);
			assertEquals(-1, $bv.toByteArray()[2]);
			
			return null;
		}
	}
	
	
	
	private class TestPop extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("110010");
			assertEquals(false, $bv.pop());
			assertEquals(true, $bv.pop());
			assertEquals(4, $bv.length());
			assertEquals("1100", $bv.toString());
			assertEquals("100", $bv.pop(3).toString());
			assertEquals("1", $bv.toString());
			
			return null;
		}
	}
	
	
	
	private class TestPush extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("110010");
			$bv.push(true);
			$bv.push("110");
			$bv.push(new BitVector("00010"));
			assertEquals("110010111000010", $bv.toString());
			
			return null;
		}
	}
	
	
	
	private class TestSlide extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("110010");
			$bv.emptySlide(2);
			assertEquals("001100", $bv.toString());
			
			BitVector $bv2 = $bv.slide(new BitVector("101"));
			assertEquals("101001", $bv.toString());
			assertEquals("100", $bv2.toString());
			
			return null;
		}
	}
	
	
	
	private class TestRslide extends TestCase.Unit {
		public Object call() {
			BitVector $bv = new BitVector("110010");
			BitVector $bv2 = $bv.rslide(new BitVector("101"));
			assertEquals("010101", $bv.toString());
			assertEquals("110", $bv2.toString());
			
			return null;
		}
	}
	
	
	
	private class TestByteArrayConstructors extends TestCase.Unit {
		public Object call() {
			byte[] $t;
			BitVector $bv;
			
			$t = new byte[] { 0x0 };
			$bv = new BitVector($t);
			assertEquals("00000000", $bv.toString());
			assertEquals($t, $bv.toByteArray());
			
			$t = new byte[] { 0x21 };
			$bv = new BitVector(new byte[] { 0x21 });
			assertEquals("00100001", $bv.toString());
			assertEquals($t, $bv.toByteArray());
			
			$t = new byte[] { 0x21, -0x01, 0x21 };
			$bv = new BitVector($t);
			assertEquals("001000011111111100100001", $bv.toString());
			assertEquals($t, $bv.toByteArray());
			
			$bv = new BitVector(new byte[] { 0x21, -0x01, 0x21 }, 4, 15);
			assertEquals("000111111111001", $bv.toString());
			
			return null;
		}
	}
	
	
	
	private class TestJsonSerialization extends TestCase.Unit {
		public Object call() throws TranslationException {
			JsonCodec $codec = new JsonCodec();
			$codec.putHook(BitVector.class, BitVectorDencoder.ENCODER);
			$codec.putHook(BitVector.class, BitVectorDencoder.DECODER);
			BitVector $bv, $bv2;
			JsonObject $jo;
			
			$bv = new BitVector("10100001");
			$jo = $codec.encode($bv);
			X.saye($jo + "");
			$bv2 = $codec.decode($jo, BitVector.class);
			assertEquals($bv, $bv2);
			
			return null;
		}
	}
}
