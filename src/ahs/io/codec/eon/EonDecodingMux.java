package ahs.io.codec.eon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.Codec.*;

import java.util.*;

/**
 * <p>
 * Use this class to implement polymorphism in codecs by enrolling multiple instantiable
 * classes that share a common interface in this "seed", along with their encoders and
 * decoders. The mux shifts the "MAGICWORD_CLASS" field to the "MAGICWORD_HINT" field upon
 * encoding, and places the common interface's class in the "MAGICWORD_CLASS" field; the
 * process is reversed in decoding -- this means that the mux'd instantiable classes
 * enrolled in the mux must not use the "MAGICWORD_HINT" data field themselves, but
 * otherwise any existing encoders and decoders should be usable transparently.
 * </p>
 * 
 * <p>
 * Calling the enroll(*) functions of this class also calls the putHook(*) methods on the
 * parent codec. Encoding and decoding hooks for the $FACE type are placed in the parent
 * codec at construction time.
 * </p>
 * 
 * <p>
 * Once this mux is configured, all references to it can be safely discarded -- it will be
 * used internally by the parent codec in a transparent fashion. Simply give the parent
 * codec instances for encoding and decoding as normal, but giving it the common interface
 * ($FACE) as the target class.
 * </p>
 * 
 * <p>
 * Be aware that the decoding process for a muxed object mutates the given EonObject as
 * opposed to cloning it (specifically, it sets the MAGICWORD_CLASS field).
 * </p>
 * 
 * @author hash
 * 
 */
public class EonDecodingMux<$FACE> {
	public EonDecodingMux(EonCodec $parent, Class<$FACE> $klass) {
		this.$parent = $parent;
		this.$klass = $klass;
		this.$demux = new HashMap<String,Class<? extends $FACE>>();
		initialize();
	}
	
	private final Class<$FACE>				$klass;
	private final EonCodec					$parent;
	private final Map<String,Class<? extends $FACE>>	$demux;
	
	private void initialize() {
		$parent.putHook($klass, new Dencoder<EonCodec,EonObject,$FACE>() {
			public EonObject encode(EonCodec $codec, $FACE $x) throws TranslationException {
				EonObject $eo = $parent.encode($x);	// even though $x currently has the type $FACE, later the parent codec will use $x.getClass... which will have more information, and thus keep the encoder from looping back to the $FACE target.
				$eo.put(Eon.MAGICWORD_HINT, $eo.getKlass());
				$eo.putKlass(EonDecodingMux.this.$klass);
				return $eo;
			}
			
			public $FACE decode(EonCodec $codec, EonObject $eo) throws TranslationException {
				$eo.assertKlass(EonDecodingMux.this.$klass);
				String $hint = $eo.getString(Eon.MAGICWORD_HINT);	// yes, this throws a translation exception if a hint isn't found.
				Class<? extends $FACE> $t = $demux.get($hint);
				if ($t == null) throw new TranslationException("Decoding dispatch hook not found for hint \"" + $hint + "\""); 
				$eo.putKlass($hint);
				return $parent.decode($eo, $t);
			}
		});
	}
	
	public <$T extends $FACE> void enroll(Class<$T> $klass, final Encoder<EonCodec,EonObject,$T> $encoder, final Decoder<EonCodec,EonObject,$T> $decoder) {
		$parent.putHook($klass, $encoder);
		$parent.putHook($klass, $decoder);
		$demux.put(Eon.getKlass($klass), $klass);
	}

	public <$T extends $FACE> void enroll(Class<$T> $klass, final Dencoder<EonCodec,EonObject,$T> $dencoder) {
		enroll($klass, (Encoder<EonCodec,EonObject,$T>)$dencoder, (Decoder<EonCodec,EonObject,$T>) $dencoder);
	}
}
