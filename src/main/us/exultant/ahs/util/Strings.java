package us.exultant.ahs.util;

import java.nio.*;
import java.nio.charset.Charset;
import java.util.*;

public class Strings {
	public static final Charset	UTF_8		= Charset.forName("UTF-8");
	public static final Charset	ASCII		= Charset.forName("ASCII");
	public static final char[]	HEX_CHARS	= new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

//////////////////////////////////////////////////////////////// TRANSLATION FUCTIONS
	// some of this functionality is already readily available, but these differ in that default charset always means utf-8
	
	public static final String fromBytes(byte[] $bats, Charset $cs) {
		return new String($bats, $cs);
	}
	
	public static final String fromBytes(byte[] $bats) {
		return new String($bats, UTF_8);
	}
	
	public static final String fromBytes(ByteBuffer $bats, Charset $cs) {
		return new String(Arr.toArray($bats), $cs);
	}
	
	public static final String fromBytes(ByteBuffer $bats) {
		return new String(Arr.toArray($bats), UTF_8);
	}
	
	public static final byte[] toBytes(String $s) {
		return $s.getBytes(UTF_8);
	}
	
//////////////////////////////////////////////////////////////// PARTING FUCTIONS
	// default to returning the original string if the pattern is not found
	
	public static final String getPartAfter(String $source, String $pattern) {
		int $index = $source.indexOf($pattern);
		if ($index < 0) return $source;
		return $source.substring($index+$pattern.length());
	}
	
	public static final String getPartAfterOrEmpty(String $source, String $pattern) {
		int $index = $source.indexOf($pattern);
		if ($index < 0) return "";
		return $source.substring($index+$pattern.length());
	}
	
	public static final String getPartBefore(String $source, String $pattern) {
		int $index = $source.indexOf($pattern);
		if ($index < 0) return $source;
		return $source.substring(0,$index);
	}
	
	public static final String getPartBeforeOrEmpty(String $source, String $pattern) {
		int $index = $source.indexOf($pattern);
		if ($index < 0) return "";
		return $source.substring(0,$index);
	}
	
	public static final String getPartBetween(String $source, String $startPattern, String $endPattern) {
		return getPartBeforeLast(getPartAfter($source,$startPattern),$endPattern);
	}
	
	public static final String getPartAfterLast(String $source, String $pattern) {
		int $index = $source.lastIndexOf($pattern);
		if ($index < 0) return $source;
		return $source.substring($index+$pattern.length());
	}
	
	public static final String getPartAfterLastOrEmpty(String $source, String $pattern) {
		int $index = $source.lastIndexOf($pattern);
		if ($index < 0) return "";
		return $source.substring($index+$pattern.length());
	}
	
	public static final String getPartBeforeLast(String $source, String $pattern) {
		int $index = $source.lastIndexOf($pattern);
		if ($index < 0) return $source;
		return $source.substring(0,$index);
	}
	
	public static final String getPartBeforeLastOrEmpty(String $source, String $pattern) {
		int $index = $source.lastIndexOf($pattern);
		if ($index < 0) return "";
		return $source.substring(0,$index);
	}
	
	public static final String[] splitOnNext(String $source, String $pattern) {
		int $index = $source.indexOf($pattern);
		if ($index < 0) return new String[] {"",$source};
		return new String[] {$source.substring(0,$index),$source.substring($index+$pattern.length())};
	}
	
	public static final String[] splitOnLast(String $source, String $pattern) {
		int $index = $source.lastIndexOf($pattern);
		if ($index < 0) return new String[] {"",$source};
		return new String[] {$source.substring(0,$index),$source.substring($index+$pattern.length())};
	}
	
////////////////////////////////////////////////////////////////
	
	public static final String merge(String[] $r) {
		return merge($r,"\n");
	}
	
	public static final String merge(String[] $r, String $dlim) {
		StringBuilder $sb = new StringBuilder();
		for (String $s : $r) $sb.append($s).append($dlim);
		return $sb.toString();
	}
	
	/**
	 * Mutates the given array of strings to contain only interned strings.
	 * 
	 * @return an array of intern'd strings (=== the arg)
	 */
	public static final String[] intern(String[] $r) {
		for (int $i = 0; $i < $r.length; $i++)
			$r[$i] = $r[$i].intern();
		return $r;
	}
	
	/**
	 * @return an empty string if the argument was null; otherwise the argument.
	 */
	public static String noNull(String s) {
		return (s == null ? "" : s);
	}
	
//////////////////////////////////////////////////////////////// FILE NAME MANIPULATION FUNCTIONS
	
	public static final String dirname(String $path) {
		if ($path.endsWith("/")) return $path;
		return $path.substring(0, $path.lastIndexOf("/")+1);
	}
	
	public static final String fullname(String $path) {
		return getPartAfterLast($path, "/");
	}
	
	public static final String basename(String $path) {
		return getPartBefore(fullname($path),".");
	}
	
	/**
	 * 
	 * @param $path a string representing a path for a filesystem
	 * @return the string following the last period
	 */
	public static final String extension(String $path) {
		return getPartAfterLastOrEmpty(fullname($path),".");
	}
	
//////////////////////////////////////////////////////////////// PADDING FUNCTIONS
	
	public static String repeat(char $pad, int $count) {
		if ($count < 0) throw new IllegalArgumentException("padCount must be >= 0");
		StringBuffer $buf = new StringBuffer($count);
		for (int i = 0; i < $count; ++i)
			$buf.append($pad);
		return $buf.toString();
	}
	
	/**
	 * Front-zero buff a number, and return it as a string. (Effectively,
	 * <code>padLeftToWidth(String.valueOf($n), "0", $desiredWidth)</code>.)
	 */
	public static String frontZeroBuff(int $n, int $desiredWidth) {
		return padLeftToWidth(String.valueOf($n), '0', $desiredWidth);
	}
	
	/**
	 * Appends $padCount space characters to the end of a string.
	 * 
	 * @param $s
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padRight(String $s, int $padCount) {
		return padRight($s, ' ', $padCount);
	}
	
	/**
	 * Appends $padCount space characters to the beginning of a string.
	 * 
	 * @param $s
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padLeft(String $s, int $padCount) {
		return padLeft($s, ' ', $padCount);
	}
	
	/**
	 * Appends $padCount iterations of the "$pad" string to the end of a string "$s".
	 * 
	 * @param $s
	 * @param $pad
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padRight(String $s, String $pad, int $padCount) {
		if ($padCount < 0) throw new IllegalArgumentException("padCount must be >= 0");
		StringBuffer $buf = new StringBuffer($s.length()+$padCount*$pad.length());
		$buf.append($s);
		for (int i = 0; i < $padCount; ++i)
			$buf.append($pad);
		return $buf.toString();
	}
	
	/**
	 * Appends $padCount iterations of the "$pad" string to the beginning of a string
	 * "$s".
	 * 
	 * @param $s
	 * @param $pad
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padLeft(String $s, String $pad, int $padCount) {
		if ($padCount < 0) throw new IllegalArgumentException("padCount must be >= 0");
		StringBuffer $buf = new StringBuffer($s.length()+$padCount*$pad.length());
		for (int i = 0; i < $padCount; ++i)
			$buf.append($pad);
		$buf.append($s);
		return $buf.toString();
	}
	
	/**
	 * Appends $padCount iterations of the "$pad" string to the end of a string "$s".
	 * 
	 * @param $s
	 * @param $pad
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padRight(String $s, char $pad, int $padCount) {
		return $s+repeat($pad,$padCount);
	}
	
	/**
	 * Appends $padCount iterations of the "$pad" string to the beginning of a string
	 * "$s".
	 * 
	 * @param $s
	 * @param $pad
	 * @param $padCount
	 * @return the padded string
	 */
	public static String padLeft(String $s, char $pad, int $padCount) {
		return repeat($pad,$padCount)+$s;
	}
	
	/**
	 * If the string "$s" is shorter than $desiredWidth, space characters are
	 * appeneded to the end of the string until it has a length matching
	 * $desiredWidth.
	 * 
	 * @param $s
	 * @param $desiredWidth
	 * @return the padded string
	 */
	public static String padRightToWidth(String $s, int $desiredWidth) {
		if ($s.length() < $desiredWidth) return padRight($s, $desiredWidth - $s.length());
		return $s;
	}
	
	/**
	 * If the string "$s" is shorter than $desiredWidth, the "$pad" string is appended
	 * to the end of the string the number of times that would be necessary to make
	 * the original string's length match $desiredWidth (assuming that the $pad string
	 * is a single character in length).
	 * 
	 * @param $s
	 * @param $desiredWidth
	 * @return the padded string
	 */
	public static String padRightToWidth(String $s, char $pad, int $desiredWidth) {
		if ($s.length() < $desiredWidth) return padRight($s, $pad, $desiredWidth - $s.length());
		return $s;
	}
	
	/**
	 * If the string "$s" is shorter than $desiredWidth, space characters are
	 * appeneded to the beginning of the string until it has a length matching
	 * $desiredWidth.
	 * 
	 * @param $s
	 * @param $desiredWidth
	 * @return the padded string
	 */
	public static String padLeftToWidth(String $s, int $desiredWidth) {
		if ($s.length() < $desiredWidth) return padLeft($s, $desiredWidth - $s.length());
		return $s;
	}
	
	/**
	 * If the string "$s" is shorter than $desiredWidth, the "$pad" string is appended
	 * to the beginning of the string the number of times that would be necessary to
	 * make the original string's length match $desiredWidth (assuming that the $pad
	 * string is a single character in length).
	 * 
	 * @param $s
	 * @param $desiredWidth
	 * @return the padded string
	 */
	public static String padLeftToWidth(String $s, char $pad, int $desiredWidth) {
		if ($s.length() < $desiredWidth) return padLeft($s, $pad, $desiredWidth - $s.length());
		return $s;
	}
	
	public static List<String> wrapToList(String $s, int $width) {
		List<String> $v = new LinkedList<String>();
		if (($s != null) && ($s.length() > 0)) {
			StringBuffer $buf = new StringBuffer();
			int $lastSpaceBufIndex = -1;
			for (int i = 0; i < $s.length(); ++i) {
				char c = $s.charAt(i);
				if (c == '\n') {
					$v.add($buf.toString());
					$buf.setLength(0);
					$lastSpaceBufIndex = -1;
				} else {
					if (c == ' ') {
						if ($buf.length() >= $width - 1) {
							$v.add($buf.toString());
							$buf.setLength(0);
							$lastSpaceBufIndex = -1;
						}
						if ($buf.length() > 0) {
							$lastSpaceBufIndex = $buf.length();
							$buf.append(c);
						}
					} else {
						if ($buf.length() >= $width) {
							if ($lastSpaceBufIndex != -1) {
								$v.add($buf.substring(0, $lastSpaceBufIndex));
								$buf.delete(0, $lastSpaceBufIndex + 1);
								$lastSpaceBufIndex = -1;
							}
						}
						$buf.append(c);
					}
				}
			}
			if ($buf.length() > 0) $v.add($buf.toString());
		}
		return $v;
	}
	
//////////////////////////////////////////////////////////////// HEX ENCODING FUNCTIONS
	
	public static final byte[] fromHex(String $hex) { return decHex($hex); }
	public static final byte[] decHex(String $hex) {
	        byte[] $bah = new byte[$hex.length() / 2];
		for (int $i = 0; $i < $hex.length(); $i += 2) {
			int $j = Integer.parseInt($hex.substring($i, $i + 2), 16);
			$bah[$i / 2] = (byte) ($j & 0xFF);
		}
		return $bah;
	}
	
	public static final String toHex(byte[] $bah) { return encHex($bah); }
	public static final String encHex(byte[] $bah) {
		char[] $chars = new char[2 * $bah.length];
		for (int $i = 0; $i < $bah.length; ++$i) {	// could probably save cpu at the cost of a 4 bytes of memory by just having two counters, since it would remove the need to multiply
			$chars[2 * $i] = HEX_CHARS[($bah[$i] & 0xF0) >>> 4];
			$chars[2 * $i + 1] = HEX_CHARS[$bah[$i] & 0x0F];
		}
		return new String($chars);
		
		//   also works.  Relative speed untested (but presumed worse):
		//
		//	char[] out = new char[$bah.length * 2]; // 2  hex characters per byte
		//	for (int i = 0; i < $bah.length; i++) {
		//		out[2 * i] = HEX_CHARS[$bah[i] < 0 ? 8 + ($bah[i] + 128) / 16 : $bah[i] / 16]; // append sign bit for negative bytes
		//		out[2 * i + 1] = HEX_CHARS[$bah[i] < 0 ? ($bah[i] + 128) % 16 : $bah[i] % 16];
		//	}
		//	return new String(out); // char sequence to string
		//
	}
}
