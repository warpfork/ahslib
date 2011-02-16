package ahs.util;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.util.Arr;

import java.util.*;

/**
 * This never wraps; always clones.  Also, exposes -- hard.
 * 
 * @author hash
 *
 */
public class ByteVector implements Comparable<ByteVector> {
	/**
	 * NEVER INITIALIZES THE DATA FIELD. Use with caution and only if for some
	 * absolutely insane reason you insist that it's going to be a good idea to insert
	 * a reference to a byte array manually from outside of this class (this is a way
	 * for efficiency-obsessed folks to shove a byte array in without cloning).
	 */
	public ByteVector() {}
	
	/**
	 * Makes a blank.
	 */
	public ByteVector(int $nbytes) {
		$d = new byte[$nbytes];
	}
	
	/**
	 * Fills a new ByteVector with the requested number of random bytes.
	 * 
	 * @param $nbytes
	 *                number of bytes desired
	 * @param $r
	 *                source of randomness
	 */
	public ByteVector(int $nbytes, Random $r) {
		$d = new byte[$nbytes];
		randomize($r);
	}
	
	/**
	 * Makes a byte vector from the utf-8 encoded string.
	 * 
	 * @param $s
	 */
	public ByteVector(String $s) {
		$d = $s.getBytes(Strings.UTF_8);
	}
	
	/**
	 * @param $b array to glean bytes from
	 * @param $offset offset from start
	 * @param $len number of bytes to get
	 */
	public ByteVector(byte[] $b, int $offset, int $len) {
		$d = new byte[$len];
		System.arraycopy($b, $offset, $d, 0, $len);
	}
	
	/**
	 * Clones.
	 */
	public ByteVector(byte[] $b) {
		$d = new byte[$b.length];
		System.arraycopy($b, 0, $d, 0, $b.length);
	}
	
	/**
	 * Clones.
	 */
	public ByteVector(byte[]... $bs) {
		$d = Arr.cat($bs);
	}
	
	/**
	 * Clones.
	 */
	public ByteVector(BitVector $bv) {
		$d = $bv.toByteArray();
	}
	
	/**
	 * Clones.
	 */
	public ByteVector(ByteVector $bv) {
		this($bv.$d);
	}
	
	public byte[]	$d;
	
	/* BEGIN EON CODEC BLOCK */
	public static final Encoder<EonCodec,EonObject,ByteVector> ENCODER;
	public static final Decoder<EonCodec,EonObject,ByteVector> DECODER;
	static { EonDencoder $t = new EonDencoder(); ENCODER = $t; DECODER = $t; }
	public static class EonDencoder implements Dencoder<EonCodec,EonObject,ByteVector> {
		public EonObject encode(EonCodec $codec, ByteVector $x) throws TranslationException {
			return $codec.simple("ByV", null, $x.$d);
		}
		public ByteVector decode(EonCodec $codec, EonObject $x) throws TranslationException {
			$x.assertKlass("ByV");
			return new ByteVector($x.getByteData());
		}
	}
	/* END EON CODEC BLOCK */
	
	// pad		-- extend to size
	// chop		-- limit to size
	// shift	-- shift one off the beginning
	// unshift	-- prepend one to the beginning
	// pop		-- pop one off the end
	// push		-- push one onto the end
	// slide	-- unshift and pop equal amounts
	// rslide	-- reverse slide (push and shift equal amounts)
	
	public void fit(int $size) {
		int $delta = $size - $d.length;
		if ($delta > 0) {
			byte[] $old = $d;
			int $olen = $old.length;
			$d = new byte[$size];
			System.arraycopy($old, 0, $d, 0, $olen);
			$old = new byte[$delta];
			new Random().nextBytes($old);
			System.arraycopy($old, 0, $d, $olen, $delta);
		} else if ($delta < 0) {
			byte[] $old = $d;
			$d = new byte[$size];
			System.arraycopy($old, 0, $d, 0, $old.length);
		}
	}
	
	public void pad(int $size) {
		if ($d.length >= $size) return;
		byte[] $old = $d;
		$d = new byte[$size];
		System.arraycopy($old, 0, $d, 0, $old.length);
	}
	
	/**
	 * @param $size
	 * @param $rando would you like it silly?
	 */
	public void pad(int $size, boolean $rando) {
		int $delta = $size - $d.length;
		if ($delta > 0) {
			byte[] $old = $d;
			int $olen = $old.length;
			$d = new byte[$size];
			System.arraycopy($old, 0, $d, 0, $olen);
			$old = new byte[$delta];
			new Random().nextBytes($old);
			System.arraycopy($old, 0, $d, $olen, $delta);
		}
	}
	
	public void chop(int $size) {
		if ($d.length <= $size) return;
		byte[] $old = $d;
		$d = new byte[$size];
		System.arraycopy($old, 0, $d, 0, $size);
	}
	
	/**
	 * Shift a byte off the beginning of the vector
	 * 
	 * @return the first byte
	 */
	public byte shift() {
		byte[] $old = $d;
		$d = new byte[$old.length-1];
		System.arraycopy($old, 1, $d, 0, $d.length);
		return $old[0];
	}
	
	/**
	 * Shift some number of bytes off the beginning of the vector
	 * 
	 * @return a ByteVector of the shifted bytes
	 * @param $bytes
	 *                the number of bytes to shift
	 */
	public ByteVector shift(int $bytes) {
		return new ByteVector(shiftArray($bytes));
	}
	
	/**
	 * Shift some number of bytes off the beginning of the vector
	 * 
	 * @return a byte array of the shifted bytes
	 * @param $bytes
	 *                the number of bytes to shift
	 */
	public byte[] shiftArray(int $bytes) {
		byte[] $old = $d;
		$d = new byte[$old.length-$bytes];
		System.arraycopy($old, $bytes, $d, 0, $d.length);
		byte[] $v = new byte[$bytes];
		System.arraycopy($old, 0, $v, 0, $bytes);
		return $v;
	}
	
	/**
	 * Prepends the given value to the beginning of the vector (effectively
	 * incrementing the index of all other values).
	 * 
	 * @param $b
	 *                the value to prepend
	 */
	public void unshift(byte $b) {
		emptyUnshift(1);
		$d[0] = $b;
	}

	/**
	 * Prepends the given ByteVector's data to the beginning of this vector
	 * (effectively increases the index of all other values by $bv.length).
	 * 
	 * @param $bv
	 *                the data the prepend
	 */
	public void unshift(ByteVector $bv) {
		int $n = $bv.length();
		emptyUnshift($n);
		System.arraycopy($bv.$d, 0, $d, 0, $bv.$d.length);
	}
	
	/**
	 * Prepends the given byte[] of data to the beginning of this vector
	 * (effectively increases the index of all other values by $ba.length).
	 * 
	 * @param $ba
	 *                the data the prepend
	 */
	public void unshift(byte[] $ba) {
		int $n = $ba.length;
		emptyUnshift($n);
		System.arraycopy($ba, 0, $d, 0, $ba.length);
	}
	
	/**
	 * Functions as per unshift, but prepending the given number of 'off' bytes. 
	 * @param $bytes
	 */
	public void emptyUnshift(int $bytes) {
		byte[] $old = $d;
		$d = new byte[$old.length+$bytes];
		System.arraycopy($old, 0, $d, $bytes, $old.length);
	}
	
	/**
	 * Pops a value off the end of the vector.
	 * @return the last value
	 */
	public byte pop() {
		byte[] $old = $d;
		$d = new byte[$old.length-1];
		System.arraycopy($old, 0, $d, 0, $d.length);
		return $old[$old.length-1];
	}
	
	/**
	 * Pop some number of bytes off the end of the vector
	 * 
	 * @return a ByteVector of the popped bytes
	 * @param $bytes
	 *                the number of bytes to pop
	 */
	public ByteVector pop(int $bytes) {
		return new ByteVector(popArray($bytes));
	}
	
	/**
	 * Pop some number of bytes off the end of the vector
	 * 
	 * @return a byte array of the popped bytes
	 * @param $bytes
	 *                the number of bytes to pop
	 */
	public byte[] popArray(int $bytes) {
		byte[] $old = $d;
		$d = new byte[$old.length-$bytes];
		System.arraycopy($old, 0, $d, 0, $d.length);
		byte[] $v = new byte[$bytes];
		System.arraycopy($old, $d.length, $v, 0, $bytes);
		return $v;
	}
	
	/**
	 * Appends the given value to the end of the vector.
	 * 
	 * @param $b
	 *                the value to append
	 */
	public void push(byte $b) {
		emptyPush(1);
		$d[$d.length-1] = $b;
	}

	/**
	 * Appends the content of the given vector to the end of the vector.
	 * 
	 * @param $bv
	 *                the vector to append
	 */
	public void push(ByteVector $bv) {
		int $i = $d.length;
		emptyPush($bv.$d.length);
		System.arraycopy($bv.$d,0,$d,$i,$bv.$d.length);
	}
	
	/**
	 * Appends the given bytes to the end of the vector.
	 * 
	 * @param $bats
	 *                the bytes to append
	 */
	public void push(byte[] $bats) {
		int $i = $d.length;
		emptyPush($bats.length);
		System.arraycopy($bats,0,$d,$i,$bats.length);
	}
	
	/**
	 * Functions as per push, but appending the given number of 'off' (0x0, b00000000) bytes.
	 * 
	 * @param $bytes
	 */
	public void emptyPush(int $bytes) {
		byte[] $old = $d;
		$d = new byte[$old.length+$bytes];
		System.arraycopy($old, 0, $d, 0, $old.length);
	}
	
	/**
	 * Unshift and pop equal amounts (putting bytes on the beginning and removing
	 * bytes from the end).
	 * 
	 * @param $bv
	 *                the bytes to add; its length is how much will be popped off and
	 *                returned.
	 * @return the ByteVector of those bytes popped of the end
	 */
	public ByteVector slide(ByteVector $bv) {
		ByteVector $v = pop($bv.$d.length);
		unshift($bv);
		return $v;
	}
	
	/**
	 * Unshift and pop equal amounts (putting bytes on the beginning and removing
	 * bytes from the end).
	 * 
	 * @param $bv
	 *                the bytes to add; its length is how much will be popped off and
	 *                returned.
	 * @return the byte array of those bytes popped of the end
	 */
	public byte[] slideArray(ByteVector $bv) {
		byte[] $v = popArray($bv.$d.length);
		unshift($bv);
		return $v;
	}
	
	/**
	 * Unshift and pop equal amounts (putting bytes on the beginning and removing
	 * bytes from the end).
	 * 
	 * @param $ba
	 *                the bytes to add; its length is how much will be popped off and
	 *                returned.
	 * @return the ByteVector of those bytes popped of the end
	 */
	public ByteVector slide(byte[] $ba) {
		ByteVector $v = pop($ba.length);
		unshift($ba);
		return $v;
	}
	
	/**
	 * Unshift and pop equal amounts (putting bytes on the beginning and removing
	 * bytes from the end).
	 * 
	 * @param $ba
	 *                the bytes to add; its length is how much will be popped off and
	 *                returned.
	 * @return the byte array of those bytes popped of the end
	 */
	public byte[] slideArray(byte[] $ba) {
		byte[] $v = popArray($ba.length);
		unshift($ba);
		return $v;
	}
	
	/**
	 * Effectively unshifts $size 'off' bytes to the beginning of the the vector, while
	 * simultaneously removing $size bytes from the end.
	 * 
	 * Same as calling emptyUnshift($size) followed by pop($size), but more efficient.
	 * 
	 * @param $size
	 *                number of bytes to move
	 */
	public void emptySlide(int $size) {
		System.arraycopy($d, 0, $d, $size, $d.length - $size);
	}
	
	/**
	 * Reverse slide; push and shift in equal amounts (putting bytes on the end and
	 * removing bytes from the beginning).
	 * 
	 * @param $bv
	 *                the bytes to append; its length is how much will be shifted off
	 *                and returned.
	 * @return the ByteVector of those bytes shifted off the front
	 */
	public ByteVector rslide(ByteVector $bv) {
		ByteVector $v = shift($bv.$d.length);
		push($bv);
		return $v;
	}
	
	/**
	 * Reverse slide; push and shift in equal amounts (putting bytes on the end and
	 * removing bytes from the beginning).
	 * 
	 * @param $bv
	 *                the bytes to append; its length is how much will be shifted off
	 *                and returned.
	 * @return the byte array of those bytes shifted off the front
	 */
	public byte[] rslideArray(ByteVector $bv) {
		byte[] $v = shiftArray($bv.$d.length);
		push($bv);
		return $v;
	}
	
	/**
	 * Reverse slide; push and shift in equal amounts (putting bytes on the end and
	 * removing bytes from the beginning).
	 * 
	 * @param $ba
	 *                the bytes to append; its length is how much will be shifted off
	 *                and returned.
	 * @return the ByteVector of those bytes shifted off the front
	 */
	public ByteVector rslide(byte[] $ba) {
		ByteVector $v = shift($ba.length);
		push($ba);
		return $v;
	}
	
	/**
	 * Reverse slide; push and shift in equal amounts (putting bytes on the end and
	 * removing bytes from the beginning).
	 * 
	 * @param $ba
	 *                the bytes to append; its length is how much will be shifted off
	 *                and returned.
	 * @return the byte array of those bytes shifted off the front
	 */
	public byte[] rslideArray(byte[] $ba) {
		byte[] $v = shiftArray($ba.length);
		push($ba);
		return $v;
	}
	
	
	
	/**
	 * Randomly flips bytes in the given range.
	 * @param $fromIndex index to start flipping at (inclusive)
	 * @param $toIndex index to stop flipping at (exclusive)
	 * @param $rand seed
	 */
	public void randomize(int $fromIndex, int $toIndex, Random $rand) {
		 int $delta = $toIndex - $fromIndex;
		 byte[] $bullocks = new byte[$delta];
		 $rand.nextBytes($bullocks);
		 System.arraycopy($bullocks, 0, $d, $fromIndex, $delta);
	}
	
	/**
	 * Randomly flips all bytes in the vector.
	 * 
	 * Same as calling this.randomize(0,this.length(),new java.util.Random()), but
	 * more efficient.
	 */
	public void randomize() {
		randomize(new java.util.Random());
	}
	
	public void randomize(Random $rand) {
		$rand.nextBytes($d);
	}
	
	
	
	
	public String toString() {
		return new String($d,Strings.UTF_8);
	}
	
	/**
	 * @return a new byte array containing the data from this vector
	 */
	public byte[] toByteArray() {
		byte[] $v = new byte[$d.length];
		System.arraycopy($d, 0, $v, 0, $d.length);
		return $v;
	}
	
	/**
	 * @return the byte array that backs this vector -- allows the caller to break
	 *         encapsulation. Will be slightly more efficient that calling
	 *         toByteArray() if the caller simply intends to copy the bytes into some
	 *         other array. If the caller knows that they are doing and want to break
	 *         encapsulation for their own reasons, that's fine... this object will
	 *         adjust anyway.
	 */
	public byte[] getByteArray() {
		return $d;
	}
	
	public BitVector toBitVector() {
		return new BitVector(getByteArray());
	}
	
	public String toBase64() {
		return Base64.encode($d);
	}
	
	public static String toBase64(ByteVector $bv) {
		return Base64.encode($bv.$d);
	}
	
	public static ByteVector fromBase64(String $b64) {
		ByteVector $t = new ByteVector();
		$t.$d = Base64.decode($b64);
		return $t;
	}
	
	
	
	public void clear() {
		$d = new byte[$d.length];
	}

	public void clear(int $fromIndex, int $toIndex) {
		for (int $i = $fromIndex; $i < $toIndex; $i++)
			$d[$i] = 0x0;
	}

	public void clear(int $byteIndex) {
		$d[$byteIndex] = 0x0;
	}

	public int hashCode() {
		return Arr.hashCode(this.$d);
	}
	
	public boolean equals(Object $obj) {
		if (!($obj instanceof ByteVector)) return false;
		ByteVector $bv = (ByteVector) $obj;
		if ($d.length != $bv.$d.length) return false;
		for (int $i = 0; $i < $d.length; $i++)
			if ($d[$i] != $bv.$d[$i]) return false;
		return true;
	}
	
	public ByteVector getRange(int $fromIndex, int $toIndex) {
		return new ByteVector($d,$fromIndex,$toIndex-$fromIndex);
	}
	
	public byte[] getRangeArray(int $fromIndex, int $toIndex) {
		return Arr.copyOfRange($d,$fromIndex,$toIndex);
	}
	
	public ByteVector get(int $fromIndex, int $length) {
		return new ByteVector(getArray($fromIndex,$length));
	}
	
	public byte[] getArray(int $fromIndex, int $length) {
		return Arr.copy($d,$fromIndex,$length);
	}
	
	public byte get(int $byteIndex) {
		return $d[$byteIndex];
	}
	
	public void set(int $byteIndex, byte $b) {
		$d[$byteIndex] = $b;
	}
	
	public void setRange(int $fromIndex, int $toIndex, byte $b) {
		for (int $i = $fromIndex; $i < $toIndex; $i++)
			$d[$i] = $b;
	}
	
	public boolean isEmpty() {
		return ($d.length == 0);
	}
	
	public int length() {
		return $d.length;
	}
	
	public int size() {
		return $d.length;
	}

	public int compareTo(ByteVector $x) {
		return Arr.compare($d,$x.$d);
	}
}
