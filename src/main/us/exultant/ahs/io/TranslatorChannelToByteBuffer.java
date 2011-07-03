package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * This actually 'translates' bytes off of a communication channel. It follows the
 * dead-simple "babble" protocol: the bytes should look like a series of 4-byte signed
 * integer lengths, followed by an arbitrary blob of the size specified by the int. The
 * size bytes are discarded after being used to determine the blob, and the blob is
 * wrapped in a ByteBuffer and returned as the result of the translation.
 * </p>
 * 
 * <p>
 * The translate method of this Translator may return null for any given invocation if
 * there is not immediately (i.e. in a nonblocking sense) sufficient data readable to get
 * a full blob.
 * </p>
 * 
 * <p>
 * It is NOT possible to use the same instance of a TranslatorChannelToByteBuffer in
 * several TranslationStack for any purpose if threads are involved. Since a
 * TranslatorChannelToByteBuffer has no way to enforce the atomicity of some of its
 * internal operations without becoming a blocking system, it must keep thread-local
 * state.
 * </p>
 * 
 * @author hash
 * 
 */
// this works fine and nonblockingly at winfully with reading because we *always* just keep handing the translator stack the same thing (the base channel) and we sometimes get data out.  writing doesn't turn out to be quite so elegant.
public class TranslatorChannelToByteBuffer implements Translator<ReadableByteChannel,ByteBuffer> {
	private final ByteBuffer	$preint	= ByteBuffer.allocate(4);
	private int			$messlen = -1;
	private ByteBuffer		$mess;
	
	public ByteBuffer translate(ReadableByteChannel $base) throws TranslationException {
		if ($messlen <= 0) {
			// try to read enough info to figure out what length of message we expect
			try {
				$base.read($preint);
			} catch (IOException $e) {
				if ($preint.remaining() != 4) throw new TranslationException("malformed babble -- partial message length header read before unexpected EOF", $e);
				return null;	// this is the one place in the Babble protocol for a smooth shutdown to be legal... the pump should just notice the channel being closed before next time around.}
			}
			
			if ($preint.remaining() > 0) return null; // don't have a size header yet.  keep waiting for more data.
			$messlen = Primitives.intFromByteArray($preint.array());
			$preint.rewind();
			if ($messlen < 1) throw new TranslationException("malformed babble -- message length header not positive");
			$mess = ByteBuffer.allocate($messlen);
		}
		// if procedure gets here, we either had messlen state from the last round or we have it now.
		
		// get the message (or at least part of it, if possible)
		try {
			$base.read($mess);
		} catch (IOException $e) {
			throw new TranslationException("babble of unexpected length", $e);
		}
		
		if ($mess.remaining() > 0) return null; // we just don't have as much information as this chunk should contain yet.  keep waiting for more data.
		
		$messlen = -1;
		$preint.rewind();
		$mess.rewind();
		return $mess;
	}
}
