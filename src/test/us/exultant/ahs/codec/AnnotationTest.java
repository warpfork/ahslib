/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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
import us.exultant.ahs.test.*;
import java.util.*;


public class AnnotationTest extends TestCase {
	public static void main(String... $args) { new AnnotationTest().run(); }
	
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasicEncodeDefault());
		$tests.add(new TestBasicEncodeSelected());
		$tests.add(new TestEncodeFieldLabeled());
		$tests.add(new TestEncodeFailsWhenNoHook());
		$tests.add(new TestEncodeNullString());
		$tests.add(new TestEncodeNullByteArray());
		$tests.add(new TestDecode());
		$tests.add(new TestNestedEncode());
		$tests.add(new TestNestedDecode());
		return $tests;
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//  Classes to test serializing upon
	////////////////////////////////////////////////////////////////
	
	@Encodable(styles={Enc.DEFAULT, Enc.SELECTED})
	private static class Encable {
		public  @Enc
			String $public;
		private @Enc(selected={Enc.DEFAULT,Enc.SELECTED})
			String $private;
		
		/** just for reflective instantiability */
		private Encable(Encodable $x) {}
		
		public Encable(String $public, String $private) {
			this.$public = $public;
			this.$private = $private;
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
	}
	
	@Encodable(value="classname")
	private static class Encable2 {
		public  @Enc("o")
			String $public;
		
		private @Enc(value="x", selected={Enc.DEFAULT,Enc.SELECTED})
			String $private;
		
		private @Enc("b")
			byte[] $bees;
		
		public Encable2(String $public, String $private, String $bees) {
			this.$public = $public;
			this.$private = $private;
			this.$bees = Base64.decode($bees);
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
		public byte[] getBees()		{ return this.$bees;	}
	}

	@Encodable(all_fields=true)
	private static class Big {
		public Little	$lil;
		
		/** just for reflective instantiability */
		private Big(Encodable $x) {}
		
		public Big(Little $lil) {
			this.$lil = $lil;
		}
		public Little getLil()	{ return this.$lil;	}
	}
	
	@Encodable
	private static class Little {
		public @Enc	String	$str;
		
		/** just for reflective instantiability */
		private Little(Encodable $x) {}
		
		public Little(String $str) {
			this.$str = $str;
		}
		public String getStr()	{ return this.$str;	}
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////
	//  Test definitions
	////////////////////////////////////////////////////////////////
	
	private class TestBasicEncodeDefault extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable $e = new Encable("pub","priv");
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable.class, new EonRAE<Encable>(Encable.class, Enc.DEFAULT));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(3, $v.size());
			assertEquals("Encable", $v.getKlass());
			assertEquals("pub",  $v.getString("$public"));
			assertEquals("priv", $v.getString("$private"));
		}
	}
	
	
	
	private class TestBasicEncodeSelected extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable $e = new Encable("pub","priv");
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable.class, new EonRAE<Encable>(Encable.class, Enc.SELECTED));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(2, $v.size());
			assertEquals("Encable", $v.getKlass());
			assertEquals("priv", $v.getString("$private"));
		}
	}
	
	
	
	private class TestEncodeFieldLabeled extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable2 $e = new Encable2("pub","priv","ABBA");
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable2.class, new EonRAE<Encable2>(Encable2.class, Enc.DEFAULT));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(4, $v.size());
			assertEquals("classname", $v.getKlass());
			assertEquals("pub",  $v.getString("o"));
			assertEquals("priv", $v.getString("x"));
			assertEquals(Base64.decode("ABBA"), $v.getBytes("b"));
		}
	}
	
	
	
	private class TestEncodeFailsWhenNoHook extends TestCase.Unit {
		@SuppressWarnings("unchecked")
		public Class<UnencodableException> expectExceptionType() { return UnencodableException.class; }
		
		public void call() throws TranslationException {
			Encable2 $e = new Encable2("pub","priv","ABBA");
			
			EonCodec $codec = new JsonCodec();
			
			EonRAE<Encable2> $rae = new EonRAE<Encable2>(Encable2.class, Enc.SELECTED);
		}
	}
	
	
	
	private class TestEncodeNullString extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable2 $e = new Encable2("pub",null,"ABBA");
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable2.class, new EonRAE<Encable2>(Encable2.class, Enc.DEFAULT));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(3, $v.size());
			assertEquals("classname", $v.getKlass());
			assertEquals("pub",  $v.getString("o"));
			assertEquals(null, $v.optString("x"));
			assertEquals(Base64.decode("ABBA"), $v.getBytes("b"));
		}
	}
	
	
	
	private class TestEncodeNullByteArray extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable2 $e = new Encable2("pub","priv",null);
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable2.class, new EonRAE<Encable2>(Encable2.class, Enc.DEFAULT));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(3, $v.size());
			assertEquals("classname", $v.getKlass());
			assertEquals("pub",  $v.getString("o"));
			assertEquals("priv", $v.getString("x"));
			assertEquals(null, $v.optBytes("b"));
		}
	}
	
	
	
	private class TestDecode extends TestCase.Unit {
		public void call() throws TranslationException {
			Encable $e = new Encable("pub","priv");
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Encable.class, new EonRAE<Encable>(Encable.class, Enc.DEFAULT));
			$codec.putHook(Encable.class, new EonRAD<Encable>(Encable.class, Enc.DEFAULT));
			
			EonObject $v = $codec.encode($e);
			$log.trace("serialized: "+$v.toString());
			
			Encable $z = $codec.decode($v, Encable.class);
			assertEquals("pub",  $z.getPublic());
			assertEquals("priv", $z.getPrivate());
		}
	}
	
	
	
	private class TestNestedEncode extends TestCase.Unit {
		public void call() throws TranslationException {
			Big $b = new Big(new Little("asdf"));
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Big.class, new EonRAE<Big>(Big.class));
			$codec.putHook(Little.class, new EonRAE<Little>(Little.class));
			
			EonObject $v = $codec.encode($b);
			$log.trace("serialized: "+$v.toString());
			
			assertEquals(2, $v.size());
			assertEquals("Big", $v.getKlass());
			
			EonObject $v2 = $v.getObj("$lil");
			assertEquals(2, $v.size());
			assertEquals("Little", $v2.getKlass());
			assertEquals("asdf", $v2.getString("$str"));
		}
	}
	
	
	
	private class TestNestedDecode extends TestCase.Unit {
		public void call() throws TranslationException {
			Big $b = new Big(new Little("asdf"));
			
			EonCodec $codec = new JsonCodec();
			$codec.putHook(Big.class, new EonRAE<Big>(Big.class));
			$codec.putHook(Little.class, new EonRAE<Little>(Little.class));
			$codec.putHook(Big.class, new EonRAD<Big>(Big.class));
			$codec.putHook(Little.class, new EonRAD<Little>(Little.class));
			
			EonObject $v = $codec.encode($b);
			$log.trace("serialized: "+$v.toString());
			
			Big $z = $codec.decode($v, Big.class);
			assertEquals("asdf", $z.getLil().getStr());
		}
	}
}
