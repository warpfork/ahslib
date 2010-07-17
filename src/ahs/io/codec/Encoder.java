package ahs.io.codec;

import ahs.io.*;

/**
 * <p>
 * Objects that are responsible for the encoding of others should implement this
 * interface; the idea is that implementers of this interface should function something
 * like a factory pattern for objects representing "encoded" data. Typically, the
 * interface maps a $TARG class that contains data in memory (say, a ByteBuffer or a Date
 * or an HTTPRequest) into a more serial form represented by the $CODE class (such as
 * JSONObject or JSONArray) which can then be readily converted into a String or byte[]
 * (or $CODE may simply be String or ByteBuffer begin with). Note that there is no
 * restriction implicit or otherwise that this interface only be responsible for
 * serialization; entire stacks of Encoder interfaces might be chained together to
 * represent layered protocols, with the $CODE of one matching a $TARG of the next.
 * </p>
 * 
 * <p>
 * Classes that support encoding are recommended to follow a pattern of providing an
 * Encoder implementer as a public static inner class of themselves, then make an instance
 * available as a public static final field. This pattern allows complete access to all
 * private fields of the encodable class without increasing their exposure, keeps code
 * cleaner by limiting amount of visibility the codec running an encode process gains into
 * its client classes, and removes any possibility for ambiguity should it be the case
 * that some classes wish to provide multiple methods for encoding.
 * </p>
 * 
 * <p>
 * Note that it is possible for a single class to implement the Encoder interface for
 * multiple $TARG types. This is not necessarily advisible, however; many will find it
 * more intuitive to keep a one-to-one correspondence between Encoder and Decoder
 * implementors (perhaps instead merging those interfaces into a single class).
 * </p>
 * 
 * <p>
 * Unless a specific implementation states otherwise, the encode method is assumed to be
 * reentrant.
 * </p>
 * 
 * @author hash
 * 
 * @param <$TARG>
 *                The type of object to be encoded. A single, complete, bi-directional
 *                codec system will typically require decoders of one $TARG type for each
 *                type of object to be encodable, each with the same $CODE type.
 * @param <$CODE>
 *                The type of object used to represent the encoded version of the data. A
 *                typical example might be ahs.json.JSONObject.
 */
public interface Encoder<$CODE, $TARG> {
	public $CODE encode(Codec<$CODE> $codec, $TARG $x) throws TranslationException;
}
