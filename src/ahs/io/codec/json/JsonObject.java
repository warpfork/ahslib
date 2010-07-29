package ahs.io.codec.json;

/*
 * Copyright (c) 2002 JSON.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * The Software shall be used for Good, not Evil.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A JsonObject is an unordered collection of name/value pairs. Its external form is a
 * string wrapped in curly braces with colons between the names and values, and commas
 * between the values and names. The internal form is an object having <code>get</code>
 * and <code>opt</code> methods for accessing the values by name, and <code>put</code>
 * methods for adding or replacing values by name. The values can be any of these types:
 * <code>Boolean</code>, <code>JsonArray</code>, <code>JsonObject</code>,
 * <code>Number</code>, <code>String</code>, or the <code>JsonObject.NULL</code> object. A
 * JsonObject constructor can be used to convert an external form JSON text into an
 * internal form whose values can be retrieved with the <code>get</code> and
 * <code>opt</code> methods, or to convert values into a JSON text using the
 * <code>put</code> and <code>toString</code> methods. A <code>get</code> method returns a
 * value if one can be found, and throws an exception if one cannot be found. An
 * <code>opt</code> method returns a default value instead of throwing an exception, and
 * so is useful for obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an object, which
 * you can cast or query for type. There are also typed <code>get</code> and
 * <code>opt</code> methods that do type checking and type coercion for you.
 * <p>
 * The <code>put</code> methods adds values to an object. For example,
 * 
 * <pre>
 * myString = new JsonObject().put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
 * </pre>
 * 
 * produces the string <code>{"JSON": "Hello, World"}</code>.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to the JSON
 * syntax rules. The constructors are more forgiving in the texts they will accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just before the
 * closing brace.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote or single
 * quote, and if they do not contain leading or trailing spaces, and if they do not
 * contain any of these characters: <code>{ } [ ] / \ : , = ; #</code> and if they do not
 * look like numbers and if they are not the reserved words <code>true</code>,
 * <code>false</code>, or <code>null</code>.</li>
 * <li>Keys can be followed by <code>=</code> or <code>=></code> as well as by
 * <code>:</code>.</li>
 * <li>Values can be followed by <code>;</code> <small>(semicolon)</small> as well as by
 * <code>,</code> <small>(comma)</small>.</li>
 * <li>Numbers may have the <code>0-</code> <small>(octal)</small> or <code>0x-</code>
 * <small>(hex)</small> prefix.</li>
 * </ul>
 * 
 * @author JSON.org
 * @version 2008-09-18
 */
public class JsonObject implements EonObject {
	
	/**
	 * JsonObject.NULL is equivalent to the value that JavaScript calls null, whilst
	 * Java's null is equivalent to the value that JavaScript calls undefined.
	 */
	private static final class Null {
		/**
		 * There is only intended to be a single instance of the NULL object, so
		 * the clone method returns itself.
		 * 
		 * @return NULL.
		 */
		protected final Object clone() {
			return this;
		}
		
		
		/**
		 * A Null object is equal to the null value and to itself.
		 * 
		 * @param object
		 *                An object to test for nullness.
		 * @return true if the object parameter is the JsonObject.NULL object or
		 *         null.
		 */
		public boolean equals(Object object) {
			return object == null || object == this;
		}
		
		
		/**
		 * Get the "null" string value.
		 * 
		 * @return The string "null".
		 */
		public String toString() {
			return "null";
		}
		public int hashCode() { return 0; }
	}
	
	
	/**
	 * The map where the JsonObject's properties are kept.
	 */
	private Map<String, Object>	map;
	

	/**
	 * It is sometimes more convenient and less ambiguous to have a <code>NULL</code>
	 * object than to use Java's <code>null</code> value.
	 * <code>JsonObject.NULL.equals(null)</code> returns <code>true</code>.
	 * <code>JsonObject.NULL.toString()</code> returns <code>"null"</code>.
	 */
	public static final Object	NULL	= new Null();
	
	
	/**
	 * Construct an empty JsonObject.
	 */
	public JsonObject() {
		this.map = new HashMap<String, Object>();
	}
	
	
	/**
	 * Construct a JsonObject from a subset of another JsonObject. An array of strings
	 * is used to identify the keys that should be copied. Missing keys are ignored.
	 * 
	 * @param jo
	 *                A JsonObject.
	 * @param names
	 *                An array of strings.
	 * @exception JsonException
	 *                    If a value is a non-finite number or if a name is
	 *                    duplicated.
	 */
	public JsonObject(JsonObject jo, String[] names) throws JsonException {
		this();
		for (int i = 0; i < names.length; i += 1) {
			putOnce(names[i], jo.opt(names[i]));
		}
	}
	
	
	/**
	 * Construct a JsonObject from a JSONTokener.
	 * 
	 * @param x
	 *                A JSONTokener object containing the source string.
	 * @throws JsonException
	 *                 If there is a syntax error in the source string or a duplicated
	 *                 key.
	 */
	public JsonObject(JsonTokener x) throws JsonException {
		this();
		char c;
		String key;
		
		if (x.nextClean() != '{') { throw x.syntaxError("A JsonObject text must begin with '{'"); }
		for (;;) {
			c = x.nextClean();
			switch (c) {
				case 0:
					throw x.syntaxError("A JsonObject text must end with '}'");
				case '}':
					return;
				default:
					x.back();
					key = x.nextValue().toString();
			}
			
			/*
			 * The key is followed by ':'. We will also tolerate '=' or '=>'.
			 */

			c = x.nextClean();
			if (c == '=') {
				if (x.next() != '>') {
					x.back();
				}
			} else if (c != ':') { throw x.syntaxError("Expected a ':' after a key"); }
			putOnce(key, x.nextValue());
			
			/*
			 * Pairs are separated by ','. We will also tolerate ';'.
			 */

			switch (x.nextClean()) {
				case ';':
				case ',':
					if (x.nextClean() == '}') { return; }
					x.back();
					break;
				case '}':
					return;
				default:
					throw x.syntaxError("Expected a ',' or '}'");
			}
		}
	}
	
	
	/**
	 * Construct a JsonObject from a Map.
	 * 
	 * @param map
	 *                A map object that can be used to initialize the contents of the
	 *                JsonObject.
	 */
	public JsonObject(Map<String, Object> map) {
		this.map = (map == null) ? new HashMap<String, Object>() : map;
	}
	
	/**
	 * Construct a JsonObject from a Map.
	 * 
	 * Note: Use this constructor when the map contains <key,bean>.
	 * 
	 * @param map
	 *                - A map with Key-Bean data.
	 * @param includeSuperClass
	 *                - Tell whether to include the super class properties.
	 */
	public JsonObject(Map<String, Object> map, boolean includeSuperClass) {
		this.map = new HashMap<String, Object>();
		if (map != null) {
			for (Iterator<Map.Entry<String,Object>> i = map.entrySet().iterator(); i.hasNext();) {
				Map.Entry<String,Object> e = i.next();
				this.map.put(e.getKey(), new JsonObject(e.getValue(), includeSuperClass));
			}
		}
	}
	
	
	/**
	 * Construct a JsonObject from an Object using bean getters. It reflects on all of
	 * the public methods of the object. For each of the methods with no parameters
	 * and a name starting with <code>"get"</code> or <code>"is"</code> followed by an
	 * uppercase letter, the method is invoked, and a key and the value returned from
	 * the getter method are put into the new JsonObject.
	 * 
	 * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
	 * prefix. If the second remaining character is not upper case, then the first
	 * character is converted to lower case.
	 * 
	 * For example, if an object has a method named <code>"getName"</code>, and if the
	 * result of calling <code>object.getName()</code> is <code>"Larry Fine"</code>,
	 * then the JsonObject will contain <code>"name": "Larry Fine"</code>.
	 * 
	 * @param bean
	 *                An object that has getter methods that should be used to make a
	 *                JsonObject.
	 */
	public JsonObject(Object bean) {
		this();
		populateInternalMap(bean, false);
	}
	
	
	/**
	 * Construct JsonObject from the given bean. This will also create JsonObject for
	 * all internal object (List, Map, Inner Objects) of the provided bean.
	 * 
	 * -- See Documentation of JsonObject(Object bean) also.
	 * 
	 * @param bean
	 *                An object that has getter methods that should be used to make a
	 *                JsonObject.
	 * @param includeSuperClass
	 *                - Tell whether to include the super class properties.
	 */
	public JsonObject(Object bean, boolean includeSuperClass) {
		this();
		populateInternalMap(bean, includeSuperClass);
	}
	
	private void populateInternalMap(Object bean, boolean includeSuperClass) {
		Class<?> klass = bean.getClass();
		
		// If klass.getSuperClass is System class then includeSuperClass = false;
		
		if (klass.getClassLoader() == null) {
			includeSuperClass = false;
		}
		
		Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
			try {
				Method method = methods[i];
				String name = method.getName();
				String key = "";
				if (name.startsWith("get")) {
					key = name.substring(3);
				} else if (name.startsWith("is")) {
					key = name.substring(2);
				}
				if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
					if (key.length() == 1) {
						key = key.toLowerCase();
					} else if (!Character.isUpperCase(key.charAt(1))) {
						key = key.substring(0, 1).toLowerCase() + key.substring(1);
					}
					
					Object result = method.invoke(bean, (Object[]) null);
					if (result == null) {
						map.put(key, NULL);
					} else if (result.getClass().isArray()) {
						map.put(key, new JsonArray(result, includeSuperClass));
					} else if (result instanceof Collection) { // List or Set
						map.put(key, new JsonArray((Collection<?>) result, includeSuperClass));
					} else if (result instanceof Map) {
						map.put(key, new JsonObject((Map<?,?>) result, includeSuperClass));
					} else if (isStandardProperty(result.getClass())) { // Primitives, String and Wrapper
						map.put(key, result);
					} else {
						if (result.getClass().getPackage().getName().startsWith("java") || result.getClass().getClassLoader() == null) {
							map.put(key, result.toString());
						} else { // User defined Objects
							map.put(key, new JsonObject(result, includeSuperClass));
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private boolean isStandardProperty(Class<?> clazz) {
		return clazz.isPrimitive() || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(Boolean.class);
	}
	
	/**
	 * Construct a JsonObject from an Object, using reflection to find the public
	 * members. The resulting JsonObject's keys will be the strings from the names
	 * array, and the values will be the field values associated with those keys in
	 * the object. If a key is not found or not visible, then it will not be copied
	 * into the new JsonObject.
	 * 
	 * @param object
	 *                An object that has fields that should be used to make a
	 *                JsonObject.
	 * @param names
	 *                An array of strings, the names of the fields to be obtained from
	 *                the object.
	 */
	public JsonObject(Object object, String names[]) {
		this();
		Class<?> c = object.getClass();
		for (int i = 0; i < names.length; i += 1) {
			String name = names[i];
			try {
				putOpt(name, c.getField(name).get(object));
			} catch (Exception e) {
				/* forget about it */
			}
		}
	}
	
	
	/**
	 * Construct a JsonObject from a source JSON text string. This is the most
	 * commonly used JsonObject constructor.
	 * 
	 * @param source
	 *                A string beginning with <code>{</code>&nbsp;<small>(left
	 *                brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *                brace)</small>.
	 * @exception JsonException
	 *                    If there is a syntax error in the source string or a
	 *                    duplicated key.
	 */
	public JsonObject(String source) throws JsonException {
		this(new JsonTokener(source));
	}


	
	public JsonObject(Object $class, String $name, JsonObject $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(String $class, String $name, JsonObject $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(Object $class, String $name, JsonArray $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(String $class, String $name, JsonArray $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(String $class, String $name, String $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(Object $class, String $name, String $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(String $class, String $name, byte[] $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JsonObject(Object $class, String $name, byte[] $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	
	public String getKlass() {
		return optString(Eon.MAGICWORD_CLASS,null);
	}

	public void assertKlass(Object $x) throws JsonException {
		assertKlass(Eon.getKlass($x));
	}
	public void assertKlass(Class<?> $x) throws JsonException {
		assertKlass(Eon.getKlass($x));
	}
	public void assertKlass(String $x) throws JsonException {
		String $klass = getKlass();
		if ($klass == null) throw new JsonException("Class of JsonObject is not declared.");
		if (!$x.equals($klass)) throw new JsonException("JsonObject class \""+$klass+"\" does not match desired class \""+$x+"\".");
	}
	
	public void putKlass(Object $x) {
		put(Eon.MAGICWORD_CLASS,Eon.getKlass($x));	
	}
	public void putKlass(Class<?> $x) {
		put(Eon.MAGICWORD_CLASS,Eon.getKlass($x));
	}
	public void putKlass(String $x) {
		put(Eon.MAGICWORD_CLASS,$x);
	}
	
	// i just didn't feel like converting these to Eon since I never use them anyway.
	// ...and really, I don't think it's the responsibility of the data structure itself to help you do things like this.
	// 
	///**
	// * Accumulate values under a key. It is similar to the put method except that if
	// * there is already an object stored under the key then a JsonArray is stored
	// * under the key to hold all of the accumulated values. If there is already a
	// * JsonArray, then the new value is appended to it. In contrast, the put method
	// * replaces the previous value.
	// * 
	// * @param key
	// *                A key string.
	// * @param value
	// *                An object to be accumulated under the key.
	// * @return this.
	// * @throws JSONException
	// *                 If the value is an invalid number or if the key is null.
	// * @throws UnencodableException 
	// */
	//public JsonObject accumulate(String key, Object value) throws JSONException, UnencodableException {
	//	testValidity(value);
	//	Object o = opt(key);
	//	if (o == null) {
	//		put(key, value instanceof JsonArray ? new JsonArray().put(value) : value);
	//	} else if (o instanceof JsonArray) {
	//		((JsonArray) o).put(value);
	//	} else {
	//		put(key, new JsonArray().put(o).put(value));
	//	}
	//	return this;
	//}
	//
	//
	///**
	// * Append values to the array under a key. If the key does not exist in the
	// * JsonObject, then the key is put in the JsonObject with its value being a
	// * JsonArray containing the value parameter. If the key was already associated
	// * with a JsonArray, then the value parameter is appended to it.
	// * 
	// * @param key
	// *                A key string.
	// * @param value
	// *                An object to be accumulated under the key.
	// * @return this.
	// * @throws JSONException
	// *                 If the key is null or if the current value associated with the
	// *                 key is not a JsonArray.
	// * @throws UnencodableException 
	// */
	//public JsonObject append(String key, Object value) throws JSONException, UnencodableException {
	//	testValidity(value);
	//	Object o = opt(key);
	//	if (o == null) {
	//		put(key, new JsonArray().put(value));
	//	} else if (o instanceof JsonArray) {
	//		put(key, ((JsonArray) o).put(value));
	//	} else {
	//		throw new JSONException("JsonObject[" + key + "] is not a JsonArray.");
	//	}
	//	return this;
	//}
	
	
	/**
	 * Produce a string from a double. The string "null" will be returned if the
	 * number is not finite.
	 * 
	 * @param d
	 *                A double.
	 * @return A String.
	 */
	static protected String doubleToString(double d) {
		if (Double.isInfinite(d) || Double.isNaN(d)) { return "null"; }
		
		// Shave off trailing zeros and decimal point, if possible.
		
		String s = Double.toString(d);
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}
	
	
	/**
	 * Get the value object associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return The object associated with the key.
	 * @throws JsonException
	 *                 if the key is not found.
	 */
	protected Object get(String key) throws JsonException {
		Object o = opt(key);
		if (o == null) { throw new JsonException("JsonObject[" + quote(key) + "] not found."); }
		return o;
	}
	
	public void putName(String $x) {
		put(Eon.MAGICWORD_NAME, $x);
	}
	
	public String getName() throws JsonException {
		return getString(Eon.MAGICWORD_NAME);
	}
	
	public void putData(EonObject $x) {
		if ($x.getClass() != JsonObject.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonObject other than JsonObject.");
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(EonArray $x) {
		if ($x.getClass() != JsonArray.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonArray other than EonArray.");
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(String $x) {
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(byte[] $x) {
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public JsonObject getData() throws JsonException {
		return getObj(Eon.MAGICWORD_DATA);
	}
	
	public JsonArray getArrayData() throws JsonException {
		return getArr(Eon.MAGICWORD_DATA);
	}
	
	public String getStringData() throws JsonException {
		return getString(Eon.MAGICWORD_DATA);
	}
	
	public byte[] getByteData() throws JsonException {
		return getBytes(Eon.MAGICWORD_DATA);
	}
	
	public void put(String $key, byte[] $val) {
		put($key, Base64.encode($val));
	}
	
	public byte[] getBytes(String $key) throws JsonException {
		return Base64.decode(getString($key));
	}
	
	public byte[] optBytes(String $key) {
		String $s = optString($key);
		return $s == null ? null : Base64.decode($s);
	}
	
	public byte[] optBytes(String $key, byte[] $default) {
		String $s = optString($key);
		if ($s == null) return $default;
		byte[] $try = Base64.decode($s);
		return $try == null ? $default : $try;
	}
	
	public void put(String $key, boolean $val) {
		put($key, new Boolean($val));
	}
	
	public void put(String $key, String $val) {
		put($key, (Object) $val);
	}
	
	public void put(String $key, EonObject $val) {
		if ($val.getClass() != JsonObject.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonObject other than JsonObject.");
		put($key, (Object) $val);
	}
	
	public void put(String $key, EonArray $val) {
		if ($val.getClass() != JsonArray.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonArray other than EonArray.");
		put($key, (Object) $val);
	}

	/**
	 * Get the boolean value associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return The truth.
	 * @throws JsonException
	 *                 if the value is not a Boolean or the String "true" or "false".
	 */
	public boolean getBoolean(String key) throws JsonException {
		Object o = get(key);
		if (o.equals(Boolean.FALSE) || (o instanceof String && ((String) o).equalsIgnoreCase("false"))) {
			return false;
		} else if (o.equals(Boolean.TRUE) || (o instanceof String && ((String) o).equalsIgnoreCase("true"))) { return true; }
		throw new JsonException("JsonObject[" + quote(key) + "] is not a Boolean.");
	}
	
	
	/**
	 * Get the double value associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return The numeric value.
	 * @throws JsonException
	 *                 if the key is not found or if the value is not a Number object
	 *                 and cannot be converted to a number.
	 */
	public double getDouble(String key) throws JsonException {
		Object o = get(key);
		try {
			return o instanceof Number ? ((Number) o).doubleValue() : Double.valueOf((String) o).doubleValue();
		} catch (Exception e) {
			throw new JsonException("JsonObject[" + quote(key) + "] is not a number.");
		}
	}
	
	
	/**
	 * Get the int value associated with a key. If the number value is too large for
	 * an int, it will be clipped.
	 * 
	 * @param key
	 *                A key string.
	 * @return The integer value.
	 * @throws JsonException
	 *                 if the key is not found or if the value cannot be converted to
	 *                 an integer.
	 */
	public int getInt(String key) throws JsonException {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).intValue() : (int) getDouble(key);
	}
	
	
	/**
	 * Get the JsonArray value associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return A JsonArray which is the value.
	 * @throws JsonException
	 *                 if the key is not found or if the value is not a JsonArray.
	 */
	public JsonArray getArr(String key) throws JsonException {
		Object o = get(key);
		if (o instanceof JsonArray) { return (JsonArray) o; }
		throw new JsonException("JsonObject[" + quote(key) + "] is not a JsonArray.");
	}
	
	
	/**
	 * Get the JsonObject value associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return A JsonObject which is the value.
	 * @throws JsonException
	 *                 if the key is not found or if the value is not a JsonObject.
	 */
	public JsonObject getObj(String key) throws JsonException {
		Object o = get(key);
		if (o instanceof JsonObject) { return (JsonObject) o; }
		throw new JsonException("JsonObject[" + quote(key) + "] is not a JsonObject.");
	}
	
	
	/**
	 * Get the long value associated with a key. If the number value is too long for a
	 * long, it will be clipped.
	 * 
	 * @param key
	 *                A key string.
	 * @return The long value.
	 * @throws JsonException
	 *                 if the key is not found or if the value cannot be converted to
	 *                 a long.
	 */
	public long getLong(String key) throws JsonException {
		Object o = get(key);
		return o instanceof Number ? ((Number) o).longValue() : (long) getDouble(key);
	}
	
	
	/**
	 * Get an array of field names from a JsonObject.
	 * 
	 * @return An array of field names, or null if there are no names.
	 */
	public static String[] getNames(JsonObject jo) {
		int length = jo.length();
		if (length == 0) { return null; }
		Iterator<String> i = jo.keys();
		String[] names = new String[length];
		int j = 0;
		while (i.hasNext()) {
			names[j] = (String) i.next();
			j += 1;
		}
		return names;
	}
	
	
	/**
	 * Get an array of field names from an Object.
	 * 
	 * @return An array of field names, or null if there are no names.
	 */
	protected static String[] getNames(Object object) {
		if (object == null) { return null; }
		Class<?> klass = object.getClass();
		Field[] fields = klass.getFields();
		int length = fields.length;
		if (length == 0) { return null; }
		String[] names = new String[length];
		for (int i = 0; i < length; i += 1) {
			names[i] = fields[i].getName();
		}
		return names;
	}
	
	
	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return A string which is the value.
	 * @throws JsonException
	 *                 if the key is not found.
	 */
	public String getString(String key) throws JsonException {
		return get(key).toString();
	}
	
	
	/**
	 * Determine if the JsonObject contains a specific key.
	 * 
	 * @param key
	 *                A key string.
	 * @return true if the key exists in the JsonObject.
	 */
	public boolean has(String key) {
		return this.map.containsKey(key);
	}
	
	
	/**
	 * Determine if the value associated with the key is null or if there is no value.
	 * 
	 * @param key
	 *                A key string.
	 * @return true if there is no value associated with the key or if the value is
	 *         the JsonObject.NULL object.
	 */
	public boolean isNull(String key) {
		return JsonObject.NULL.equals(opt(key));
	}
	
	
	/**
	 * Get an enumeration of the keys of the JsonObject.
	 * 
	 * @return An iterator of the keys.
	 */
	public Iterator<String> keys() {
		return this.map.keySet().iterator();
	}
	
	/**
	 * Get an enumeration of the values of the JsonObject.
	 * 
	 * @return An iterator of the values.
	 */
	protected Iterator<Object> values() {
		return this.map.values().iterator();
	}
	
	
	/**
	 * Get the number of keys stored in the JsonObject.
	 * 
	 * @return The number of keys in the JsonObject.
	 */
	public int length() {
		return this.map.size();
	}
	
	
	/**
	 * Produce a JsonArray containing the names of the elements of this JsonObject.
	 * 
	 * @return A JsonArray containing the key strings, or null if the JsonObject is
	 *         empty.
	 */
	public JsonArray names() {
		JsonArray ja = new JsonArray();
		Iterator<String> keys = keys();
		while (keys.hasNext()) {
			ja.put(keys.next());
		}
		return ja.length() == 0 ? null : ja;
	}
	
	/**
	 * Produce a string from a Number.
	 * 
	 * @param n
	 *                A Number
	 * @return A String.
	 * @throws UnencodableException
	 *                 If n is a non-finite number.
	 */
	static public String numberToString(Number n) throws UnencodableException {
		if (n == null) { throw new NullPointerException(); }
		testValidity(n);
		
		// Shave off trailing zeros and decimal point, if possible.
		
		String s = n.toString();
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}
	static private String numberToStringUnchecked(Number n) {
		if (n == null) { throw new NullPointerException(); }
		
		// Shave off trailing zeros and decimal point, if possible.
		
		String s = n.toString();
		if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}
	
	
	/**
	 * Get an optional value associated with a key.
	 * 
	 * @param key
	 *                A key string.
	 * @return An object which is the value, or null if there is no value.
	 */
	protected Object opt(String key) {
		return key == null ? null : this.map.get(key);
	}
	
	
	/**
	 * Get an optional boolean associated with a key. It returns the defaultValue if
	 * there is no such key, or if it is not a Boolean or the String "true" or "false"
	 * (case insensitive).
	 * 
	 * @param key
	 *                A key string.
	 * @param defaultValue
	 *                The default.
	 * @return The truth.
	 */
	public boolean optBoolean(String key, boolean defaultValue) {
		try {
			return getBoolean(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	
	/**
	 * Get an optional double associated with a key, or NaN if there is no such key or
	 * if its value is not a number. If the value is a string, an attempt will be made
	 * to evaluate it as a number.
	 * 
	 * @param key
	 *                A string which is the key.
	 * @return An object which is the value.
	 */
	public double optDouble(String key) {
		return optDouble(key, Double.NaN);
	}
	
	
	/**
	 * Get an optional double associated with a key, or the defaultValue if there is
	 * no such key or if its value is not a number. If the value is a string, an
	 * attempt will be made to evaluate it as a number.
	 * 
	 * @param key
	 *                A key string.
	 * @param defaultValue
	 *                The default.
	 * @return An object which is the value.
	 */
	public double optDouble(String key, double defaultValue) {
		try {
			Object o = opt(key);
			return o instanceof Number ? ((Number) o).doubleValue() : new Double((String) o).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	
	/**
	 * Get an optional int value associated with a key, or zero if there is no such
	 * key or if the value is not a number. If the value is a string, an attempt will
	 * be made to evaluate it as a number.
	 * 
	 * @param key
	 *                A key string.
	 * @return An object which is the value.
	 */
	public int optInt(String key) {
		return optInt(key, 0);
	}
	
	
	/**
	 * Get an optional int value associated with a key, or the default if there is no
	 * such key or if the value is not a number. If the value is a string, an attempt
	 * will be made to evaluate it as a number.
	 * 
	 * @param key
	 *                A key string.
	 * @param defaultValue
	 *                The default.
	 * @return An object which is the value.
	 */
	public int optInt(String key, int defaultValue) {
		try {
			return getInt(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	
	/**
	 * Get an optional JsonArray associated with a key. It returns null if there is no
	 * such key, or if its value is not a JsonArray.
	 * 
	 * @param key
	 *                A key string.
	 * @return A JsonArray which is the value.
	 */
	public JsonArray optArr(String key) {
		Object o = opt(key);
		return o instanceof JsonArray ? (JsonArray) o : null;
	}
	
	
	/**
	 * Get an optional JsonObject associated with a key. It returns null if there is
	 * no such key, or if its value is not a JsonObject.
	 * 
	 * @param key
	 *                A key string.
	 * @return A JsonObject which is the value.
	 */
	public JsonObject optObj(String key) {
		Object o = opt(key);
		return o instanceof JsonObject ? (JsonObject) o : null;
	}
	
	
	/**
	 * Get an optional long value associated with a key, or the default if there is no
	 * such key or if the value is not a number. If the value is a string, an attempt
	 * will be made to evaluate it as a number.
	 * 
	 * @param key
	 *                A key string.
	 * @param defaultValue
	 *                The default.
	 * @return An object which is the value.
	 */
	public long optLong(String key, long defaultValue) {
		try {
			return getLong(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Get an optional string associated with a key. It returns the defaultValue if
	 * there is no such key.
	 * 
	 * @param key
	 *                A key string.
	 * @param defaultValue
	 *                The default.
	 * @return A string which is the value.
	 */
	public String optString(String key, String defaultValue) {
		Object o = opt(key);
		return o != null ? o.toString() : defaultValue;
	}
	public String optString(String key) {
		return optString(key, null);
	}
	
	
	/**
	 * Put a key/double pair in the JsonObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A double which is the value.
	 * @throws UnencodableException
	 *                 If the number is invalid.
	 * @throws NullPointerException
	 *                 If the key is null.
	 */
	public void put(String key, double value) throws UnencodableException {
		putQuestionable(key, new Double(value));
	}
	
	
	/**
	 * Put a key/int pair in the JsonObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An int which is the value.
	 * @throws NullPointerException
	 *                 If the key is null.
	 */
	public void put(String key, int value) {
		put(key, new Integer(value));
	}
	
	
	/**
	 * Put a key/long pair in the JsonObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A long which is the value.
	 * @throws NullPointerException
	 *                 If the key is null.
	 */
	public void put(String key, long value) {
		put(key, new Long(value));
	}
	
	
	/**
	 * Put a key/value pair in the JsonObject. If the value is null, then the key will
	 * be removed from the JsonObject if it is present.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object which is the value. It should be of one of these
	 *                types: Boolean, Integer, JsonArray, JsonObject, Long,
	 *                String, null.
	 * @return this.
	 * @throws NullPointerException
	 *                 If the key is null.
	 */
	protected JsonObject put(String key, Object value) {
		if (key == null) { throw new NullPointerException("JSON cannot accept null key."); }
		if (value != null) {
			this.map.put(key, value);
		} else {
			remove(key);
		}
		return this;
	}
	
	
	/**
	 * Put a key/value pair in the JsonObject, checking numerical types for values not
	 * valid in JSON and throwing exceptions where necessary. If the value is null,
	 * then the key will be removed from the JsonObject if it is present.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object which is the value. It should be of one of these
	 *                types: Double, null.
	 * @return this.
	 * @throws UnencodableException
	 *                 If the value is non-finite number.
	 */
	protected JsonObject putQuestionable(String key, Object value) throws UnencodableException {
		if (key == null) { throw new NullPointerException("JSON cannot accept null key."); }
		if (value != null) {
			testValidity(value);
			this.map.put(key, value);
		} else {
			remove(key);
		}
		return this;
	}
	
	
	/**
	 * Put a key/value pair in the JsonObject, but only if the key and the value are
	 * both non-null, and only if there is not already a member with that name.
	 * 
	 * @param key
	 * @param value
	 * @return true if put a value; false if a value was already present.
	 * @throws JsonException
	 *                 if the key is a duplicate
	 */
	public boolean putOnce(String key, Object value) throws JsonException {
		if (key != null && value != null) {
			if (opt(key) != null) { throw new JsonException("Duplicate key \"" + key + "\""); }
			put(key, value);
		}
		return true;
	}
	
	
	/**
	 * Put a key/value pair in the JsonObject, but only if the key and the value are
	 * both non-null.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object which is the value. It should be of one of these
	 *                types: Boolean, Double, Integer, JsonArray, JsonObject, Long,
	 *                String, or the JsonObject.NULL object.
	 * @return this.
	 * @throws JsonException
	 *                 If the value is a non-finite number.
	 */
	protected JsonObject putOpt(String key, Object value) throws JsonException {
		if (key != null && value != null) {
			put(key, value);
		}
		return this;
	}
	
	
	/**
	 * Produce a string in double quotes with backslash sequences in all the right
	 * places. A backslash will be inserted within </, allowing JSON text to be
	 * delivered in HTML. In JSON text, a string cannot contain a control character or
	 * an unescaped quote or backslash.
	 * 
	 * @param string
	 *                A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) { return "\"\""; }
		
		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String t;
		
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					sb.append('\\');
					sb.append(c);
					break;
				case '/':
					if (b == '<') {
						sb.append('\\');
					}
					sb.append(c);
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
						t = "000" + Integer.toHexString(c);
						sb.append("\\u" + t.substring(t.length() - 4));
					} else {
						sb.append(c);
					}
			}
		}
		sb.append('"');
		return sb.toString();
	}
	
	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *                The name to be removed.
	 * @return The value that was associated with the name, or null if there was no
	 *         value.
	 */
	public Object remove(String key) {
		return this.map.remove(key);
	}
	
	/**
	 * Get an enumeration of the keys of the JsonObject. The keys will be sorted
	 * alphabetically.
	 * 
	 * @return An iterator of the keys.
	 */
	public Iterator<String> sortedKeys() {
		return new TreeSet<String>(this.map.keySet()).iterator();
	}
	
	/**
	 * Try to convert a string into a number, boolean, or null. If the string can't be
	 * converted, return the string.
	 * 
	 * @param s
	 *                A String.
	 * @return A simple JSON value.
	 */
	static public Object stringToValue(String s) {
		if (s.equals("")) { return s; }
		if (s.equalsIgnoreCase("true")) { return Boolean.TRUE; }
		if (s.equalsIgnoreCase("false")) { return Boolean.FALSE; }
		if (s.equalsIgnoreCase("null")) { return JsonObject.NULL; }
		
		/*
		 * If it might be a number, try converting it. We support the 0- and 0x- conventions. If a number cannot be produced, then the value will just be a string. Note that the 0-, 0x-, plus, and implied string conventions are non-standard. A JSON parser is free to accept non-JSON forms as long as it accepts all correct JSON forms.
		 */

		char b = s.charAt(0);
		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
			if (b == '0') {
				if (s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
					try {
						return new Integer(Integer.parseInt(s.substring(2), 16));
					} catch (Exception e) {
						/* Ignore the error */
					}
				} else {
					try {
						return new Integer(Integer.parseInt(s, 8));
					} catch (Exception e) {
						/* Ignore the error */
					}
				}
			}
			try {
				return new Integer(s);
			} catch (Exception e) {
				try {
					return new Long(s);
				} catch (Exception f) {
					try {
						return new Double(s);
					} catch (Exception g) {
						/* Ignore the error */
					}
				}
			}
		}
		return s;
	}
	
	
	/**
	 * Throw an exception if the object is an NaN or infinite number.
	 * 
	 * @param $o
	 *                The object to test.
	 * @throws UnencodableException
	 *                 If $o is a non-finite number.
	 */
	static boolean testValidity(Object $o) throws UnencodableException {
		if ($o != null) {
			if ($o instanceof Double) {
				if (((Double) $o).isInfinite() || ((Double) $o).isNaN()) { throw new UnencodableException("JSON does not allow non-finite numbers."); }
			} else if ($o instanceof Float) {
				if (((Float) $o).isInfinite() || ((Float) $o).isNaN()) { throw new UnencodableException("JSON does not allow non-finite numbers."); }
			}
		}
		return true;
	}
	
	
	/**
	 * Produce a JsonArray containing the values of the members of this JsonObject.
	 * 
	 * @param names
	 *                A JsonArray containing a list of key strings. This determines
	 *                the sequence of the values in the result.
	 * @return A JsonArray of values.
	 * @throws JsonException
	 *                 If any of the values are non-finite numbers.
	 */
	public JsonArray toJsonArray(JsonArray names) throws JsonException {
		if (names == null || names.length() == 0) { return null; }
		JsonArray ja = new JsonArray();
		for (int i = 0; i < names.length(); i += 1) {
			ja.put(this.opt(names.getString(i)));
		}
		return ja;
	}
	
	/**
	 * Make a JSON text of this JsonObject. For compactness, no whitespace is added.
	 * If this would not result in a syntactically correct JSON text, then null will
	 * be returned instead.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return a printable, displayable, portable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left brace)</small>
	 *         and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 */
	public String toString() {
		try {
			Iterator<String> keys = keys();
			StringBuffer sb = new StringBuffer("{");
			
			while (keys.hasNext()) {
				if (sb.length() > 1) {
					sb.append(',');
				}
				Object o = keys.next();
				sb.append(quote(o.toString()));
				sb.append(':');
				sb.append(valueToString(this.map.get(o)));
			}
			sb.append('}');
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Make a prettyprinted JSON text of this JsonObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *                The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation of the
	 *         object, beginning with <code>{</code>&nbsp;<small>(left brace)</small>
	 *         and ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 * @throws JsonException
	 *                 If the object contains an invalid number.
	 * @throws UnencodableException 
	 */
	public String toString(int indentFactor) throws JsonException, UnencodableException {
		if (indentFactor == -1) 
			return toString();
		else
			return toString(indentFactor, 0);
	}
	
	
	/**
	 * Make a prettyprinted JSON text of this JsonObject.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *                The number of spaces to add to each level of indentation.
	 * @param indent
	 *                The indentation of the top level.
	 * @return a printable, displayable, transmittable representation of the object,
	 *         beginning with <code>{</code>&nbsp;<small>(left brace)</small> and
	 *         ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 * @throws JsonException
	 *                 If the object contains an invalid number.
	 * @throws UnencodableException 
	 */
	String toString(int indentFactor, int indent) throws JsonException, UnencodableException {
		int j;
		int n = length();
		if (n == 0) { return "{}"; }
		Iterator<String> keys = sortedKeys();
		StringBuffer sb = new StringBuffer("{");
		int newindent = indent + indentFactor;
		Object o;
		if (n == 1) {
			o = keys.next();
			sb.append(quote(o.toString()));
			sb.append(": ");
			sb.append(valueToStringUnchecked(this.map.get(o), indentFactor, indent));
		} else {
			while (keys.hasNext()) {
				o = keys.next();
				if (sb.length() > 1) {
					sb.append(",\n");
				} else {
					sb.append('\n');
				}
				for (j = 0; j < newindent; j += 1) {
					sb.append(' ');
				}
				sb.append(quote(o.toString()));
				sb.append(": ");
				sb.append(valueToStringUnchecked(this.map.get(o), indentFactor, newindent));
			}
			if (sb.length() > 1) {
				sb.append('\n');
				for (j = 0; j < indent; j += 1) {
					sb.append(' ');
				}
			}
		}
		sb.append('}');
		return sb.toString();
	}
	
	
	/**
	 * Make a JSON text of an Object value. If the object has an value.toJSONString()
	 * method, then that method will be used to produce the JSON text. The method is
	 * required to produce a strictly conforming text. If the object does not contain
	 * a toJSONString method (which is the most common case), then a text will be
	 * produced by other means. If the value is an array or Collection, then a
	 * JsonArray will be made from it and its toJSONString method will be called. If
	 * the value is a MAP, then a JsonObject will be made from it and its toJSONString
	 * method will be called. Otherwise, the value's toString method will be called,
	 * and the result will be quoted.
	 * 
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param value
	 *                The value to be serialized.
	 * @return a printable, displayable, transmittable representation of the object,
	 *         beginning with <code>{</code>&nbsp;<small>(left brace)</small> and
	 *         ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 * @throws JsonException
	 *                 If the value is or contains an invalid number.
	 * @throws UnencodableException 
	 */
	static String valueToString(Object value) throws JsonException, UnencodableException {
		if (value == null || value.equals(null)) { return "null"; }
		if (value instanceof JsonString) {
			Object o;
			try {
				o = ((JsonString) value).toJSONString();
			} catch (Exception e) {
				throw new JsonException(e);
			}
			if (o instanceof String) { return (String) o; }
			throw new JsonException("Bad value from toJSONString: " + o);
		}
		if (value instanceof Number) { return numberToString((Number) value); }
		if (value instanceof Boolean || value instanceof JsonObject || value instanceof JsonArray) { return value.toString(); }
		if (value instanceof Map) { return new JsonObject((Map<?,?>) value).toString(); }
		if (value instanceof Collection) { return new JsonArray((Collection<?>) value).toString(); }
		if (value.getClass().isArray()) { return new JsonArray(value).toString(); }
		return quote(value.toString());
	}
	
	
	/**
	 * Make a prettyprinted JSON text of an object value.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @param value
	 *                The value to be serialized.
	 * @param indentFactor
	 *                The number of spaces to add to each level of indentation.
	 * @param indent
	 *                The indentation of the top level.
	 * @return a printable, displayable, transmittable representation of the object,
	 *         beginning with <code>{</code>&nbsp;<small>(left brace)</small> and
	 *         ending with <code>}</code>&nbsp;<small>(right brace)</small>.
	 * @throws JsonException
	 *                 If the object contains an invalid number.
	 * @throws UnencodableException 
	 */
	static String valueToString(Object value, int indentFactor, int indent) throws JsonException, UnencodableException {
		if (value == null || value.equals(null)) { return "null"; }
		try {
			if (value instanceof JsonString) {
				Object o = ((JsonString) value).toJSONString();
				if (o instanceof String) { return (String) o; }
			}
		} catch (Exception e) {
			/* forget about it */
		}
		if (value instanceof Number) { return numberToString((Number) value); }
		if (value instanceof Boolean) { return value.toString(); }
		if (value instanceof JsonObject) { return ((JsonObject) value).toString(indentFactor, indent); }
		if (value instanceof JsonArray) { return ((JsonArray) value).toString(indentFactor, indent); }
		if (value instanceof Map) { return new JsonObject((Map<?,?>) value).toString(indentFactor, indent); }
		if (value instanceof Collection) { return new JsonArray((Collection<?>) value).toString(indentFactor, indent); }
		if (value.getClass().isArray()) { return new JsonArray(value).toString(indentFactor, indent); }
		return quote(value.toString());
	}
	static String valueToStringUnchecked(Object value, int indentFactor, int indent) throws JsonException, UnencodableException {
		if (value == null || value.equals(null)) { return "null"; }
		try {
			if (value instanceof JsonString) {
				Object o = ((JsonString) value).toJSONString();
				if (o instanceof String) { return (String) o; }
			}
		} catch (Exception e) {
			/* forget about it */
		}
		if (value instanceof Number) { return numberToStringUnchecked((Number) value); }
		if (value instanceof Boolean) { return value.toString(); }
		if (value instanceof JsonObject) { return ((JsonObject) value).toString(indentFactor, indent); }
		if (value instanceof JsonArray) { return ((JsonArray) value).toString(indentFactor, indent); }
		if (value instanceof Map) { return new JsonObject((Map<?,?>) value).toString(indentFactor, indent); }
		if (value instanceof Collection) { return new JsonArray((Collection<?>) value).toString(indentFactor, indent); }
		if (value.getClass().isArray()) { return new JsonArray(value).toString(indentFactor, indent); }
		return quote(value.toString());
	}
	
	
	/**
	 * Write the contents of the JsonObject as JSON text to a writer. For compactness,
	 * no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return The writer.
	 * @throws JsonException
	 * @throws UnencodableException 
	 */
	public Writer write(Writer writer) throws JsonException, UnencodableException {
		try {
			boolean b = false;
			Iterator<String> keys = keys();
			writer.write('{');
			
			while (keys.hasNext()) {
				if (b) {
					writer.write(',');
				}
				Object k = keys.next();
				writer.write(quote(k.toString()));
				writer.write(':');
				Object v = this.map.get(k);
				if (v instanceof JsonObject) {
					((JsonObject) v).write(writer);
				} else if (v instanceof JsonArray) {
					((JsonArray) v).write(writer);
				} else {
					writer.write(valueToString(v));
				}
				b = true;
			}
			writer.write('}');
			return writer;
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	protected JsonObject uncheckedPut(String key, Object value) {
		this.map.put(key, value);
		return this;
	}
	
	public Set<Map.Entry<String,Object>> entrySet() {
		return map.entrySet();
	}
}
