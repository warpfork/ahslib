package us.exultant.ahs.codec.eon.pre;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.*;
import us.exultant.ahs.codec.eon.*;

public class ByteVectorDencoder implements Dencoder<EonCodec,EonObject,ByteVector> {
	public static final Dencoder<EonCodec,EonObject,ByteVector> DENCODER = new ByteVectorDencoder();
	public static final  Encoder<EonCodec,EonObject,ByteVector> ENCODER = DENCODER;
	public static final  Decoder<EonCodec,EonObject,ByteVector> DECODER = DENCODER;
	
	public EonObject encode(EonCodec $codec, ByteVector $x) throws TranslationException {
		return $codec.simple("ByV", null, $x.$d);
	}
	
	public ByteVector decode(EonCodec $codec, EonObject $x) throws TranslationException {
		$x.assertKlass("ByV");
		return new ByteVector($x.getByteData());
	}
}
