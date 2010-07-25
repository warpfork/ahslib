package ahs.io.codec.json;

import ahs.io.codec.eon.*;
import ahs.util.*;

import java.util.Map;

/**
 * Overrides most of the bitchy methods in HarshJSONObject that throw exceptions whenever they
 * damn well please.
 * 
 * @author hash
 * 
 */
public class JSONObjectFuckery extends HarshJSONObject implements EonObject<JSONObject,JSONArray> {
	public String getKlass() {
		return optString(Eon.MAGICWORD_CLASS,null);
	}
	
	public static String getKlass(Object $x) {
		return getKlass($x.getClass());
	}
	public static String getKlass(Class<?> $c) {
		String[] $arrg = $c.getCanonicalName().split("\\Q.\\E");
		return $arrg[$arrg.length-1];
	}
	
	public void assertKlass(Object $x) throws JSONException {
		assertKlass(getKlass($x));
	}
	public void assertKlass(Class<?> $x) throws JSONException {
		String[] $arrg = $x.getCanonicalName().split("\\Q.\\E");
		assertKlass($arrg[$arrg.length-1]);
	}
	public void assertKlass(String $x) throws JSONException {
		String $klass = getKlass();
		if ($klass == null) throw new JSONException("Class of JSONObject is not declared.");
		if (!$x.equals($klass)) throw new JSONException("JSONObject class \""+$klass+"\" does not match desired class \""+$x+"\".");
	}
	
	public void putKlass(Object $x) {
		put(Eon.MAGICWORD_CLASS,getKlass($x));
	}
	public void putKlass(Class<?> $x) {
		put(Eon.MAGICWORD_CLASS,getKlass($x));
	}
	public void putKlass(String $x) {
		put(Eon.MAGICWORD_CLASS,$x);
	}
	
	public void putName(String $x) {
		put(Eon.MAGICWORD_NAME,$x);
	}
	public String getName() throws JSONException {
		return getString(Eon.MAGICWORD_NAME);
	}
	
	public void putData(JSONObject $x) {
		put(Eon.MAGICWORD_DATA,$x);
	}
	public void putData(JSONArray $x) {
		put(Eon.MAGICWORD_DATA,$x);
	}
	public void putData(String $x) {
		put(Eon.MAGICWORD_DATA,$x);
	}
	public void putData(byte[] $x) {
		put(Eon.MAGICWORD_DATA,$x);
	}
	public JSONObject getData() throws JSONException {
		return getJSONObject(Eon.MAGICWORD_DATA);
	}
	public JSONArray getArrayData() throws JSONException {
		return getJSONArray(Eon.MAGICWORD_DATA);
	}
	public String getStringData() throws JSONException {
		return getString(Eon.MAGICWORD_DATA);
	}
	public byte[] getByteData() throws JSONException {
		return getBytes(Eon.MAGICWORD_DATA);
	}
	
	public JSONObject(Object $class, String $name, JSONObject $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(String $class, String $name, JSONObject $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(Object $class, String $name, JSONArray $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(String $class, String $name, JSONArray $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(String $class, String $name, String $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(Object $class, String $name, String $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(String $class, String $name, byte[] $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	public JSONObject(Object $class, String $name, byte[] $data) {
		this();
		if ($class != null) putKlass($class);
		if ($name != null)  putName($name);
		if ($data != null)  putData($data);
	}
	
	public void put(String $key, byte[] $val) {
		put($key,Base64.encode($val));
	}
	public byte[] getBytes(String $key) throws JSONException {
		return Base64.decode(getString($key));
	}
	
	public JSONObject(Map<String,String> $map) {
		super();
		for (Map.Entry<String,String> $ent : $map.entrySet())
			put($ent.getKey(),$ent.getValue());
	}
	
	
	
	////////////////////////////////////////////////////////////////
	
	/**
	 * Construct an empty JSONObject.
	 */
	public JSONObject() {
		super();
	}
	
	/**
	 * Construct a JSONObject from a subset of another JSONObject. An array of strings
	 * is used to identify the keys that should be copied. Missing keys are ignored.
	 * 
	 * @param jo
	 *                A JSONObject.
	 * @param names
	 *                An array of strings.
	 * @exception JSONException
	 *                    If a value is a non-finite number or if a name is
	 *                    duplicated.
	 */
	public JSONObject(HarshJSONObject jo, String[] names) throws JSONException {
		super(jo,names);
	}
	
	/**
	 * Construct a JSONObject from a JSONTokener.
	 * 
	 * @param x
	 *                A JSONTokener object containing the source string.
	 * @throws JSONException
	 *                 If there is a syntax error in the source string or a duplicated
	 *                 key.
	 */
	public JSONObject(JSONTokener x) throws JSONException {
		super(x);
	}
	
	// THIS THING USES FUCKIN BEANS.
	// IF YOU WANT THAT SHIT, USE HARSHJSONOBJECT.
	//
//	/**
//	 * Construct a JSONObject from a Map.
//	 * 
//	 * @param map
//	 *                A map object that can be used to initialize the contents of the
//	 *                JSONObject.
//	 */
//	public JSONObject(Map<Object,Object> map) {
//		super(map);
//	}
	
	/**
	 * Construct a JSONObject from a Map.
	 * 
	 * Note: Use this constructor when the map contains <key,bean>.
	 * 
	 * @param map
	 *                - A map with Key-Bean data.
	 * @param includeSuperClass
	 *                - Tell whether to include the super class properties.
	 */
	public JSONObject(Map<?,?> map, boolean includeSuperClass) {
		super(map,includeSuperClass);
	}
	
	/**
	 * Construct JSONObject from the given bean. This will also create JSONObject for
	 * all internal object (List, Map, Inner Objects) of the provided bean.
	 * 
	 * -- See Documentation of JSONObject(Object bean) also.
	 * 
	 * @param bean
	 *                An object that has getter methods that should be used to make a
	 *                JSONObject.
	 * @param includeSuperClass
	 *                - Tell whether to include the super class properties.
	 */
	public JSONObject(Object bean, boolean includeSuperClass) {
		super(bean,includeSuperClass);
	}
	
	/**
	 * Construct a JSONObject from an Object, using reflection to find the public
	 * members. The resulting JSONObject's keys will be the strings from the names
	 * array, and the values will be the field values associated with those keys in
	 * the object. If a key is not found or not visible, then it will not be copied
	 * into the new JSONObject.
	 * 
	 * @param object
	 *                An object that has fields that should be used to make a
	 *                JSONObject.
	 * @param names
	 *                An array of strings, the names of the fields to be obtained from
	 *                the object.
	 */
	public JSONObject(Object object, String names[]) {
		super(object,names);
	}
	
	
	/**
	 * Construct a JSONObject from a source JSON text string. This is the most
	 * commonly used JSONObject constructor.
	 * 
	 * @param source
	 *                A string beginning with <code>{</code>&nbsp;<small>(left
	 *                brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *                brace)</small>.
	 * @exception JSONException
	 *                    If there is a syntax error in the source string or a
	 *                    duplicated key.
	 */
	public JSONObject(String source) throws JSONException {
		super(source);
	}
	
	
	/**
	 * Accumulate values under a key. It is similar to the put method except that if
	 * there is already an object stored under the key then a JSONArray is stored
	 * under the key to hold all of the accumulated values. If there is already a
	 * JSONArray, then the new value is appended to it. In contrast, the put method
	 * replaces the previous value.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object to be accumulated under the key.
	 * @return this.
	 */
	public HarshJSONObject accumulate(String key, Object value) {
		Object o = opt(key);
		if (o == null) {
			put(key, value instanceof JSONArray ? new JSONArray().put(value) : value);
		} else if (o instanceof JSONArray) {
			((JSONArray) o).put(value);
		} else {
			put(key, new JSONArray().put(o).put(value));
		}
		return this;
	}
	
	
	/**
	 * Append values to the array under a key. If the key does not exist in the
	 * JSONObject, then the key is put in the JSONObject with its value being a
	 * JSONArray containing the value parameter. If the key was already associated
	 * with a JSONArray, then the value parameter is appended to it.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object to be accumulated under the key.
	 * @return this.
	 * @throws JSONException
	 *                 If the current value associated with the
	 *                 key is not a JSONArray.
	 */
	public HarshJSONObject append(String key, Object value) throws JSONException {
		Object o = opt(key);
		if (o == null) {
			put(key, new JSONArray().put(value));
		} else if (o instanceof JSONArray) {
			put(key, ((JSONArray) o).put(value));
		} else {
			throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
		}
		return this;
	}
	
	
	
	/**
	 * Put a key/boolean pair in the JSONObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A boolean which is the value.
	 */
	public void put(String key, boolean value)  {
		put(key, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	
	/**
	 * Put a key/double pair in the JSONObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A double which is the value.
	 * @return this.
	 */
	public HarshJSONObject put(String key, double value) {
		put(key, new Double(value));
		return this;
	}
	
	
	/**
	 * Put a key/int pair in the JSONObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An int which is the value.
	 * @return this.
	 */
	public JSONObject put(String key, int value) {
		put(key, new Integer(value));
		return this;
	}
	
	
	/**
	 * Put a key/long pair in the JSONObject.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A long which is the value.
	 * @return this.
	 */
	public HarshJSONObject put(String key, long value) {
		put(key, new Long(value));
		return this;
	}
	
	
	/**
	 * Put a key/value pair in the JSONObject, where the value will be a JSONObject
	 * which is produced from a Map.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                A Map value.
	 * @return this.
	 */
	public HarshJSONObject put(String key, Map<Object,Object> value) {
		put(key, new HarshJSONObject(value));
		return this;
	}
	
	
	/**
	 * Put a key/value pair in the JSONObject. If the value is null, then the key will
	 * be removed from the JSONObject if it is present.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object which is the value. It should be of one of these
	 *                types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *                String, or the JSONObject.NULL object.
	 * @return this.
	 */
	public HarshJSONObject put(String key, Object value) {
		if (value != null) {
		//	if (testValidity(value))
			uncheckedPut(key,value);
		} else {
			remove(key);
		}
		return this;
	}
	
	
	/**
	 * Put a key/value pair in the JSONObject, but only if the key and the value are
	 * both non-null, and only if there is not already a member with that name.
	 * 
	 * @param key
	 * @param value
	 * @return true if put a value; false if a value was already present.
	 */
	public boolean putOnce(String key, Object value) {
		if (key != null && value != null) {
			if (opt(key) != null) return false;
			put(key, value);
		}
		return true;
	}
	
	
	/**
	 * Put a key/value pair in the JSONObject, but only if the key and the value are
	 * both non-null.
	 * 
	 * @param key
	 *                A key string.
	 * @param value
	 *                An object which is the value. It should be of one of these
	 *                types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
	 *                String, or the JSONObject.NULL object.
	 * @return this.
	 */
	public HarshJSONObject putOpt(String key, Object value) {
		if (key != null && value != null) {
			put(key, value);
		}
		return this;
	}
	
	/**
	 * Throw an exception if the object is an NaN or infinite number.
	 * 
	 * @param o
	 *                The object to test.
	 */
	public static boolean testValidity(Object o) {
		if (o == null)
			return false;
		if (o instanceof Double) {
			if (((Double) o).isInfinite() || ((Double) o).isNaN())
				return false;
		} else if (o instanceof Float) {
			if (((Float) o).isInfinite() || ((Float) o).isNaN())
				return false;
		}
		return true;
	}
}
