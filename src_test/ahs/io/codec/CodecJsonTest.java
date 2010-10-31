package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.test.*;
import ahs.util.*;

import java.util.*;

public class CodecJsonTest extends CodecEonTest {
	public void setUp() throws Exception {
		super.setUp();
		X.saye("");
	}
	
	public void testTrivial() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oa.class, new Oa.Den());
		
		Oa $x1 = new Oa();
		JsonObject $c = $jc.encode($x1);
		Oa $x2 = $jc.decode($c, Oa.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testTrivialSerial() throws TranslationException {
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
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		
		Ob $x1 = new Ob("whip it");
		JsonObject $c = $jc.encode($x1);
		Ob $x2 = $jc.decode($c, Ob.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testBasicString() throws TranslationException {
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
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Dense());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		JsonObject $c = $jc.encode($x1);
		Oc $x2 = $jc.decode($c, Oc.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	
	
	public void testMuxing() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		EonDecodingMux<Ox> $mux = new EonDecodingMux<Ox>($jc, Ox.class);
		$mux.enroll(Ob.class, new Ob.Den());
		$mux.enroll(Oc.class, new Oc.Den());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		JsonObject $c = $jc.encode($x1, Ox.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		Ox $x2 = $jc.decode($c, Ox.class);
		
		assertEquals($x1, $x2);
	}
	public void testMuxing2() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		EonDecodingMux<Ox> $mux = new EonDecodingMux<Ox>($jc, Ox.class);
		$mux.enroll(Ob.class, new Ob.Den());
		$mux.enroll(Oc.class, new Oc.Den());
		
		Ob $x1 = new Ob("whip it");
		JsonObject $c = $jc.encode($x1, Ox.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		Ob $x2 = (Ob)$jc.decode($c, Ox.class);
		
		assertEquals($x1, $x2);
	}
}
