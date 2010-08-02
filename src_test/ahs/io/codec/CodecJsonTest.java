package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.test.*;
import ahs.util.*;

import java.util.*;

public class CodecJsonTest extends CodecEonTest {
	public void testTrivial() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oa.class, new Oa.Den());
		
		Oa $x1 = new Oa();
		JsonObject $c = $jc.encode($x1);
		Oa $x2 = $jc.decode($c, Oa.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testTrivialSerial() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oa.class, new Oa.Den());
		
		Oa $x1 = new Oa();
		byte[] $c = $jc.serialize($x1);
		Oa $x2 = $jc.deserialize($c, Oa.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	/**
	 * tests basic en/decoding of an object with a single field.
	 */
	public void testBasic() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		
		Ob $x1 = new Ob("whip it");
		JsonObject $c = $jc.encode($x1);
		Ob $x2 = $jc.decode($c, Ob.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testBasicString() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		
		Ob $x1 = new Ob("whip it");
		JsonObject $c = $jc.encode($x1);
		
		assertEquals("{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it\"}", $c.toString());
	}
	
	
	

	/**
	 * tests the recursive stuff where an object contains a list that it hands off to
	 * codec and hopes for the best.
	 */
	public void testList() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Den());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		JsonObject $c = $jc.encode($x1);
		Oc $x2 = $jc.decode($c, Oc.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	/**
	 * same as previous recursive test, except the codec isn't initialized with all of
	 * the needed encoders or decoders.
	 */
	public void testListFailFromMissingDencoder() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oc.class, new Oc.Den());
		
		try {
			Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
			$jc.encode($toy);
			fail("Encoding should have failed.");
		} catch (TranslationException $e) {
			assertEquals("Encoding dispatch hook not found for ahs.io.codec.CodecEonTest$Ob",$e.getMessage());	
		}
	}
	
	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public void testListWithDenseDencoder() throws TranslationException {
		X.saye("");
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Dense());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		JsonObject $c = $jc.encode($x1);
		Oc $x2 = $jc.decode($c, Oc.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	/**
//	 * tests basic en/decoding of an object with a single field.
//	 */
//	public void testBasic() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Ob.class, new Ob.Den());
//		
//		String $str = $jc.encode(new Ob("whip it")).toString();
//		System.out.println($str);
//		assertEquals("{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it\"}",$str);
//		assertEquals(new Ob("whip it"),$jc.decode($jc.encode(new Ob("whip it")),Ob.class));
//	}
//	
//	/**
//	 * tests the recursive stuff where an object hands its own children to the codec and hopes for the best.
//	 */
//	public void testRecursive() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Ob.class, new Ob.Den());
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } )); 
//		String $str = $jc.encode($toy).toString();
//		System.out.println($str);
//		assertEquals("{\"#\":\"Oc\",\"%\":[{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"before the cream sits out too long\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"you must whip it\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it\"},{\"#\":\"Ob\",\"$\":\"dat\",\"%\":\"whip it good\"}]}",$str);
//		assertEquals($toy,$jc.decode($jc.encode($toy),Oc.class));
//	}
//	
//	/**
//	 * same as previous recursive test, except the codec isn't initialized with all of
//	 * the needed encoders or decoders.
//	 */
//	public void testRecursiveFailFromMissingDencoder() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		try {
//			Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
//			$jc.encode($toy);
//			fail("Encoding should have failed.");
//		} catch (TranslationException $e) {
//			assertEquals("Encoding dispatch hook not found for ahs.io.codec.CodecEonTest$Ob",$e.getMessage());	
//		}
//	}
//	
//	/**
//	 * demonstrates how use of a different encoder is easily possible (and can have
//	 * significant space savings in special cases over more generic approaches).
//	 */
//	public void testRecursiveWithDenseDencoder() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Ob.class, new Ob.Den());
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } )); 
//		String $str = $jc.encode($toy).toString();
//		System.out.println($str);
//		assertEquals("{\"#\":\"Oc\",\"%\":[\"before the cream sits out too long\",\"you must whip it\",\"whip it\",\"whip it good\"]}",$str);
//		assertEquals($toy,$jc.decode($jc.encode($toy),Oc.class));
//	}
//	
//	/**
//	 * title is self-explanitory.
//	 */
//	public void testRecursiveFailFromDecoderNotMatchingEncoder() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Ob.class, new Ob.Den());
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
//		EonObject $datenc = $jc.encode($toy);
//		X.say($datenc+"");
//		
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		// this is actually a spectacularly awkward test case, since the replacement decoder will actually use toString liberally to the point that it entirely corrupts data but won't throw exceptions.
//		//    ...but this is a problem of the specific Decoder, not of the Codec system itself.
//		assertFalse($toy.equals($jc.decode($datenc, Oc.class)));
//	}
//	
//	/**
//	 * title is self-explanitory.  Better than previous.
//	 */
//	public void testRecursiveFailFromDecoderNotMatchingEncoder2() throws TranslationException {
//		JsonCodec $jc = new JsonCodec();
//		$jc.putHook(Ob.class, new Ob.Den());
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		Oc $toy = new Oc(Arr.asList(new Ob[] { new Ob("whip it") }));
//		EonObject $datenc = $jc.encode($toy);
//		X.say($datenc+"");
//		
//		$jc.putHook(Oc.class, new Oc.Den());
//		
//		try {
//			$toy = $jc.decode($datenc, Oc.class);
//			fail("Decoding should have failed.");
//		} catch (TranslationException $e) {
//			assertEquals("Decoding failed for class ahs.io.codec.CodecEonTest$Oc.",$e.getMessage());
//		}
//	}
}
