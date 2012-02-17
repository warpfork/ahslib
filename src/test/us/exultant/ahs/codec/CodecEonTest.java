/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 * 
 * AHSlib is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation,
 * version 3 of the License, or (at the original copyright holder's option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.eon.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.util.*;

/**
 * <p>
 * Any {@link EonCodec} implementor should be able to make an instantiable test case out
 * of this by just making a default constructor that hands an appropriate codec to this
 * testcase.
 * </p>
 * 
 * <p>
 * This does NOT test {@link EonRAD} or {@link EonRAE} in any way; reflective annotative
 * stuff is its own bag.
 * </p>
 * 
 * <p>
 * This does NOT test boundary conditions on things like {@link Integer#MAX_VALUE} or
 * {@link Double#NaN} &mdash; these are things that (perhaps unfortunately) aren't
 * actually fully specified for the Eon interfaces. Some implementations (like EBON) will
 * deal with these readily, while others (like JSON) have other specifications that I have
 * no control over which forbid them these options.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
abstract class CodecEonTest extends TestCase {
	public CodecEonTest(EonCodec $seed) {
		this($seed, new Logger(Logger.LEVEL_TRACE), true);
	}
	
	public CodecEonTest(EonCodec $seed, Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
		this.$seed = $seed;
	}
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestTrivial());
		$tests.add(new TestTrivialSerial());
		$tests.add(new TestPrimitiveSerial());
		$tests.add(new TestCompositeD2Serial());
		$tests.add(new TestCompositeNullSerial());
		$tests.add(new TestCompositeD3Mux());
		$tests.add(new TestCompositeD4NMuxSerial());
		$tests.add(new TestList());
		$tests.add(new TestListDense());
		$tests.add(new TestListDenseSerial());
		return $tests;
	}
	
	/**
	 * This is set in the constructor and all codecs for each test are created from it
	 * via the copy constructors. Thus, this determines the object and array types for
	 * the tests.
	 */
	protected final EonCodec	$seed;
	
	
	
	
	/** Encode and decode a single object with no fields. */
	public class TestTrivial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjTrivial.class, new TobjTrivial.Den());
			
			TobjTrivial $x1 = new TobjTrivial();
			EonObject $c = $codec.encode($x1);
			$log.trace(this, str($c));
			TobjTrivial $x2 = $codec.decode($c, TobjTrivial.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode, serialize, deserialize, and decode a single object with no fields. */
	public class TestTrivialSerial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjTrivial.class, new TobjTrivial.Den());
			
			TobjTrivial $x1 = new TobjTrivial();
			byte[] $c = $codec.serialize($x1);
			$log.trace(this, Strings.semireadable($c));
			TobjTrivial $x2 = $codec.deserialize($c, TobjTrivial.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode, serialize, deserialize, and decode a single object with one primitive field. */
	public class TestPrimitiveSerial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			
			TobjPrimitive $x1 = new TobjPrimitive("stuff");
			byte[] $c = $codec.serialize($x1);
			TobjPrimitive $x2 = $codec.deserialize($c, TobjPrimitive.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode, serialize, deserialize, and decode an object with a primitive field and a composite field (depth=2). */
	public class TestCompositeD2Serial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjComposite.class, new TobjComposite.DenNoMux());
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			
			TobjComposite $x1 = new TobjComposite(17, new TobjPrimitive("stuff"));
			byte[] $c = $codec.serialize($x1);
			TobjComposite $x2 = $codec.deserialize($c, TobjComposite.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode, serialize, deserialize, and decode an object with a primitive field and a composite field which is set to null. */
	public class TestCompositeNullSerial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjComposite.class, new TobjComposite.DenNoMux());
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());		//XXX: is this needed here?  unsure.  probably something that belongs in documentation.
			
			TobjComposite $x1 = new TobjComposite(17, null);
			byte[] $c = $codec.serialize($x1);
			TobjComposite $x2 = $codec.deserialize($c, TobjComposite.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode and decode an object with a primitive field and a composite field (depth=3).  The composite field is muxed. */
	public class TestCompositeD3Mux extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
			$mux.enroll(TobjPrimitive.class, new TobjPrimitive.Den());
			$mux.enroll(TobjComposite.class, new TobjComposite.DenMux());
			
			TobjComposite $x1 = new TobjComposite(17, new TobjComposite(321512, new TobjPrimitive("jump")));
			EonObject $c = $codec.encode($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
			X.saye(str($c));
			TobjComposite $x2 = (TobjComposite) $codec.decode($c, OFace.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	
	
	/** Encode, serialize, deserialize, and decode an object with a primitive field and a composite field (depth=4), some which may be null.  Muxed.*/
	public class TestCompositeD4NMuxSerial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($codec, OFace.class);
			$mux.enroll(TobjPrimitive.class, new TobjPrimitive.Den());
			$mux.enroll(TobjComposite.class, new TobjComposite.DenMux());
			
			TobjComposite $x1 = new TobjComposite(17, new TobjComposite(321512, new TobjComposite(33, new TobjPrimitive("deep"))));
			byte[] $c = $codec.serialize($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!;
			TobjComposite $x2 = (TobjComposite) $codec.deserialize($c, OFace.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	


	/**
	 * Tests a list with a {@link Decoder} that uses the
	 * {@link EonCodec#encodeList(List)} and
	 * {@link EonCodec#decodeList(EonArray, Class)} methods to deal with the
	 * composites contained in the list.
	 */
	public class TestList extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			$codec.putHook(TobjFaceList.class, new TobjFaceList.Den());
			
			TobjFaceList $x1 = new TobjFaceList(Arr.asList(new TobjPrimitive[] { new TobjPrimitive("before the cream sits out too long"), new TobjPrimitive("you must whip it"), new TobjPrimitive("whip it"), new TobjPrimitive("whip it good") }));
			EonObject $c = $codec.encode($x1);
			X.saye(str($c));
			TobjFaceList $x2 = $codec.decode($c, TobjFaceList.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	


	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public class TestListDense extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			$codec.putHook(TobjFaceList.class, new TobjFaceList.Dense());
			
			TobjFaceList $x1 = new TobjFaceList(Arr.asList(new TobjPrimitive[] { new TobjPrimitive("before the cream sits out too long"), new TobjPrimitive("you must whip it"), new TobjPrimitive("whip it"), new TobjPrimitive("whip it good") }));
			EonObject $c = $codec.encode($x1);
			X.saye(str($c));
			TobjFaceList $x2 = $codec.decode($c, TobjFaceList.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	


	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public class TestListDenseSerial extends TestCase.Unit {
		public Object call() throws TranslationException {
			EonCodec $codec = new EonCodec($seed);
			$codec.putHook(TobjPrimitive.class, new TobjPrimitive.Den());
			$codec.putHook(TobjFaceList.class, new TobjFaceList.Dense());
			
			TobjFaceList $x1 = new TobjFaceList(Arr.asList(new TobjPrimitive[] { new TobjPrimitive("before the cream sits out too long"), new TobjPrimitive("you must whip it"), new TobjPrimitive("whip it"), new TobjPrimitive("whip it good") }));
			byte[] $c = $codec.serialize($x1);
			TobjFaceList $x2 = $codec.deserialize($c, TobjFaceList.class);
			
			assertEquals($x1, $x2);
			return null;
		}
	}
	
	

	
//	/**
//	 * same as previous recursive test, except the codec isn't initialized with all of
//	 * the needed encoders or decoders.
//	 */
//	public void testListFailFromMissingDencoder() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(TobjFaceList.class, new TobjFaceList.Den());
//		
//		try {
//			TobjFaceList $toy = new TobjFaceList(Arr.asList(new TobjPrimitive[] { new TobjPrimitive("whip it") }));
//			$jc.encode($toy);
//			fail("Encoding should have failed.");
//		} catch (TranslationException $e) {
//			assertEquals("Encoding dispatch hook not found for ahs.codec.CodecEonTest$O1p",$e.getMessage());	
//		}
//	}
	
	
	
	
	
	// stuff!
	
	protected String str(EonObject $eo) {
		//if ($eo instanceof EbonObject) try {
		//	return Strings.semireadable($eo.serialize());
		//} catch (TranslationException $e) { throw new MajorBug($e); }
		return $eo.toString();
	}
	
	
	
	/**
	 * Simplest possible target. Contains no fields. None.
	 */
	public static class TobjTrivial {
		public TobjTrivial() {}
		
		
		
		public static class Den implements Dencoder<EonCodec,EonObject,TobjTrivial> {
			public EonObject encode(EonCodec $codec, TobjTrivial $x) throws TranslationException {
				return $codec.simple($x, null, (String)null);
			}
			
			public TobjTrivial decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjTrivial.class);
				return new TobjTrivial();
			}
		}
		
		public int hashCode() {
			return getClass().hashCode();
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
	public static class TobjPrimitive implements OFace {
		public TobjPrimitive(String $s) {
			$dat = $s;
		}
		
		String	$dat;
		
		
		
		public static class Den implements Dencoder<EonCodec,EonObject,TobjPrimitive> {
			public EonObject encode(EonCodec $codec, TobjPrimitive $x) throws TranslationException {
				return $codec.simple($x, "dat", $x.$dat);
			}
			
			public TobjPrimitive decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjPrimitive.class);
				return new TobjPrimitive($x.getStringData());
			}
		}
		
		public int hashCode() {
			return 31 + ((this.$dat == null) ? 0 : this.$dat.hashCode());
		}
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TobjPrimitive other = (TobjPrimitive) obj;
			if (this.$dat == null) {
				if (other.$dat != null) return false;
			} else if (!this.$dat.equals(other.$dat)) return false;
			return true;
		}
	}
	
	
	
	public static class TobjComposite implements OFace {
		public TobjComposite(int $val, OFace $kid) {
			this.$val = $val;
			this.$kid = $kid;
		}
		
		int	$val;
		OFace	$kid;
		
		
		
		public static class DenNoMux implements Dencoder<EonCodec,EonObject,TobjComposite> {
			public EonObject encode(EonCodec $codec, TobjComposite $x) throws TranslationException {
				EonObject $eo = $codec.newObj();
				$eo.putKlass(TobjComposite.class);
				$eo.put("val", $x.$val);
				if ($x.$kid != null) $eo.put("bebe", $codec.encode($x.$kid));
				return $eo;
			}
			
			public TobjComposite decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjComposite.class);
				return new TobjComposite(
						$x.getInt("val"),
						$x.optObj("bebe") == null ? null : $codec.decode($x.getObj("bebe"), TobjPrimitive.class)	// note that this is NOT using polymorphism.  this works only for some values of $kid, but doesn't demand muxing be used in the codec.
				);
			}
		}
		
		
		
		public static class DenMux implements Dencoder<EonCodec,EonObject,TobjComposite> {
			public EonObject encode(EonCodec $codec, TobjComposite $x) throws TranslationException {
				EonObject $eo = $codec.newObj();
				$eo.putKlass(TobjComposite.class);
				$eo.put("val", $x.$val);
				if ($x.$kid != null) $eo.put("bebe", $codec.encode($x.$kid, OFace.class));	// this is another place you have to make dang sure you're demanding the polymorphic behavior you expect from muxing.
				return $eo;
			}
			
			public TobjComposite decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjComposite.class);
				return new TobjComposite(
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
			TobjComposite other = (TobjComposite) obj;
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
	public static class TobjFaceList implements OFace {
		public TobjFaceList(List<TobjPrimitive> $x) {
			$dat = $x;
		}
		
		List<TobjPrimitive>	$dat;
		
		
		
		public static class Den implements Dencoder<EonCodec,EonObject,TobjFaceList> {
			public EonObject encode(EonCodec $codec, TobjFaceList $x) throws TranslationException {
				return $codec.simple($x, null, $codec.encodeList($x.$dat));
			}
			
			public TobjFaceList decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjFaceList.class);
				return new TobjFaceList($codec.decodeList($x.getArrayData(), TobjPrimitive.class));
			}
		}
		
		
		
		public static class Dense implements Dencoder<EonCodec,EonObject,TobjFaceList> {
			public EonObject encode(EonCodec $codec, TobjFaceList $x) throws TranslationException {
				EonArray $ja = $codec.newArr();
				for (int $i = 0; $i < $x.$dat.size(); $i++)
					$ja.put($i, $x.$dat.get($i).$dat);
				return $codec.simple($x, null, $ja);
			}
			
			public TobjFaceList decode(EonCodec $codec, EonObject $x) throws TranslationException {
				$x.assertKlass(TobjFaceList.class);
				EonArray $ja = $x.getArrayData();
				List<TobjPrimitive> $ar = new ArrayList<TobjPrimitive>($ja.size());
				for (int $i = 0; $i < $ja.size(); $i++)
					$ar.add(new TobjPrimitive($ja.getString($i)));
				return new TobjFaceList($ar);
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
			TobjFaceList other = (TobjFaceList) obj;
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
