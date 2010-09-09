package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;

import java.nio.*;

/**
 * Assumes the entire ByteBuffer is backed by an accessible array and the entire thing
 * should be used.
 */
public class ByteBufferToEonTranslator implements Translator<ByteBuffer,EonObject> {
	public ByteBufferToEonTranslator(EonCodec $co) {
		this.$co = $co;
	}
	
	private final EonCodec	$co;
	
	public EonObject translate(ByteBuffer $bb) throws TranslationException {
		return $co.deserialize($bb.array());
	}
}
