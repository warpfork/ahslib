package us.exultant.ahs.util;

import us.exultant.ahs.core.*;
import java.nio.*;

/** Like it says on the tin. */
public class TranslatorToB64 implements Translator<ByteBuffer,String> {
	public static final TranslatorToB64 INSTANCE = new TranslatorToB64();

	/**
	 * @param $bytes
	 *                a ByteBuffer to read between position and limit
	 * @return a base64 string
	 */
	public final String translate(ByteBuffer $bytes) {
		byte[] $x = new byte[$bytes.limit() - $bytes.position()];
		$bytes.get($x);	//XXX:AHS:EFFIC: this copy is silly.  we should alter the Base64 class to allow offset+range operations and then make this better.
		return Base64.encode($x);
	}
}
