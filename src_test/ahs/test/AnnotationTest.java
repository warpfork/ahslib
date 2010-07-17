package ahs.test;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.json.*;
import ahs.util.*;

import java.lang.reflect.*;


public class AnnotationTest extends TestCase {
	public static @interface ENC {
		String[] value() default { DEFAULT };
		public static final String DEFAULT = "$";
	}
	
	private static class Encable {
		public  @ENC			String $public;
		private @ENC({ENC.DEFAULT,"!"})	String $private;
		
		public Encable(String $public, String $private) {
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
			JSONObject $jo = new JSONObject();
			$jo.putKlass($x);

			Class<?> $class = $x.getClass();
			for (Field $f : $class.getFields()) {
				ENC $enc = $f.getAnnotation(ENC.class);
				if ($enc != null) {
					try {
						$jo.put($f.getName(), $f.get($x));
					} catch (IllegalArgumentException $e) {
						$e.printStackTrace();
					} catch (IllegalAccessException $e) {
						$e.printStackTrace();
					}
				}
			}
//			$jo.put("p", $codec.encode($x.$pse));
//			$jo.put("n", $codec.encode($x.$nth));
//			$jo.put("k", Codec_Json.encodeList($codec, $x.$pkr));
			return $jo;
		}
	}
	
	public void testMe() throws TranslationException {
		Encable $e = new Encable("pub","priv");
		
		Codec<JSONObject> $codec = new CodecImpl<JSONObject>();
		$codec.putHook(Encable.class, new ReflectiveAnnotatedEncoder(ENC.DEFAULT));
		
		X.saye($codec.encode($e).toString());
	}
}
