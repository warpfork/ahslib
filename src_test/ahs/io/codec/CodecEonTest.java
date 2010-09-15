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
 * @author hash
 * 
 */
public class CodecEonTest extends JUnitTestCase {
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
		
		public int hashCode() {
			return 0;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			return true;
		}
	}

	/**
	 * Contains a single field (a String) that can be handled by any encoding scheme
	 * directly.
	 */
	public static class Ob implements Ox {
		public Ob(String $s) { $dat = $s; }
		
		String $dat;
		
		public static class Den implements Dencoder<EonCodec,EonObject,Ob> {
			public EonObject encode(EonCodec $codec, Ob $x) throws TranslationException {
				return $codec.simple($x,"dat",$x.$dat);
			}
			
			public Ob decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Ob.class);
				return new Ob($x.getStringData());
			}
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.$dat == null) ? 0 : this.$dat.hashCode());
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Ob other = (Ob) obj;
			if (this.$dat == null) {
				if (other.$dat != null) return false;
			} else if (!this.$dat.equals(other.$dat)) return false;
			return true;
		}
	}
	
	/**
	 * Contains a list of #Ob, thus testing both recursive encoding as well as
	 * EonCodec's special functions for lists.
	 */
	public static class Oc implements Ox {
		public Oc(List<Ob> $x) { $dat = $x; }
		
		List<Ob> $dat;
		
		public static class Den implements Dencoder<EonCodec,EonObject,Oc> {
			public EonObject encode(EonCodec $codec, Oc $x) throws TranslationException {
				return $codec.simple($x,null,$codec.encodeList($x.$dat));
			}
			
			public Oc decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				return new Oc($codec.decodeList($x.getArrayData(), Ob.class));
			}
		}
		
		public static class Dense implements Dencoder<EonCodec,EonObject,Oc> {
			public EonObject encode(EonCodec $codec, Oc $x) throws TranslationException {
				EonArray $ja = $codec.newArr();
				for (int $i = 0; $i < $x.$dat.size(); $i++)
					$ja.put($i, $x.$dat.get($i).$dat);
				return $codec.simple($x, null, $ja);
			}
			
			public Oc decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				EonArray $ja = $x.getArrayData();
				List<Ob> $ar = new ArrayList<Ob>($ja.size());
				for (int $i = 0; $i < $ja.size(); $i++)
					$ar.add(new Ob($ja.getString($i)));
				return new Oc($ar);
			}
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.$dat == null) ? 0 : this.$dat.hashCode());
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Oc other = (Oc) obj;
			if (this.$dat == null) {
				if (other.$dat != null) return false;
			} else if (!this.$dat.equals(other.$dat)) return false;
			return true;
		}
	}
	
	/**
	 * Extended by #Ob and #Oc.  Intended for use in mux testing.
	 */
	public static interface Ox {
		/* just for grouping. */
	}
}
