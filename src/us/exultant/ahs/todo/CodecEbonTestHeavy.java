package us.exultant.ahs.codec;

import us.exultant.ahs.io.*;
import us.exultant.ahs.codec.*;
import us.exultant.ahs.codec.eon.*;
import us.exultant.ahs.codec.ebon.*;
import us.exultant.ahs.test.*;
import us.exultant.ahs.util.*;

import java.util.*;

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
