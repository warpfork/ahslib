package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.CodecJsonTest.*;
import ahs.io.codec.eon.*;
import ahs.test.*;

import java.util.*;

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
		
		public static class Den implements Dencoder<EonCodec,EonObject,Oa> {
			public EonObject encode(EonCodec $codec, Oa $x) throws TranslationException {
				return $codec.simple($x,null,"nothing");
			}
			
			public Oa decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oa.class);
				return new Oa();
			}
		}
	}

	/**
	 * Contains a single field (a String) that can be handled by any encoding scheme
	 * directly.
	 */
	public static class Ob {
		public Ob(String $s) { $dat = $s; }
		
		String $dat;
		
		public static class Den implements Dencoder<EonCodec,EonObject,Ob> {
			public EonObject encode(EonCodec $codec, Ob $x) throws TranslationException {
				return $codec.simple($x,null,$x.$dat);
			}
			
			public Ob decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Ob.class);
				return new Ob($x.getStringData());
			}
		}
	}
	
	/**
	 * Contains a list of #Ob, thus testing both recursive encoding as well as
	 * EonCodec's special functions for lists.
	 */
	public static class Oc {
		public Oc(List<Ob> $x) { $dat = $x; }
		
		List<Ob> $dat;

		public static class Den implements Dencoder<EonCodec,EonObject,Oc> {
			public EonObject encode(EonCodec $codec, Oc $x) throws TranslationException {
				return $codec.simple($x,"Ob",$codec.encodeList($x.$dat));
			}
			
			public Oc decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				return new Oc($codec.decodeList($x.getArrayData(), Ob.class));
			}
		}
	}
}
