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
	
	private static class Encable2 {
		public  @ENC	static final				String $	= "";
		public  @ENC(key="o")					String $public;
		private @ENC(key="x", value={ENC.DEFAULT,ENC.SELECTED})	String $private;
		
		public Encable2(String $public, String $private) {
			this.$public = $public;
			this.$private = $private;
		}
		public String getPublic()	{ return this.$public;	}
		public String getPrivate()	{ return this.$private;	}
	}
	
	private static class ReflectiveAnnotatedEncoder implements Encoder<JSONObject,Object> {
		public ReflectiveAnnotatedEncoder(String $selector) {
			this.$selector = $selector;
		}
		
		private String $selector;
		
		public JSONObject encode(Codec<JSONObject> $codec, Object $x) throws TranslationException {
			try {
				JSONObject $jo = new JSONObject();
				String $key;
				
				Class<?> $class = $x.getClass();
				
				//... this bit with the class name stuff needs significantly more work (and a separate annotation at the class level, i've decided).
				// we need to be able to specify multiple names and cases -- at the very least we should be able to choose to use the default, a specific, or stiffle for any selector.
				try {
					Field $cn = $class.getField("$");
					String $scn = (String) $cn.get($x);
					if ($scn.length() == 0)
						$jo.putKlass($x);
					else
						$jo.putKlass($scn);
				} catch (NoSuchFieldException $e) {
					$jo.putKlass($x);	// just use the default name as decided by the code
				}
				
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);	// noact?
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
			} catch (SecurityException $e) {
				throw new UnencodableException("reflection problem",$e);
			}
		}
	}
			
	public void testEncodeDefault() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder(ENC.DEFAULT));
		
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
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder(ENC.SELECTED));
		
		JSONObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(2, $v.length());
		assertEquals("Encable", $v.getKlass());
		assertEquals("priv", $v.get("$private"));
	}
	
	public void testEncodeToMagicKey() throws TranslationException {
		Encable2 $e = new Encable2("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable2.class, new ReflectiveAnnotatedEncoder(ENC.DEFAULT));
		
		JSONObject $v = $codec.encode($e);
		X.saye($v.toString());
		assertEquals(3, $v.length());
		assertEquals("Encable2", $v.getKlass());
		assertEquals("pub",  $v.get("o"));
		assertEquals("priv", $v.get("x"));
	}
}
