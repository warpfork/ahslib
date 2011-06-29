package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.ebon.*;

public class CodecEbonTestHeavy extends CodecEonTest {
	public CodecEbonTestHeavy() {
		super(new EbonCodec());
	}
	
	public void testHard() throws TranslationException {
		for (int $i = 0; $i < 1000000; $i++) {
			testListDenseSerial();
			testCompositeD3NMuxSerial();
		}
	}
}
