package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import java.nio.*;

/** Like it says on the tin. */
public class TranslatorFromB64 implements Translator<String,ByteBuffer> {
	public static final TranslatorFromB64 INSTANCE = new TranslatorFromB64();

	/**
	 * @param $hex
	 *                a base64 string
	 * @return a new array-backed buffer
	 */
	public final ByteBuffer translate(String $hex) {
		return ByteBuffer.wrap(Base64.decode($hex));
	}
}
