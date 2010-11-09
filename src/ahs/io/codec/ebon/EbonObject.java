package ahs.io.codec.ebon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.util.*;

import java.util.*;

public class EbonObject implements EonObject {
	public EbonObject() {
		$map = new HashMap<String,Object>();
	}
	
	private Map<String,Object> $map;
	
	public String getKlass() {
		return optString(Eon.MAGICWORD_CLASS,null);
	}
	
	public void assertKlass(Object $x) throws EbonException {
		assertKlass(Eon.getKlass($x));
	}
	public void assertKlass(Class<?> $x) throws EbonException {
		assertKlass(Eon.getKlass($x));
	}
	public void assertKlass(String $x) throws EbonException {
		String $klass = getKlass();
		if ($klass == null) throw new EbonException("Class of EbonObject is not declared.");
		if (!$x.equals($klass)) throw new EbonException("EbonObject class \""+$klass+"\" does not match desired class \""+$x+"\".");
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
	
	protected Object get(String $key) throws EbonException {
		Object $o = opt($key);
		if ($o == null) throw new EbonException("EbonObject[" + $key + "] not found.");
		return $o;
	}
	
	public void putName(String $x) {
		put(Eon.MAGICWORD_NAME, $x);
	}
	
	public String getName() throws EbonException {
		return getString(Eon.MAGICWORD_NAME);
	}
	
	public void putData(EonObject $x) {
		if ($x.getClass() != EbonObject.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonObject other than EbonObject.");
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(EonArray $x) {
		if ($x.getClass() != EbonArray.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonArray other than EonArray.");
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(String $x) {
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public void putData(byte[] $x) {
		put(Eon.MAGICWORD_DATA, $x);
	}
	
	public EbonObject getData() throws EbonException {
		return getObj(Eon.MAGICWORD_DATA);
	}
	
	public EbonArray getArrayData() throws EbonException {
		return getArr(Eon.MAGICWORD_DATA);
	}
	
	public String getStringData() throws EbonException {
		return getString(Eon.MAGICWORD_DATA);
	}
	
	public byte[] getByteData() throws EbonException {
		return getBytes(Eon.MAGICWORD_DATA);
	}
	
	public void put(String $key, boolean $val) {
		put($key, new Boolean($val));
	}
	
	public void put(String $key, String $val) {
		$map.put($key, (Object) $val);
	}
	
	public void put(String $key, EonObject $val) {
		if ($val.getClass() != EbonObject.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonObject other than EbonObject.");
		$map.put($key, (Object) $val);
	}
	
	public void put(String $key, EonArray $val) {
		if ($val.getClass() != EbonArray.class) throw new IllegalArgumentException("EonObject isn't willing to deal with nested EonArray other than EonArray.");
		$map.put($key, $val);
	}
	
	protected Object opt(String $key) {
		return $map.get($key);
	}
	
	public boolean has(String $key) {
		return (opt($key) != null);
	}
	
	public int size() {
		return $map.size();
	}
	
	public void put(String $key, byte[] $val) {
		$map.put($key, $val);
	}
	
	public byte[] getBytes(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof byte[]) {
			return (byte[]) $x;
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not a byte[].");
		}
	}
	
	public byte[] optBytes(String $key) {
		return optBytes($key, null);
	}
	
	public byte[] optBytes(String $key, byte[] $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof byte[])
			return (byte[]) $x;
		else
			return $default;
	}
	
	public boolean getBoolean(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof Boolean) {
			return ($x.equals(Boolean.TRUE));
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not a Boolean.");
		}
	}
	
	public boolean optBoolean(String $key, boolean $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof Boolean)
			return ($x.equals(Boolean.TRUE));
		else
			return $default;
	}
	
	public void put(String $key, double $val) {
		$map.put($key, Double.valueOf($val));
	}
	
	public double getDouble(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof Double) {
			return ((Double) $x).doubleValue();
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not a Double.");
		}
	}
	
	public double optDouble(String $key, double $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof Double)
			return ((Double) $x).doubleValue();
		else
			return $default;
	}
	
	public void put(String $key, int $val) {
		$map.put($key, Integer.valueOf($val));
	}
	
	public int getInt(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof Integer) {
			return ((Integer) $x).intValue();
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not an Integer.");
		}
	}
	
	public int optInt(String $key, int $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof Integer)
			return ((Integer) $x).intValue();
		else
			return $default;
	}
	
	public void put(String $key, long $val) {
		$map.put($key, Long.valueOf($val));
	}
	
	public long getLong(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof Long) {
			return ((Long) $x).longValue();
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not a Long.");
		}
	}
	
	public long optLong(String $key, long $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof Long)
			return ((Long) $x).longValue();
		else
			return $default;
	}
	
	public String getString(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof String) {
			return (String) $x;
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not a String.");
		}
	}
	
	public String optString(String $key) {
		return optString($key, null);
	}
	
	public String optString(String $key, String $default) {
		Object $x = opt($key);
		if ($x == null)
			return $default;
		else if ($x instanceof String)
			return (String) $x;
		else
			return $default;
	}
	
	public EbonObject getObj(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof EbonObject) {
			return (EbonObject) $x;
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not an EbonObject.");
		}
	}
	
	public EonObject optObj(String $key) {
		Object $x = opt($key);
		if ($x == null)
			return null;
		else if ($x instanceof EonObject)
			return (EonObject) $x;
		else
			return null;
	}
	
	public EbonArray getArr(String $key) throws EbonException {
		Object $x = get($key);
		if ($x instanceof EbonArray) {
			return (EbonArray) $x;
		} else {
			throw new EbonException("EbonObject[" + $key + "] is not an EbonArray.");
		}
	}
	
	public EbonArray optArr(String $key) {
		Object $x = opt($key);
		if ($x == null)
			return null;
		else if ($x instanceof EbonArray)
			return (EbonArray) $x;
		else
			return null;
	}
	
	
	
	public byte[] serialize() throws EbonException {
		//TODO:AHS: needs me an (efficiently) growing byte buffer nao plz
		//TODO:AHS: wants me an ordered map that performs more like a linked list than that TreeMap thang.
		ByteVector $bv = new ByteVector();
		$bv.push((byte)'o');
		$bv.push(Primitives.byteArrayFromInt($map.size()));
		for (Map.Entry<String,Object> $ent : $map.entrySet()) {
			String $key = $ent.getKey();
			Object $x = $ent.getValue();
			if ($x instanceof byte[]) {
				byte[] $y = (byte[]) $x;
				$bv.push((byte)'[');
				$bv.push(Primitives.byteArrayFromInt($y.length));
				$bv.push($y);
			} else if ($x instanceof Boolean) {
				$bv.push((byte)'b');
				if ($x.equals(Boolean.TRUE)) $bv.push((byte) 1);
				else $bv.push((byte) 0);
			} else if ($x instanceof Double) {
				$bv.push((byte)'d');
				;
			} else if ($x instanceof Integer) {
				$bv.push((byte)'i');
				$bv.push(Primitives.byteArrayFromInt(((Integer) $x).intValue()));
			} else if ($x instanceof Long) {
				$bv.push((byte)'l');
				;
			} else if ($x instanceof String) {
				byte[] $y = ((String) $x).getBytes(Strings.UTF_8);
				$bv.push((byte)'s');
				$bv.push(Primitives.byteArrayFromInt($y.length));
				$bv.push($y);
			} else if ($x instanceof EbonObject) {
				$bv.push((byte)'o');	//TODO:AHS: would LOVE to just inherit bits of the container from the recursion so we don't have to copy too hard
			} else if ($x instanceof EbonArray) {
				$bv.push((byte)'a');
				;
			}
		
		}
		return null;
	}
	
	public void deserialize(byte[] $bats) throws EbonException {
		//TODO
		
	}
}
