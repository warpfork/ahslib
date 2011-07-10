package us.exultant.ahs.test;

import java.nio.*;
import java.util.*;

/**
 * <p>
 * Utility class for holding data handy in unit testing.
 * </p>
 * 
 * <p>
 * Data held in this class IS in many cases mutable. To check that your test unit hasn't
 * inappropriate modified such state, an equality method is provided that asserts this.
 * However, this isn't foolproof -- your test unit might be modifying data in this class
 * but leaving it in the same state it found it, which may or may not be acceptable to
 * you.
 * </p>
 * 
 * <p>
 * Some data held in this class is random on a PER VM basis -- that is, the data is
 * randomly generated when the class is loaded, but even when new fresh instances of this
 * class are requested (presumably by different test units). If you haven't already
 * adopted the idea that to be rigorous tests should be run multiple times in completely
 * separate executions (this is critical to observe performance correctly, since the JIT
 * can be a source of a similiar scale of randomness), then adopt that idea now.
 * </p>
 * 
 * @author hash
 * 
 */
public final class TestData {
	public static TestData getFreshTestData() {
		return new TestData(SingletonHolder.INSTANCE);
	}
	
	/**
	 * Constructor used on first invocation of TestData.  Does all initialization that should happen once per VM.
	 */
	private TestData() {
		Random $r = new Random();
		
		bb10m = ByteBuffer.allocate(1024 * 1024 * 10);	// 10MB
		while (bb10m.hasRemaining())
			bb10m.putInt($r.nextInt());	// yes, this relies on slightly unspoken alignment
		
	}
	/**
	 * Copy-constructor invoked ever time a "fresh" instance of TestData is requested via the static factory method.
	 * @param $td
	 */
	private TestData(TestData $td) {
		bb10m = $td.bb10m;
	}
	private static class SingletonHolder {
		public static final TestData	INSTANCE	= new TestData();
	}
	
	public final String	s1 = "asdf";
	public final String	s2 = "qwer";
	public final String	s3 = "zxcv";
	public final ByteBuffer	bb5 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x0 });
	public final ByteBuffer	bb26 = ByteBuffer.wrap(new byte[] { 0x2, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x7F, 0x44, -0x1, 0x0 });
	public final ByteBuffer bb10m;
	// considered putting a bb1g here.  however, since we've got the root instance, then a live instance, and then most cases ask for a second fresh instance to assert equality on... yeah, i don't wanna be that much of a dick about ram.
	
}
