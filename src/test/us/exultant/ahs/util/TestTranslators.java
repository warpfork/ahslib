package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import us.exultant.ahs.test.*;
import java.nio.*;
import java.util.*;

public class TestTranslators extends TestCase {
	public static void main(String... $args) { new TestTranslators().run(); }
	
	public List<Unit> getUnits() {
		return Arr.<Unit>asList(
				new TestHexConsistency(),
				new TestHexAccuracy()
		);
	}

	/**  */
	private class TestHexConsistency extends TestCase.Unit {
		public Object call() throws TranslationException {
			byte[] $x = new byte[] { (byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE };
			byte[] $y = TranslatorFromHex.INSTANCE.translate(TranslatorToHex.INSTANCE.translate(ByteBuffer.wrap($x))).array();
			assertEquals($x, $y);
			return null;
		}
	}
	
	/**  */
	private class TestHexAccuracy extends TestCase.Unit {
		public Object call() throws TranslationException {
			byte[] $x = new byte[] { (byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE };
			byte[] $y = TranslatorFromHex.INSTANCE.translate("CAFEBABE").array();
			assertEquals($x, $y);
			return null;
		}
	}
}
