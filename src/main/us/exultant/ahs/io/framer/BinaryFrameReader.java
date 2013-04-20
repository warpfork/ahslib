package us.exultant.ahs.io.framer;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.io.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * Reads messages framed by the binary length delimiters: the bytes that are read from the
 * channel look like a series of 4-byte signed integer lengths, followed by an arbitrary
 * blob of the size specified by the int. The size bytes are discarded after being used to
 * determine the blob, and the blob is wrapped in a ByteBuffer and returned as the result
 * of the translation.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class BinaryFrameReader implements ChannelReader<ByteBuffer> {
	private final ByteBuffer $preint = ByteBuffer.allocate(4);
	private int $messlen = -1;
	private ByteBuffer $mess;

	public ByteBuffer read(ReadableByteChannel $base) throws IOException, TranslationException {
		if ($messlen < 0) {
			// try to read enough info to figure out what length of message we expect
			try {
				if ($base.read($preint) == -1)
					$base.close();
			} catch (ClosedChannelException $e) {
				/* this is the one place a binary frame protocol for a smooth shutdown to be legal... the pump should just notice the channel being closed before next time around. */
				if ($preint.remaining() == 4) return null;
				throw new TranslationException("malformed babble -- partial message length header read before unexpected EOF", $e);
			} /* any other IOException is a channel break. */

			if ($preint.remaining() > 0) return null; // don't have a size header yet.  keep waiting for more data.
			$messlen = Primitives.intFromByteArray($preint.array());
			$preint.rewind();
			if ($messlen < 0) throw new TranslationException("malformed babble -- message length header cannot be negative");
			$mess = ByteBuffer.allocate($messlen);
		} else if ($messlen == 0) {
			// i don't know who sends empty chunks, but okay...
			$messlen = -1;
		}
		// if procedure gets here, we either had messlen state from the last round or we have it now.

		// get the message (or at least part of it, if possible)
		if ($base.read($mess) == -1) {
			$base.close();
			throw new TranslationException("malformed babble -- partial message read before unexpected EOF");
		}

		if ($mess.remaining() > 0) return null; // we just don't have as much information as this chunk should contain yet.  keep waiting for more data.

		$messlen = -1;
		$mess.rewind();
		return $mess;
	}
}
