package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.json.*;
import ahs.test.*;
import ahs.util.*;

import java.util.*;

public class CodecJsonTest extends TestCase {
	/**
	 * tests basic en/decoding of an object with a single field.
	 */
	public void testBasic() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		
		String $str = $jc.encode(new Ob("whip it")).toString();
		System.out.println($str);
		assertEquals("{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it\"}",$str);
		assertEquals(new Ob("whip it"),$jc.decode($jc.encode(new Ob("whip it")),Ob.class));
	}
	
	/**
	 * tests the recursive stuff where an object hands its own children to the codec and hopes for the best.
	 */
	public void testRecursive() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Den());
		
		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } )); 
		String $str = $jc.encode($toy).toString();
		System.out.println($str);
		assertEquals("{\"#\":\"Oc\",\"%\":[{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"before the cream sits out too long\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"you must whip it\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it good\"}]}",$str);
		assertEquals($toy,$jc.decode($jc.encode($toy),Oc.class));
	}
	
	/**
	 * same as previous recursive test, except the codec isn't initialized with all of
	 * the needed encoders or decoders.
	 */
	public void testRecursiveFailFromMissingDencoder() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oc.class, new Oc.Den());
		
		try {
			Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
			$jc.encode($toy);
			fail("Encoding should have failed.");
		} catch (TranslationException $e) {
			assertEquals("Encoding dispatch hook not found for ahs.test.CodecJsonTest$Ob",$e.getMessage());	
		}
	}
	
	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public void testRecursiveWithDenseDencoder() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Dense());
		
		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } )); 
		String $str = $jc.encode($toy).toString();
		System.out.println($str);
		assertEquals("{\"#\":\"Oc\",\"%\":[\"before the cream sits out too long\",\"you must whip it\",\"whip it\",\"whip it good\"]}",$str);
		assertEquals($toy,$jc.decode($jc.encode($toy),Oc.class));
	}
	
	/**
	 * title is self-explanitory.
	 */
	public void testRecursiveFailFromDecoderNotMatchingEncoder() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Den());
		
		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
		JSONObject $datenc = $jc.encode($toy);
		X.say($datenc+"");
		
		$jc.putHook(Oc.class, new Oc.Dense());
		
		// this is actually a spectacularly awkward test case, since the replacement decoder will actually use toString liberally to the point that it entirely corrupts data but won't throw exceptions.
		//    ...but this is a problem of the specific Decoder, not of the Codec system itself.
		assertFalse($toy.equals($jc.decode($datenc, Oc.class)));
	}
	
	/**
	 * title is self-explanitory.  Better than previous.
	 */
	public void testRecursiveFailFromDecoderNotMatchingEncoder2() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Dense());
		
		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
		JSONObject $datenc = $jc.encode($toy);
		X.say($datenc+"");
		
		$jc.putHook(Oc.class, new Oc.Den());
		
		try {
			$toy = $jc.decode($datenc, Oc.class);
			fail("Decoding should have failed.");
		} catch (TranslationException $e) {
			assertEquals("Decoding failed for class ahs.test.CodecJsonTest$Oc.",$e.getMessage());
		}
	}
	
	
	
	
	////////////////////////////////////////////////////////////////
	
	public static class Ob {
		public Ob(String $s) { $dat = $s; }
		
		String $dat;
		
		public static class Den implements Dencoder<JSONObject,Ob> {
			public JSONObject encode(Codec<JSONObject> $codec, Ob $x) throws TranslationException {
				return new JSONObject($x,"dat",$x.$dat);
			}
			
			public Ob decode(Codec<JSONObject> $codec, JSONObject $x) throws TranslationException {
				$x.assertKlass(Ob.class);
				return new Ob($x.getStringData());
			}
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
		
		public int hashCode() { return 0; }
	}
	
	////////////////////////////////////////////////////////////////
	
	public static class Oc {
		public Oc(List<Ob> $x) { $dat = $x; }
		
		List<Ob> $dat;
		
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
		
		public static class Den implements Dencoder<JSONObject,Oc> {
			public JSONObject encode(Codec<JSONObject> $codec, Oc $x) throws TranslationException {
				JSONArray $ja = new JSONArray();
				for (int $i = 0; $i < $x.$dat.size(); $i++)
					$ja.put($codec.encode($x.$dat.get($i)));
				return new JSONObject($x, null, $ja);
			}
			

			public Oc decode(Codec<JSONObject> $codec, JSONObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				JSONArray $ja = $x.getArrayData();
				List<Ob> $ar = new ArrayList<Ob>($ja.length());
				for (int $i = 0; $i < $ja.length(); $i++)
					$ar.add($codec.decode($ja.getJSONObject($i), Ob.class));
				return new Oc($ar);
			}
		}
		
		public static class Dense implements Dencoder<JSONObject,Oc> {
			public JSONObject encode(Codec<JSONObject> $codec, Oc $x) throws TranslationException {
				JSONArray $ja = new JSONArray();
				for (int $i = 0; $i < $x.$dat.size(); $i++)
					$ja.put($x.$dat.get($i).$dat);
				return new JSONObject($x, null, $ja);
			}
			
			public Oc decode(Codec<JSONObject> $codec, JSONObject $x) throws TranslationException {
				$x.assertKlass(Oc.class);
				JSONArray $ja = $x.getArrayData();
				List<Ob> $ar = new ArrayList<Ob>($ja.length());
				for (int $i = 0; $i < $ja.length(); $i++)
					$ar.add(new Ob($ja.getString($i)));
				return new Oc($ar);
			}
		}
	}
}
