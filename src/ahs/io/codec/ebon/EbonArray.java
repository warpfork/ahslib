package ahs.io.codec.ebon;

import ahs.io.codec.eon.*;
import ahs.util.*;

import java.io.*;
import java.util.*;

public class EbonArray implements EonArray {
	
	private List<Object> $arr;
	
	
	
	
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
			$dou.writeChar('a');
			final int $arrl = $arr.size();
			$dou.writeInt($arrl);
			for (int $i = 0; $i < $arrl; $i++) {
				Object $x = $arr.get($i);
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
			if ('a' != $din.readChar()) throw new EbonException("An EbonArray serial must begin with 'a'.");
			final int $arrl = $din.readInt();
			int $len;	// temp bucket
			byte[] $bats;	// temp bucket
			char $switch;	// temp bucket
			Object $win;	// self explanitory
			for (int $i = 0; $i < $arrl; $i++) {
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
						throw new EbonException("EbonArray does not recognize field type '"+$switch+"'.");
				}
				$arr.add($win);
			}
		} catch (EOFException $e) {
			throw new EbonException("Unexpected end of EbonArray.", $e);
		} catch (IOException $e) {
			// ought not happen.  we can't really get io exceptions from reading from an internal buffer we just declared...
			throw new EbonException($e);
		}
	}
}
