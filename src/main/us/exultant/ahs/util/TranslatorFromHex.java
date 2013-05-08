package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import java.nio.*;

/** Like it says on the tin. */
public class TranslatorFromHex implements Translator<String,ByteBuffer> {
	public static final TranslatorFromHex INSTANCE = new TranslatorFromHex();

	/**
	 * @param $hex
	 *                a hexadecimal string
	 * @return a new array-backed buffer
	 * @throws TranslationException
	 *                 if there are an odd number of characters, or any of the
	 *                 characters aren't hex characters.
	 */
	public final ByteBuffer translate(String $hex) throws TranslationException {
		final int $len = $hex.length();
		if (($len & 0x01) != 0) throw new TranslationException("hexadecimal must be an even number of characters");
		ByteBuffer $v = ByteBuffer.allocate($len >> 1);
		final char[] $in = $hex.toCharArray();
		final byte[] $out = $v.array();
		for (int $i = 0, $j = 0; $j < $len; $i++)
			$out[$i] = (byte) ((digit($in[$j++]) << 4) | digit($in[$j++]) & 0xFF);
		return $v;
	}

	private static final int digit(char $c) throws TranslationException {
		final int $v = Character.digit($c, 16);
		if ($v == -1) throw new TranslationException("invalid character for hexidecimal");
		return $v;
	}
}
