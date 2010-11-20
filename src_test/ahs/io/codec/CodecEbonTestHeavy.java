package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.ebon.*;
import ahs.test.*;
import ahs.util.*;

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
