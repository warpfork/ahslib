package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.Codec.*;
import ahs.io.codec.eon.*;

import mcon.msg.*;
import mcon.msg.MconMessage.*;

/**
 * Use this class to implement polymorphism in codecs by enrolling multiple instantiable
 * classes that share a common interface in this "seed", along with their encoders and
 * decoders. The mux shifts the "MAGICWORD_CLASS" field to the "MAGICWORD_HINT" field upon
 * encoding, and places the common interface's class in the "MAGICWORD_CLASS" field; the
 * process is reversed in decoding -- this means that the mux'd instantiable classes
 * enrolled in the mux must not use the "MAGICWORD_HINT" data field themselves, but otherwise any existing encoders and decoders should be usable transparently..
 * 
 * Calling the enroll(*) functions of this class automatically calls the appropriate
 * putHook(*) methods on the parent codec.
 * 
 * Once this mux is configured, all references to it can be safely discarded -- it will be
 * used internally by the parent codec in a transparent fashion. Simply give the parent
 * codec instances for encoding as normal, and when decoding give it the common interface
 * as the decode target.
 * 
 * @author hash
 * 
 */
public class EonDecodingMux<$FACE> {
	public EonDecodingMux(EonCodec $parent, Class<$FACE> $klass) {
		this.$parent = $parent;
		this.$klass = $klass;
	}
	
	private final EonCodec		$parent;
	private final Class<$FACE>	$klass;
	
	// okay, i'm going about this almost completely wrong: i'm going to need to list everything first, THEN put it in the parent with a final initialize call
	// i need a single decode method that knows about all of the mux'd targets that i can give to the parent codec
	public <$T extends $FACE> void enroll(Class<$T> $klass, final Encoder<EonCodec,EonObject,$T> $encoder, final Decoder<EonCodec,EonObject,$T> $decoder) {
		$parent.putHook($klass, new Dencoder<EonCodec,EonObject,$T>() {
			public EonObject encode(EonCodec $codec, $T $x) throws TranslationException {
				EonObject $eo = $encoder.encode($parent, $x);
				$eo.put(Eon.MAGICWORD_HINT, $eo.getKlass());
				$eo.putKlass(EonDecodingMux.this.$klass);
				return $eo;
			}
			
			public $FACE decode(EonCodec $codec, EonObject $eo) throws TranslationException {
				$eo.assertKlass(EonDecodingMux.this.$klass);
				$eo.putKlass($eo.getString(Eon.MAGICWORD_HINT));
				$T $x = $decoder.decode($parent, $eo);
				return $x;
			}
		});
		
	}
}
