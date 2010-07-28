package ahs.test;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.util.*;

import java.lang.annotation.*;
import java.lang.reflect.*;


public class AnnotationTest extends TestCase {
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ENC {
		String[] value() default { DEFAULT };
		String key() default "";	// I wish this could just be null.
		public static final String DEFAULT = "$";
		public static final String SELECTED = "!";
	}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Encodable {
		String value() default DEFAULT;	// this specifies the value that the encoder should use for a classname if the encoder has no other more specific instructions.
						// it's completely legit for an encoder implementation to wayside this flag completely
		String[] styles() default { DEFAULT };
		public static final String DEFAULT = "$";
		public static final String NONE = "!";
		
		boolean all_fields() default false;
	}
	
	
	
	@Encodable(styles={ENC.DEFAULT, ENC.SELECTED})
	private static class Encable {
		public  @ENC					String $public;
		private @ENC({ENC.DEFAULT,ENC.SELECTED})	String $private;
		
		public Encable(String $public, String $private) {
			this.$public = $public;
			this.$private = $private;
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
	}
	
	
	
	@Encodable(value="classname")
	private static class Encable2 {
		public  @ENC(key="o")					String $public;
		private @ENC(key="x", value={ENC.DEFAULT,ENC.SELECTED})	String $private;
		private @ENC(key="b")					byte[] $bees;
		
		public Encable2(String $public, String $private, String $bees) {
			this.$public = $public;
			this.$private = $private;
			this.$bees = Base64.decode($bees);
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
		public byte[] getBees()		{ return this.$bees;	}
	}
	
	
	
	/**
	 * Always respects the value given for classname encoding preferences as given in
	 * the Encodable annotation.
	 * 
	 * @param <$T>
	 */
	private static class ReflectiveAnnotatedEncoder<$T> implements Encoder<JsonObject,$T> {
		public ReflectiveAnnotatedEncoder(String $selector) {
			this.$selector = $selector;
		}
		
		private String $selector;
		
		public JsonObject encode(Codec<JsonObject> $codec, $T $x) throws TranslationException {
			try {
				JsonObject $jo = new JsonObject();
				String $key;
				
				// pick out and put in the semblance of a class name we want
				// also, check if that class will allow itself to be encoded like this
				Class<?> $class = $x.getClass();
				Encodable $cenc = $class.getAnnotation(Encodable.class);
				if ($cenc == null)
					throw new UnencodableException("Class to be encoded must be annotated with the @Encodable interface.");
				else {
					if (!Arr.contains($cenc.styles(), $selector))
						throw new UnencodableException("Class to be encoded must be annotated to accept the style that this Encoder is configured for (selected=\""+$selector+"\", accept="+Arr.toString($cenc.styles())+").");
					
					$key = $cenc.value();
					if ($key.equals(Encodable.NONE))
						; /* nothing */
					else if ($key.equals(Encodable.DEFAULT))
						$jo.putKlass($x);
					else
						$jo.putKlass($key);
				}

				// walk across fields and serialize the non-static ones
				if ($cenc.all_fields()) {	// all of them, regardless of whether that particular field is annotated
					for (Field $f : $class.getDeclaredFields()) {
						$f.setAccessible(true);
						int $mod = $f.getModifiers();
						if (Modifier.isStatic($mod)) continue;
						ENC $anno = $f.getAnnotation(ENC.class);
						if ($anno != null) {
							if ($anno.key().isEmpty())
								$key = $f.getName();
							else $key = $anno.key(); 
							
							putField($codec, $jo, $key, $f, $x);
						} else {
							putField($codec, $jo, $f.getName(), $f, $x);
						}
					}
				} else {	// only annotated fields matching the selector
					for (Field $f : $class.getDeclaredFields()) {
						$f.setAccessible(true);
						int $mod = $f.getModifiers();
						if (Modifier.isStatic($mod)) continue;
						ENC $anno = $f.getAnnotation(ENC.class);
						if ($anno != null) {
							if (Arr.contains($anno.value(), $selector)) {
								if ($anno.key().isEmpty())
									$key = $f.getName();
								else $key = $anno.key(); 

								putField($codec, $jo, $key, $f, $x);
							}
						}
					}
				}
				
				return $jo;
			} catch (IllegalAccessException $e) {
				throw new UnencodableException("reflection problem",$e);
			}
		}
		
		private void putField(Codec<JsonObject> $codec, EonObject $eo, String $key, Field $f, $T $x) throws TranslationException, IllegalAccessException {
			Class<?> $typo = $f.getType();
			// i wish you could do a switch on anything that acts like a pointer
			if ($typo == byte[].class)
				$eo.put($key, (byte[])$f.get($x));
			else if ($typo == double[].class)
				;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
			else if ($typo == int[].class)
				;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
			else if ($typo == long[].class)
				;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
			//else if ($typo == short[].class)	// just don't do this.  ew.  so ineffic and wrong.
			//	$eo.put($key, (SATAN)$value);
			else if ($typo == boolean.class)
				$eo.put($key, $f.getBoolean($x));
			else if ($typo == double.class)
				$eo.put($key, $f.getDouble($x));
			else if ($typo == int.class)
				$eo.put($key, $f.getInt($x));
			else if ($typo == long.class)
				$eo.put($key, $f.getLong($x));
			else if ($typo == String.class)
				$eo.put($key, (String)$f.get($x));
			// i suppose we could check here if the value is already an EonObject or EonArray, but in practice who would ever do that?
			else
				$eo.put($key, $codec.encode($f.get($x)));
		}
	}
	
	/**
	 * <p>
	 * Note that it's impossible for this class to provide any sort of validation that
	 * the object it returns obeys any sort of invariants, since it operates entirely
	 * through mindless reflection instead of any controlled pattern of constructors
	 * or factory methods. In particular, it's trivially possible for the encoded form
	 * to have been modified to, for example, exclude fields (which will likely result
	 * in the decoded object having unexpected null values), or modify values to
	 * invalid combinations.
	 * </p>
	 * 
	 * <p>
	 * It's also impossible to use one instance of this class a decoder for multiple
	 * classes, unfortunately -- note the constructor.
	 * </p>
	 */
	private static class ReflectiveAnnotatedDecoder<$T> implements Decoder<JsonObject,$T> {
		/**
		 * <p>
		 * This constructor is awkward and somewhat redundant-sounding, but
		 * unfortunately there's no other way to get a reference to the Class<?>
		 * object for the generic type, and that reference is needed at runtime
		 * for critical reflection operations.
		 * </p>
		 * 
		 * @throws UnencodableException
		 *                 if the class is not annotated with the Encodable
		 *                 interface.
		 */
		public ReflectiveAnnotatedDecoder(Class<$T> $class) throws UnencodableException {
			this.$class = $class;
			
			// check if the class will allow itself to be dencoded like this
			Encodable $cenc = $class.getAnnotation(Encodable.class);
			if ($cenc == null)
				throw new UnencodableException("Class to be decoded must be annotated with the @Encodable interface.");
		}
		
		private Class<$T> $class;
		
		public $T decode(Codec<JsonObject> $codec, JsonObject $jo) throws TranslationException {
			String $key;
			
			//$class.
			
			// also, check if that class should have a name including in it's encoding
			// make assertions for sanity if it does
			Encodable $cenc = $class.getAnnotation(Encodable.class);
			
			$key = $cenc.value();
			if ($key.equals(Encodable.NONE))
				; /* no checks */
			else if ($key.equals(Encodable.DEFAULT))
				$jo.assertKlass($class);
			else
				$jo.assertKlass($key);
			
			// we're going to have FUN with reflection to figure out decoding types
			// use the isPrimitive method as a presort, and go to string compares after that (hopefully intern is an option)
			return null;
		}
		
	}
	
	public void testEncodeDefault() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JsonObject> $codec = new CodecImpl<JsonObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder<Encable>(ENC.DEFAULT));
		
		JsonObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(3, $v.length());
		assertEquals("Encable", $v.getKlass());
		assertEquals("pub",  $v.getString("$public"));
		assertEquals("priv", $v.getString("$private"));
	}
	
	public void testEncodeSelected() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JsonObject> $codec = new CodecImpl<JsonObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder<Encable>(ENC.SELECTED));
		
		JsonObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(2, $v.length());
		assertEquals("Encable", $v.getKlass());
		assertEquals("priv", $v.getString("$private"));
	}
	
	public void testEncodeToMagicKey() throws TranslationException {
		Encable2 $e = new Encable2("pub","priv","ABBA");
		
		Codec<JsonObject> $codec = new CodecImpl<JsonObject>();
		$codec.putHook(Encable2.class, new ReflectiveAnnotatedEncoder<Encable2>(ENC.DEFAULT));
		
		JsonObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(4, $v.length());
		assertEquals("classname", $v.getKlass());
		assertEquals("pub",  $v.getString("o"));
		assertEquals("priv", $v.getString("x"));
		assertEquals(Base64.decode("ABBA"), $v.getBytes("b"));
	}
	
	public void testEncodeUnacceptable() throws TranslationException {
		Encable2 $e = new Encable2("pub","priv","ABBA");
		
		Codec<JsonObject> $codec = new CodecImpl<JsonObject>();
		$codec.putHook(Encable2.class, new ReflectiveAnnotatedEncoder<Encable2>(ENC.SELECTED));
		
		try {
			JsonObject $v = $codec.encode($e);
			fail("this should have exploded.");
		} catch (UnencodableException $e1) {
			/* good */
		}
	}
	
}
