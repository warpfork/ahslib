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

package us.exultant.ahs.util;

import us.exultant.ahs.test.*;
import java.util.*;

public class BitVectorTestSpeed extends TestCase {
	public static void main(String... $args) { new BitVectorTestSpeed().run(); }
	
	public List<Unit> getUnits() {
		return Arr.asList(
				new TestToByteArray_t1(),
				new TestToByteArray_t3(),
				new TestByteArrayConstructors()
		);
	}
	
	public static final int LOTS = 1000000;
	
	//LOTS = 100000000 =>
	//0.00102737	0.00108977
	//0.00105995	0.00112417	// yup, look at that.  t3 is consistently slower (ever so slightly) than t1... but only when doing them longer.
	//0.00172712	0.00168323
	
	//LOTS = 1000000 =>
	//0.001178	0.001234
	//0.001106	0.001195
	//0.00182	0.001814
	
	
	private class TestToByteArray_t1 extends TestCase.Unit {
		public void call() {
			long $start = X.time();
			
			BitVector $bv;
			for (int $i = 0; $i < LOTS; $i++) {
				$bv = new BitVector("1");
				assertEquals(-128,$bv.toByteArray_t1()[0]);
				
				$bv = new BitVector("00000001");
				assertEquals(1,$bv.toByteArray_t1()[0]);
				
				$bv = new BitVector("00001011");
				assertEquals(11,$bv.toByteArray_t1()[0]);
				
				$bv = new BitVector("0000101100000000");
				assertEquals(11,$bv.toByteArray_t1()[0]);
				assertEquals(0,$bv.toByteArray_t1()[1]);
				
				$bv = new BitVector("000000010000001111111111");
				assertEquals(24,$bv.length());
				assertEquals(1,$bv.toByteArray_t1()[0]);
				assertEquals(3,$bv.toByteArray_t1()[1]);
				assertEquals(-1,$bv.toByteArray_t1()[2]);
			}
			
			double $long = (X.time()-$start)/(double)LOTS;
			X.say($long+"");
		}
	}
	
	

	private class TestToByteArray_t3 extends TestCase.Unit {
		public void call() {
			long $start = X.time();
			
			BitVector $bv;
			for (int $i = 0; $i < LOTS; $i++) {
				$bv = new BitVector("1");
				assertEquals(-128,$bv.toByteArray_t3()[0]);
				
				$bv = new BitVector("00000001");
				assertEquals(1,$bv.toByteArray_t3()[0]);
				
				$bv = new BitVector("00001011");
				assertEquals(11,$bv.toByteArray_t3()[0]);
				
				$bv = new BitVector("0000101100000000");
				assertEquals(11,$bv.toByteArray_t3()[0]);
				assertEquals(0,$bv.toByteArray_t3()[1]);
				
				$bv = new BitVector("000000010000001111111111");
				assertEquals(24,$bv.length());
				assertEquals(1,$bv.toByteArray_t3()[0]);
				assertEquals(3,$bv.toByteArray_t3()[1]);
				assertEquals(-1,$bv.toByteArray_t3()[2]);
			}
	
			double $long = (X.time()-$start)/(double)LOTS;
			X.say($long+"");
		}
	}
	
	

	private class TestByteArrayConstructors extends TestCase.Unit {
		public void call() {
			long $start = X.time();
			
			byte[] $t;
			BitVector $bv;
			for (int $i = 0; $i < LOTS; $i++) {
				$t = new byte[] {0x0};
				$bv = new BitVector($t);
				assertEquals("00000000",$bv.toString());
				assertEquals($t, $bv.toByteArray());
				
				$t = new byte[] {0x21};
				$bv = new BitVector(new byte[] {0x21});
				assertEquals("00100001",$bv.toString());
				assertEquals($t, $bv.toByteArray());
				
				$t = new byte[] {0x21, -0x01, 0x21};
				$bv = new BitVector($t);
				assertEquals("001000011111111100100001",$bv.toString());
				assertEquals($t, $bv.toByteArray());
				
				$bv = new BitVector(new byte[] {0x21, -0x01, 0x21},4,15);
				assertEquals("000111111111001",$bv.toString());
			}
	
			double $long = (X.time()-$start)/(double)LOTS;
			X.say($long+"");
		}
	}
}
