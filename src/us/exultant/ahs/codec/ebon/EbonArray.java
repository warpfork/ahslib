package us.exultant.ahs.codec.ebon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.*;
import us.exultant.ahs.codec.eon.*;

import java.io.*;
import java.util.*;

public class EbonArray implements EonArray {
	public EbonArray() {
		$arr = new ArrayList<Object>();
	}
	
	public EbonArray(int $capacity) {
		$arr = new ArrayList<Object>($capacity);
	}
	
	private List<Object> $arr;
	
	public int size() {
		return $arr.size();
	}
	
	protected Object opt(int $index) {
		return $arr.get($index);
	}
	
	protected Object get(int $index) throws EbonException {
		Object $o = opt($index);
		if ($o == null) throw new EbonException("EbonArray[" + $index + "] not found.");
		return $o;
	}
	
	public void put(int $index, byte[] $val) {
		$arr.add($index, $val);
	}
	
	public byte[] getBytes(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof byte[]) {
			return (byte[]) $x;
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not a byte[].");
		}
	}
	
	public byte[] optBytes(int $index) {
		return optBytes($index, null);
	}
	
	public byte[] optBytes(int $index, byte[] $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof byte[])
			return (byte[]) $x;
		else
			return $default;
	}
	
	public void put(int $index, boolean $val) {
		$arr.add($index, $val);
	}
	
	public boolean getBoolean(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof Boolean) {
			return ($x.equals(Boolean.TRUE));
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not a Boolean.");
		}
	}
	
	public boolean optBoolean(int $index, boolean $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof Boolean)
			return ($x.equals(Boolean.TRUE));
		else
			return $default;
	}

	public void put(int $index, double $val) {
		$arr.add($index, $val);
	}
	
	public double getDouble(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof Double) {
			return ((Double) $x).doubleValue();
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not a Double.");
		}
	}

	public double optDouble(int $index, double $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof Double)
			return ((Double) $x).doubleValue();
		else
			return $default;
	}

	public void put(int $index, int $val) {
		$arr.add($index, $val);
	}

	public int getInt(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof Integer) {
			return ((Integer) $x).intValue();
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not an Integer.");
		}
	}

	public int optInt(int $index, int $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof Integer)
			return ((Integer) $x).intValue();
		else
			return $default;
	}

	public void put(int $index, long $val)  {
		$arr.add($index, $val);
	}

	public long getLong(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof Long) {
			return ((Long) $x).longValue();
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not a Long.");
		}
	}

	public long optLong(int $index, long $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof Long)
			return ((Long) $x).longValue();
		else
			return $default;
	}

	public void put(int $index, String $val) {
		$arr.add($index, $val);
	}

	public String getString(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof String) {
			return (String) $x;
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not a String.");
		}
	}

	public String optString(int $index) {
		return optString($index, null);
	}

	public String optString(int $index, String $default) {
		Object $x = opt($index);
		if ($x == null)
			return $default;
		else if ($x instanceof String)
			return (String) $x;
		else
			return $default;
	}

	public void put(int $index, EonObject $val) {
		$arr.add($index, $val);
	}

	public EbonObject getObj(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof EbonObject) {
			return (EbonObject) $x;
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not an EbonObject.");
		}
	}

	public EbonObject optObj(int $index) {
		Object $x = opt($index);
		if ($x == null)
			return null;
		else if ($x instanceof EbonObject)
			return (EbonObject) $x;
		else
			return null;
	}

	public void put(int $index, EonArray $val) {
		$arr.add($index, $val);
	}

	public EbonArray getArr(int $index) throws EbonException {
		Object $x = get($index);
		if ($x instanceof EbonArray) {
			return (EbonArray) $x;
		} else {
			throw new EbonException("EbonArray[" + $index + "] is not an EbonArray.");
		}
	}

	public EbonArray optArr(int $index) {
		Object $x = opt($index);
		if ($x == null)
			return null;
		else if ($x instanceof EbonArray)
			return (EbonArray) $x;
		else
			return null;
	}
	
	
	
	public byte[] serialize() throws EbonException {
		Bah $bah = new Bah(128);
		DataOutputStream $dou = new DataOutputStream($bah);
		serialize($dou);
		return $bah.getByteArray();
	}
	
	/**
	 * Package-visible so EbonArray and EbonObject can play tag.
	 * @param $dou
	 * @throws EbonException
	 */
	void serialize(DataOutputStream $dou) throws EbonException {
		//SOMEDAY:AHS: wants me an ordered map that performs more like a linked list than that TreeMap thang -- i only ever want fifo traversal and a comparator for that is not my favorite idea.
		
		try {
			$dou.writeByte((byte)'a');
			final int $arrl = $arr.size();
			$dou.writeInt($arrl);
			for (int $i = 0; $i < $arrl; $i++) {
				Object $x = $arr.get($i);
				if ($x instanceof byte[]) {
					byte[] $y = (byte[]) $x;
					$dou.writeByte((byte)'[');
					$dou.writeInt($y.length);
					$dou.write($y);
				} else if ($x instanceof Boolean) {
					$dou.writeByte((byte)'b');
					$dou.writeBoolean($x.equals(Boolean.TRUE));
				} else if ($x instanceof Double) {
					$dou.writeByte((byte)'d');
					$dou.writeDouble((Double) $x);
				} else if ($x instanceof Integer) {
					$dou.writeByte((byte)'i');
					$dou.writeInt((Integer) $x);
				} else if ($x instanceof Long) {
					$dou.writeByte((byte)'l');
					$dou.writeLong((Long) $x);
				} else if ($x instanceof String) {
					// it might seem a little strange here to just blast on past the methods DataOutputStream provides us for strings.
					// however, we want the header to contain the length of the string _in_bytes_, so we can't use writeChars and have to do this hop-skip instead.
					// (writeUTF is also kinda gross because it uses a modified UTF-8 instead of the real deal (and also has a 32k limit).)
					byte[] $y = ((String) $x).getBytes(Strings.UTF_8);
					$dou.writeByte((byte)'s');
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
		byte $bat;
		try {
			$bat = $din.readByte();
		} catch (IOException $e) {
			throw new EbonException($e);
		}
		if ('a' != $bat) throw new EbonException("An EbonArray serial must begin with 'a'.");
		deserialize($din);
	}
	
	public void deserialize(DataInputStream $din) throws EbonException {
		try {
			final int $arrl = $din.readInt();
			int $len;	// temp bucket
			byte[] $bats;	// temp bucket
			byte $switch;	// temp bucket
			Object $win;	// self explanitory
			for (int $i = 0; $i < $arrl; $i++) {
				$switch = $din.readByte();
				switch ($switch) {
					case '[':
						$len = $din.readInt();
						if ($len > $din.available()) throw new EbonException("Invalid format; Length header specified a field to be longer than remaining data.");
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
						if ($len > $din.available()) throw new EbonException("Invalid format; Length header specified a field to be longer than remaining data.");
						$bats = new byte[$len];
						$din.read($bats);
						$win = new String($bats, Strings.UTF_8);	//XXX:AHS:EFFIC: it would be nice if there was a factory for strings that would let me read from DataInputStream directly without that intermediate byte array copy.
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
						throw new EbonException("EbonArray does not recognize field type '"+$switch+"'.");
				}
				$arr.add($win);
			}
		} catch (EOFException $e) {
			throw new EbonException("Unexpected end of EbonArray.", $e);
		} catch (IOException $e) {
			if ($e instanceof EbonException) throw (EbonException)$e;	// i hate this line so
			// we can't really get io exceptions from reading from an internal buffer we just declared...
			throw new EbonException($e);
		}
	}
}
