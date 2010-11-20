package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.ebon.*;
import ahs.test.*;
import ahs.util.*;

import java.util.*;

/**
 * Any Eon implementor should be able to make an instantiable test case out of this by
 * just making a default constructor that hands an appropriate codec to this testcase.
 * 
 * This does NOT test EonRAD or EonRAE in any way. Annotative reflective crap is its own
 * bag.
 * 
 * This does NOT test boundary conditions on things like Int.MAX_VALUE or Double.NAN --
 * these are things that (perhaps unfortunately) aren't actually fully specified for the
 * Eon interfaces. Some implementations (like EBON) will deal with these readily, while
 * others (like JSON) have other specifications that I have no control over which forbid
 * them these options.
 * 
 * @author hash
 * 
 */
abstract class CodecEonTest extends JUnitTestCase {
	protected CodecEonTest(EonCodec $seed) {
		this.$seed = $seed;
	}
	
	private EonCodec $seed;
	
	public void setUp() throws Exception {
		super.setUp();
		X.saye("");
	}
	
	
	
	// single object with no fields
	
	public void testTrivial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oe.class, new Oe.Den());
		
		Oe $x1 = new Oe();
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		Oe $x2 = $codec.decode($c, Oe.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testTrivialSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oe.class, new Oe.Den());
		
		Oe $x1 = new Oe();
		byte[] $c = $codec.serialize($x1);
		Oe $x2 = $codec.deserialize($c, Oe.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// single object with primitive field
	
	public void testSimple() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(O1p.class, new O1p.Den());
		
		O1p $x1 = new O1p("stuff");
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		O1p $x2 = $codec.decode($c, O1p.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testSimpleSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(O1p.class, new O1p.Den());
		
		O1p $x1 = new O1p("stuff");
		byte[] $c = $codec.serialize($x1);
		O1p $x2 = $codec.deserialize($c, O1p.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// object with both a primitive and composite field (which may not be null). depth = 2.
	
	public void testCompositeD2() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oc.class, new Oc.DenNoMux());
		$codec.putHook(O1p.class, new O1p.Den());
		
		Oc $x1 = new Oc(17, new O1p("thingy\nsauce"));
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		Oc $x2 = $codec.decode($c, Oc.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testCompositeD2Serial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oc.class, new Oc.DenNoMux());
		$codec.putHook(O1p.class, new O1p.Den());
		
		Oc $x1 = new Oc(17, new O1p("stuff"));
		byte[] $c = $codec.serialize($x1);
		Oc $x2 = $codec.deserialize($c, Oc.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// object with both a primitive and composite field (which is be null). depth = 2.
	
	public void testCompositeD2N() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oc.class, new Oc.DenNoMux());
		$codec.putHook(O1p.class, new O1p.Den());
		
		Oc $x1 = new Oc(17, null);
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		Oc $x2 = $codec.decode($c, Oc.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testCompositeD2NSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		$codec.putHook(Oc.class, new Oc.DenNoMux());
		$codec.putHook(O1p.class, new O1p.Den());
		
		Oc $x1 = new Oc(17, null);
		byte[] $c = $codec.serialize($x1);
		Oc $x2 = $codec.deserialize($c, Oc.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// object with both a primitive and composite field.  the composite field is muxed.  depth = 2.
	
	public void testCompositeD2Mux() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Oc.class, new Oc.DenMux());
		
		Oc $x1 = new Oc(17, new Oc(321512, new O1p("jump")));
		EonObject $c = $codec.encode($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye(str($c));
		Oc $x2 = (Oc)$codec.decode($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testCompositeD2MuxSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Oc.class, new Oc.DenMux());
		
		Oc $x1 = new Oc(17, new Oc(321512, new O1p("jump")));
		byte[] $c = $codec.serialize($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!;
		Oc $x2 = (Oc)$codec.deserialize($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// object with both primitive and composite fields, some of which may be null.  muxed.  depth = 3.
	
	public void testCompositeD3NMux() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Oc.class, new Oc.DenMux());
		
		Oc $x1 = new Oc(17, new Oc(321512, new Oc(33, new O1p("deep"))));
		EonObject $c = $codec.encode($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye(str($c));
		Oc $x2 = (Oc)$codec.decode($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
	
	public void testCompositeD3NMuxSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Oc.class, new Oc.DenMux());

		Oc $x1 = new Oc(17, new Oc(321512, new Oc(33, new O1p("deep"))));
		byte[] $c = $codec.serialize($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!;
		Oc $x2 = (Oc)$codec.deserialize($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	// object with a list which in turn contains composites.
	
	/**
	 * tests the recursive stuff where an object contains a list that it hands off to
	 * codec and hopes for the best.
	 */
	public void testList() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		$codec.putHook(O1p.class, new O1p.Den());
		$codec.putHook(Ol.class, new Ol.Den());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		Ol $x2 = $codec.decode($c, Ol.class);
		
		assertEquals($x1, $x2);
	}

	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public void testListDense() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		$codec.putHook(O1p.class, new O1p.Den());
		$codec.putHook(Ol.class, new Ol.Dense());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		EonObject $c = $codec.encode($x1);
		X.saye(str($c));
		Ol $x2 = $codec.decode($c, Ol.class);
		
		assertEquals($x1, $x2);
	}

	public void testListDenseSerial() throws TranslationException {
		EonCodec $codec = new EonCodec($seed);
		$codec.putHook(O1p.class, new O1p.Den());
		$codec.putHook(Ol.class, new Ol.Dense());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		byte[] $c = $codec.serialize($x1);
		Ol $x2 = $codec.deserialize($c, Ol.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// stuff!
	
	protected String str(EonObject $eo) {
		if ($eo instanceof EbonObject) return ((EbonObject)$eo).toArrStr();
		return $eo.toString();
	}
	
	/**
	 * Simplest possible target.  Contains no fields.  None.
	 */
	public static class Oe {
		public Oe() {}
		
		public static class Den implements Dencoder<EonCodec,EonObject,Oe> {
			public EonObject encode(EonCodec $codec, Oe $x) throws TranslationException {
				return $codec.simple($x,null,"nothing");
			}
			
			public Oe decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oe.class);
				return new Oe();
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
	public static class O1p implements OFace {
		public O1p(String $s) { $dat = $s; }
		
		String $dat;
		
		public static class Den implements Dencoder<EonCodec,EonObject,O1p> {
			public EonObject encode(EonCodec $codec, O1p $x) throws TranslationException {
				return $codec.simple($x,"dat",$x.$dat);
			}
			
			public O1p decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(O1p.class);
				return new O1p($x.getStringData());
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
			O1p other = (O1p) obj;
			if (this.$dat == null) {
				if (other.$dat != null) return false;
			} else if (!this.$dat.equals(other.$dat)) return false;
			return true;
		}
	}
	
	public static class Oc implements OFace {
		public Oc(int $val, OFace $kid) {
			this.$val = $val;
			this.$kid = $kid;
		}
		
		int $val;
		OFace $kid;

		public static class DenNoMux implements Dencoder<EonCodec,EonObject,Oc> {
			public EonObject encode(EonCodec $codec, Oc $x) throws TranslationException {
				EonObject $eo = $codec.newObj();
				$eo.putKlass(Oc.class);
				$eo.put("val", $x.$val);
				if ($x.$kid != null) $eo.put("bebe", $codec.encode($x.$kid));
				return $eo;
			}
			
			public Oc decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				return new Oc(
						$x.getInt("val"), 
						$x.optObj("bebe") == null ? null : $codec.decode($x.getObj("bebe"), O1p.class)	// note that this is NOT using polymorphism.  this works only for some values of $kid, but doesn't demand muxing be used in the codec.
				);
			}
		}
		
		public static class DenMux implements Dencoder<EonCodec,EonObject,Oc> {
			public EonObject encode(EonCodec $codec, Oc $x) throws TranslationException {
				EonObject $eo = $codec.newObj();
				$eo.putKlass(Oc.class);
				$eo.put("val", $x.$val);
				if ($x.$kid != null) $eo.put("bebe", $codec.encode($x.$kid, OFace.class));	// this is another place you have to make dang sure you're demanding the polymorphic behavior you expect from muxing.
				return $eo;
			}
			
			public Oc decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				return new Oc(
						$x.getInt("val"), 
						$x.optObj("bebe") == null ? null : $codec.decode($x.getObj("bebe"), OFace.class)	// demands muxing on the OFace interface be set up in the codec.
				);
			}
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.$kid == null) ? 0 : this.$kid.hashCode());
			result = prime * result + this.$val;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Oc other = (Oc) obj;
			if (this.$kid == null) {
				if (other.$kid != null) return false;
			} else if (!this.$kid.equals(other.$kid)) return false;
			if (this.$val != other.$val) return false;
			return true;
		}
	}
	
	/**
	 * Contains a list of Ob, thus testing both recursive encoding as well as
	 * EonCodec's special functions for lists.
	 */
	public static class Ol implements OFace {
		public Ol(List<O1p> $x) { $dat = $x; }
		
		List<O1p> $dat;
		
		public static class Den implements Dencoder<EonCodec,EonObject,Ol> {
			public EonObject encode(EonCodec $codec, Ol $x) throws TranslationException {
				return $codec.simple($x,null,$codec.encodeList($x.$dat));
			}
			
			public Ol decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Ol.class);
				return new Ol($codec.decodeList($x.getArrayData(), O1p.class));
			}
		}
		
		public static class Dense implements Dencoder<EonCodec,EonObject,Ol> {
			public EonObject encode(EonCodec $codec, Ol $x) throws TranslationException {
				EonArray $ja = $codec.newArr();
				for (int $i = 0; $i < $x.$dat.size(); $i++)
					$ja.put($i, $x.$dat.get($i).$dat);
				return $codec.simple($x, null, $ja);
			}
			
			public Ol decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(Ol.class);
				EonArray $ja = $x.getArrayData();
				List<O1p> $ar = new ArrayList<O1p>($ja.size());
				for (int $i = 0; $i < $ja.size(); $i++)
					$ar.add(new O1p($ja.getString($i)));
				return new Ol($ar);
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
			Ol other = (Ol) obj;
			if (this.$dat == null) {
				if (other.$dat != null) return false;
			} else if (!this.$dat.equals(other.$dat)) return false;
			return true;
		}
	}
	
	/**
	 * Implemented by O1p and Ol for use in mux testing. (Also, this is what she
	 * showed Lumberg.)
	 */
	public static interface OFace {
		/* just for grouping. */
	}
}
