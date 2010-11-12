package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.ebon.*;
import ahs.test.*;
import ahs.util.*;

import java.util.*;

public class CodecEbonTestHeavy extends CodecEonTest {
	public void setUp() throws Exception {
		super.setUp();
		X.saye("");
	}
	
	/**
	 * tests the recursive stuff where an object contains a list that it hands off to
	 * codec and hopes for the best.
	 */
	public void testList() throws TranslationException {
		EbonCodec $jc = new EbonCodec();
		$jc.putHook(Ob.class, new Ob.Den());
		$jc.putHook(Oc.class, new Oc.Den());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		EbonObject $c = $jc.encode($x1);
		Oc $x2 = $jc.decode($c, Oc.class);
		
		X.saye($c.toString());
		
		assertEquals($x1, $x2);
	}
	public void testMuxing() throws TranslationException {
		EbonCodec $jc = new EbonCodec();
		EonDecodingMux<Ox> $mux = new EonDecodingMux<Ox>($jc, Ox.class);
		$mux.enroll(Ob.class, new Ob.Den());
		$mux.enroll(Oc.class, new Oc.Den());
		
		Oc $x1 = new Oc(Arr.asList(new Ob[] { new Ob("before the cream sits out too long"), new Ob("you must whip it"), new Ob("whip it"), new Ob("whip it good") } ));
		EbonObject $c = $jc.encode($x1, Ox.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		Ox $x2 = $jc.decode($c, Ox.class);
		
		assertEquals($x1, $x2);
	}
	public void testMuxing2() throws TranslationException {
		EbonCodec $jc = new EbonCodec();
		EonDecodingMux<Ox> $mux = new EonDecodingMux<Ox>($jc, Ox.class);
		$mux.enroll(Ob.class, new Ob.Den());
		$mux.enroll(Oc.class, new Oc.Den());
		
		Ob $x1 = new Ob("whip it");
		EbonObject $c = $jc.encode($x1, Ox.class);	// you NEED this class reference here!  if you don't have it, you don't get the polymorphic behavior!
		X.saye($c.toString());	// mind the placement of this toString... for this mux, it's different if you put it after the decode!
		Ob $x2 = (Ob)$jc.decode($c, Ox.class);
		
		assertEquals($x1, $x2);
	}
	
	/**
	 * This should take a few minutes. It's meant for watching heap usage with
	 * jconsole.
	 */
	public void testHard() throws TranslationException {
		for (int $i = 0; $i < 1000000; $i++) {
			testList();
			testMuxing();
			testMuxing2();
		}
	}
}
