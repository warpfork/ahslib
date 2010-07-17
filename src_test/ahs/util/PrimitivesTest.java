package ahs.util;

import ahs.test.*;

public class PrimitivesTest extends TestCase {
	public void testIntBytes() {
		intBytesTrial(0);
		intBytesTrial(1);
		intBytesTrial(-1);
		intBytesTrial(255);
		intBytesTrial(2108579011);
		intBytesTrial(-2108579011);
		intBytesTrial(new byte[] {0,0,0,0});
		intBytesTrial(new byte[] {34,45,67,8});
		intBytesTrial(new byte[] {-128,3,7,8});
		intBytesTrial(new byte[] {127,127,127,127});
		intBytesTrial(new byte[] {-128,-128,-128,-128});
		
		//System.out.println(Primitives.intFromByteArray(new byte[] {127,-128,-128,-128}));
		//System.out.println(Primitives.intFromByteArray(new byte[] {0,0,0,0}));
		//System.out.println(Primitives.intFromByteArray(new byte[] {-128,-128,-128,-128}));
	}
	private void intBytesTrial(byte[] $b) {
		assertEquals($b, Primitives.byteArrayFromInt(Primitives.intFromByteArray($b)));
	}
	private void intBytesTrial(int $i) {
		assertEquals($i, Primitives.intFromByteArray(Primitives.byteArrayFromInt($i)));
	}
	
	public void testStringArrayCleanse() {
		String[] $a1 = new String[] { "a", "b", "c", null, "e", null, null, "h" };
		assertEquals(8,$a1.length);
		String[] $a2 = Arr.arrayCleanse($a1);
		//assertTrue(Arr.equals(new String[] { "a", "b", "c", null, "e", null, null, "h" }, $a1));
		assertTrue(Arr.equals(new String[] { "a", "b", "c", "e", "h" }, $a2));
	}
	
	public void testByteArrayCat() {
		assertTrue(Arr.equals(
				new byte[] { 34, 34, 65, 48, 18, 23, 84, 96, 10 },
				Arr.cat(new byte[] { 34, 34, 65, 48, 18 }, new byte[] { 23, 84, 96, 10 })
		));
	}
	
	public void testMasking() {
		int $i = 4;
		$i = Primitives.addMask($i,3);
		$i = Primitives.removeMask($i,3);
		assertEquals(4,$i);
		$i = Primitives.removeMask($i,7);
		assertEquals(0,$i);
		$i = Primitives.addMask($i,10);
		$i = Primitives.removeMask($i,7);
		assertEquals(8,$i);
	}
	
	public void testMaskContains() {
		assertTrue (Primitives.containsFullMask   (Integer.parseInt( "110010000", 2), Integer.parseInt( "110010000", 2)));
		assertTrue (Primitives.containsFullMask   (Integer.parseInt("0110010000", 2), Integer.parseInt( "110010000", 2)));
		assertTrue (Primitives.containsFullMask   (Integer.parseInt( "110010000", 2), Integer.parseInt("0110010000", 2)));
		assertTrue (Primitives.containsFullMask   (Integer.parseInt( "110010110", 2), Integer.parseInt( "010010000", 2)));
		assertTrue (Primitives.containsFullMask   (Integer.parseInt( "110010110", 2), Integer.parseInt( "000000000", 2)));
		assertFalse(Primitives.containsFullMask   (Integer.parseInt( "110010110", 2), Integer.parseInt( "010010001", 2)));
		assertTrue (Primitives.containsPartialMask(Integer.parseInt( "110010110", 2), Integer.parseInt( "010010001", 2)));
		assertTrue (Primitives.containsPartialMask(Integer.parseInt( "110010110", 2), Integer.parseInt( "110010110", 2)));
		assertFalse(Primitives.containsPartialMask(Integer.parseInt( "110010110", 2), Integer.parseInt( "000000000", 2)));	// not sure i'm a fan of this behavior, but eh.
		assertFalse(Primitives.containsPartialMask(Integer.parseInt( "110010110", 2), Integer.parseInt( "001101001", 2)));
	}
	
	public void testBase64() {
		base64trial(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x77, (byte)0x02, (byte)0xF7, (byte)0x02});
		base64trial(new byte[] { (byte)0x00, (byte)0xFF, (byte)0x97, (byte)0x42, (byte)0x17});
	}
	private void base64trial(byte[] $bat) {
		assertEquals($bat,Base64.decode(Base64.encode($bat)));
	}
}
