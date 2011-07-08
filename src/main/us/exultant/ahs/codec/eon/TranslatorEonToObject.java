package us.exultant.ahs.codec.eon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.*;

/**
 * Adapts a {@link Codec} to perform as a {@link Translator} between a serializable
 * {@link EonObject} form and some full Object specified by the generic type. (Note that
 * this may even implicitly be a muxed type, since the codec will handle this
 * transparently if already properly configured with a {@link EonDecodingMux}.)
 * 
 * @author hash
 * 
 * @param <$FACE>
 *                the type of Object this Translator should wield the Codec to decode
 *                EonObject into.
 */
public class TranslatorEonToObject<$FACE> implements Translator<EonObject,$FACE> {
	public TranslatorEonToObject(Class<$FACE> $klass, EonCodec $codec) {
		this.$klass = $klass;
		this.$codec = $codec;
	}
	
	private final Class<$FACE>	$klass;
	private final EonCodec		$codec;
	
	/**
	 * As long as the {@link Codec} this Translator was constructed with contains only
	 * re-entrant {@link Decoder}s, this translation method also reentrant.
	 */
	public $FACE translate(EonObject $x) throws TranslationException {
		return $codec.decode($x, $klass);
	}
}
