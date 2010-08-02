package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.CodecJsonTest.*;
import ahs.io.codec.eon.*;
import ahs.test.*;

/**
 * This isn't a test of... well, anything, actually. It just provides a bunch of objects
 * of various nestings that can be used in specific tests of specific codecs and
 * encodings.
 * 
 * All of the provided classes can be instantiated in their native state using the
 * TestObject(CodecTest $blah) constructor; the default constructors should not be used
 * since those are involved in the reflection used in some annotative encoders.
 * 
 * @author hash
 * 
 */
public class CodecTest extends TestCase {
	/**
	 * Simplest possible target.  Contains no fields.  None.
	 */
	public static class Oa {
		public Oa() {}
		public Oa(CodecTest $blah) {}
		
		public static class Den implements Dencoder<EonObject,Oa> {
			public EonObject encode(Codec<EonObject> $codec, Oa $x) throws TranslationException {
				EonCodec $c = (EonCodec)$codec;
				return $c.simple($x,null,"nothing");
			}
			
			public Oa decode(Codec<EonObject> $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oa.class);
				return new Oa();
			}
		}
	}
}
