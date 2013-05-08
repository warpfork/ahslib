package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import java.nio.*;

/** Like it says on the tin. */
public class TranslatorToHex implements Translator<ByteBuffer,String> {
	public static final TranslatorToHex INSTANCE = new TranslatorToHex();

	/**
	 * @param $bytes
	 *                a ByteBuffer to read between position and limit
	 * @return a hexadecimal string (capitalized).
	 */
	public String translate(ByteBuffer $bytes) {
		final int $len = $bytes.limit() - $bytes.position();
		char[] $chars = new char[$len << 1];
		for (int $i = 0, $j = 0; $i < $len; $i++) {
			final byte $b = $bytes.get();
			$chars[$j++] = Strings.HEX_CHARS[($b & 0xF0) >>> 4];
			$chars[$j++] = Strings.HEX_CHARS[$b & 0x0F];
		}
		return new String($chars);
	}
}
