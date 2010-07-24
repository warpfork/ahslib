package ahs.test;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.json.*;
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
	
	
	
	@Encodable(value="name")
	private static class Encable2 {
		public  @ENC(key="o")					String $public;
		private @ENC(key="x", value={ENC.DEFAULT,ENC.SELECTED})	String $private;
		
		public Encable2(String $public, String $private) {
			this.$public = $public;
			this.$private = $private;
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
	}
	
	
	
	private static class ReflectiveAnnotatedEncoder<$T> implements Encoder<JSONObject,$T> {
		public ReflectiveAnnotatedEncoder(String $selector) {
			this.$selector = $selector;
		}
		
		private String $selector;
		
		public JSONObject encode(Codec<JSONObject> $codec, $T $x) throws TranslationException {
			try {
				JSONObject $jo = new JSONObject();
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
				
				// walk across fields and serialize the non-static annotated ones
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);
					X.saye("FIELD: "+$f);
					int $mod = $f.getModifiers();
					if (Modifier.isStatic($mod)) continue;
					ENC $enc = $f.getAnnotation(ENC.class);
					if ($enc != null) {
						X.saye("is annotated");
						if (Arr.contains($enc.value(), $selector)) {
							if ($enc.key().isEmpty())
								$key = $f.getName();
							else $key = $enc.key(); 
							
							$jo.put($key, $f.get($x));
						}
					} else {
						X.saye("is NOT annotated");
						X.saye(Arr.toString($f.getAnnotations()));
					}
				}
				
				return $jo;
			} catch (IllegalAccessException $e) {
				throw new UnencodableException("reflection problem",$e);
			}
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
	private static class ReflectiveAnnotatedDecoder<$T> implements Decoder<JSONObject,$T> {
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
		
		public $T decode(Codec<JSONObject> $codec, JSONObject $x) throws TranslationException {
			Class<Encable> $c = Encable.class;
			// oh my god at no point did we indicate what kind of get should be used here
			return null;
		}
		
	}
	
	public void testEncodeDefault() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder<Encable>(ENC.DEFAULT));
		
		JSONObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(3, $v.length());
		assertEquals("Encable", $v.getKlass());
		assertEquals("pub",  $v.get("$public"));
		assertEquals("priv", $v.get("$private"));
	}
	
	public void testEncodeSelected() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder<Encable>(ENC.SELECTED));
		
		JSONObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(2, $v.length());
		assertEquals("Encable", $v.getKlass());
		assertEquals("priv", $v.get("$private"));
	}
	
	public void testEncodeToMagicKey() throws TranslationException {
		Encable2 $e = new Encable2("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable2.class, new ReflectiveAnnotatedEncoder<Encable2>(ENC.DEFAULT));
		
		JSONObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(3, $v.length());
		assertEquals("name", $v.getKlass());
		assertEquals("pub",  $v.get("o"));
		assertEquals("priv", $v.get("x"));
	}
	
	public void testEncodeUnacceptable() throws TranslationException {
		Encable2 $e = new Encable2("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable2.class, new ReflectiveAnnotatedEncoder<Encable2>(ENC.SELECTED));
		
		try {
			JSONObject $v = $codec.encode($e);
			fail("this should have exploded.");
		} catch (UnencodableException $e1) {
			/* good */
		}
	}
	
}
