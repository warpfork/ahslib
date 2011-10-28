/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.eon.*;
import us.exultant.ahs.codec.json.*;

public class CodecJsonTest extends CodecEonTest {
	public CodecJsonTest() {
		super(new JsonCodec());
	}
	
	// methods below this is out of date and test odd things like the order of strings which aren't necessary actually required to be that consistent.  i should probably just throw them away.
	
	public void testTrivial() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oe.class, new Oe.Den());
		
		Oe $x1 = new Oe();
		JsonObject $c = $jc.encode($x1);
		Oe $x2 = $jc.decode($c, Oe.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testTrivialSerial() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Oe.class, new Oe.Den());
		
		Oe $x1 = new Oe();
		byte[] $c = $jc.serialize($x1);
		Oe $x2 = $jc.deserialize($c, Oe.class);
		
		assertEquals($x1, $x2);
	}
	
	
	
	/**
	 * tests basic en/decoding of an object with a single field.
	 */
	public void testBasic() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(O1p.class, new O1p.Den());
		
		O1p $x1 = new O1p("whip it");
		JsonObject $c = $jc.encode($x1);
		O1p $x2 = $jc.decode($c, O1p.class);

		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	public void testBasicString() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(O1p.class, new O1p.Den());
		
		O1p $x1 = new O1p("whip it");
		JsonObject $c = $jc.encode($x1);
		
		assertEquals("{\"#\":\"O1p\",\"$\":\"dat\",\"%\":\"whip it\"}", $c.toString());
	}
	
	
	

	/**
	 * tests the recursive stuff where an object contains a list that it hands off to
	 * codec and hopes for the best.
	 */
	public void testList() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(O1p.class, new O1p.Den());
		$jc.putHook(Ol.class, new Ol.Den());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		JsonObject $c = $jc.encode($x1);
		Ol $x2 = $jc.decode($c, Ol.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	/**
	 * same as previous recursive test, except the codec isn't initialized with all of
	 * the needed encoders or decoders.
	 */
	public void testListFailFromMissingDencoder() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(Ol.class, new Ol.Den());
		
		try {
			Ol $toy = new Ol(Arr.asList(new O1p[] { new O1p("whip it") }));
			$jc.encode($toy);
			fail("Encoding should have failed.");
		} catch (TranslationException $e) {
			assertEquals("Encoding dispatch hook not found for ahs.codec.CodecEonTest$O1p",$e.getMessage());	
		}
	}
	
	/**
	 * demonstrates how use of a different encoder is easily possible (and can have
	 * significant space savings in special cases over more generic approaches).
	 */
	public void testListWithDenseDencoder() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		$jc.putHook(O1p.class, new O1p.Den());
		$jc.putHook(Ol.class, new Ol.Dense());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		JsonObject $c = $jc.encode($x1);
		Ol $x2 = $jc.decode($c, Ol.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	
	
	
	public void testMuxing() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($jc, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Ol.class, new Ol.Den());
		
		Ol $x1 = new Ol(Arr.asList(new O1p[] { new O1p("before the cream sits out too long"), new O1p("you must whip it"), new O1p("whip it"), new O1p("whip it good") } ));
		JsonObject $c = $jc.encode($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		OFace $x2 = $jc.decode($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
	public void testMuxing2() throws TranslationException {
		JsonCodec $jc = new JsonCodec();
		EonDecodingMux<OFace> $mux = new EonDecodingMux<OFace>($jc, OFace.class);
		$mux.enroll(O1p.class, new O1p.Den());
		$mux.enroll(Ol.class, new Ol.Den());
		
		O1p $x1 = new O1p("whip it");
		JsonObject $c = $jc.encode($x1, OFace.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		O1p $x2 = (O1p)$jc.decode($c, OFace.class);
		
		assertEquals($x1, $x2);
	}
}
