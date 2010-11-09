package ahs.io.codec.ebon;

import ahs.io.codec.eon.*;
import ahs.util.*;

import java.io.*;
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
		ByteArrayOutputStream $bah = new ByteArrayOutputStream(128);
		DataOutputStream $dou = new DataOutputStream($bah);
		serialize($dou);
		return $bah.toByteArray();	//FIXME: this is absurd.  i do NOT want to do a full copy here but the api is raping me.
	}
	
	/**
	 * Package-visible so EbonArray and EbonObject can play tag.
	 * @param $dou
	 * @throws EbonException
	 */
	void serialize(DataOutputStream $dou) throws EbonException {
		//SOMEDAY:AHS: wants me an ordered map that performs more like a linked list than that TreeMap thang -- i only ever want fifo traversal and a comparator for that is not my favorite idea.
		
		try {
			$dou.writeChar('o');
			$dou.writeInt($map.size());
			for (Map.Entry<String,Object> $ent : $map.entrySet()) {
				byte[] $k = $ent.getKey().getBytes(Strings.UTF_8);
				$dou.writeShort($k.length);
				$dou.write($k);
				
				Object $x = $ent.getValue();
				if ($x instanceof byte[]) {
					byte[] $y = (byte[]) $x;
					$dou.writeChar('[');
					$dou.writeInt($y.length);
					$dou.write($y);
				} else if ($x instanceof Boolean) {
					$dou.writeChar('b');
					$dou.writeBoolean($x.equals(Boolean.TRUE));
				} else if ($x instanceof Double) {
					$dou.writeChar('d');
					$dou.writeDouble((Double) $x);
				} else if ($x instanceof Integer) {
					$dou.writeChar('i');
					$dou.writeInt((Integer) $x);
				} else if ($x instanceof Long) {
					$dou.writeChar('l');
					$dou.writeLong((Long) $x);
				} else if ($x instanceof String) {
					// it might seem a little strange here to just blast on past the methods DataOutputStream provides us for strings.
					// however, we want the header to contain the length of the string _in_bytes_, so we can't use writeChars and have to do this hop-skip instead.
					// (writeUTF is also kinda gross because it uses a modified UTF-8 instead of the real deal (and also has a 32k limit).)
					byte[] $y = ((String) $x).getBytes(Strings.UTF_8);
					$dou.writeChar('s');
					$dou.writeInt($y.length);
					$dou.write($y);
				} else if ($x instanceof EbonObject) {
					((EbonObject) $x).serialize($dou);
				} else if ($x instanceof EbonArray) {
					((EbonArray) $x).serialize($dou);
				}
			}
		} catch (IOException $e) {
			// ought not happen.  we can't really get io exceptions from writing to an internal buffer we just declared...
			throw new EbonException($e);
		}
	}
	
	public void deserialize(byte[] $bats) throws EbonException {
		DataInputStream $din = new DataInputStream(new ByteArrayInputStream($bats));
		deserialize($din);
	}
	
	public void deserialize(DataInputStream $din) throws EbonException {
		try {
			if ('o' != $din.readChar()) throw new EbonException("An EbonObject serial must begin with 'o'.");
			final int $mapl = $din.readInt();
			int $len;	// temp bucket
			byte[] $bats;	// temp bucket
			char $switch;	// temp bucket
			String $key;	// self explanitory
			Object $win;	// self explanitory
			for (int $i = 0; $i < $mapl; $i++) {
				$len = $din.readShort();
				$bats = new byte[$len];
				$din.read($bats);
				$key = new String($bats, Strings.UTF_8);
				if (has($key)) throw new EbonException("Duplicate key \"" + $key + "\"");
				
				$switch = $din.readChar();
				switch ($switch) {
					case '[':
						$len = $din.readInt();
						$bats = new byte[$len];
						$din.read($bats);
						$win = $bats;
						break;
					case 'b':
						$win = $din.readBoolean();
						break;
					case 'd':
						$win = $din.readDouble();
						break;
					case 'i':
						$win = $din.readInt();
						break;
					case 'l':
						$win = $din.readLong();
						break;
					case 's':
						$len = $din.readInt();
						$bats = new byte[$len];
						$din.read($bats);
						$win = new String($bats, Strings.UTF_8);
						break;
					case 'o':
						$win = new EbonObject();
						((EbonObject)$win).deserialize($din);
						break;
					case 'a':
						$win = new EbonArray();
						((EbonArray)$win).deserialize($din);
						break;
					default:
						throw new EbonException("EbonObject does not recognize field type '"+$switch+"'.");
				}
				$map.put($key, $win);
			}
		} catch (EOFException $e) {
			throw new EbonException("Unexpected end of EbonObject.", $e);
		} catch (IOException $e) {
			// ought not happen.  we can't really get io exceptions from reading from an internal buffer we just declared...
			throw new EbonException($e);
		}
	}
}
