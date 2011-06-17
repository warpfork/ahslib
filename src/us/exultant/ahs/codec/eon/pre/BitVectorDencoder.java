package us.exultant.ahs.codec.eon.pre;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.*;
import us.exultant.ahs.codec.eon.*;

public class BitVectorDencoder implements Dencoder<EonCodec,EonObject,BitVector> {
	public static final Dencoder<EonCodec,EonObject,BitVector> DENCODER = new BitVectorDencoder();
	public static final  Encoder<EonCodec,EonObject,BitVector> ENCODER = DENCODER;
	public static final  Decoder<EonCodec,EonObject,BitVector> DECODER = DENCODER;
	
	public EonObject encode(EonCodec $codec, BitVector $x) throws TranslationException {
		EonObject $jo = $codec.simple("BiV", null, $x.toByteArray());
		$jo.put("l", $x.length());
		return $jo;
	}
	
	public BitVector decode(EonCodec $codec, EonObject $x) throws TranslationException {
		$x.assertKlass("BiV");
		return new BitVector($x.getByteData(),0,$x.getInt("l"));
	}
}
