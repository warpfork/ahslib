package us.exultant.ahs.io.framer;

import us.exultant.ahs.core.*;
import us.exultant.ahs.io.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * <p>
 * Writes messages framed by the binary length delimiters: the bytes that are written to
 * the channel look like a series of 4-byte signed integer lengths, followed by an
 * arbitrary blob of the size specified by the int.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class BinaryFrameWriter implements ChannelWriter<ByteBuffer> {
	private final ByteBuffer $preint = ByteBuffer.allocate(4);
	private ByteBuffer $blob;	// i don't think i should need to put volatile on this since even though a work target can be powered from different threads over time, there's enough locks around somewhere in that handoff that this should already be safe.

	public boolean write(WritableByteChannel $channel, ByteBuffer $data) throws IOException, TranslationException {
		if ($data != null) {
			if ($blob != null) throw new IllegalStateException("you may not push more data into a ChannelWriter that already has data in a buffer!");
			$blob = $data;
			/* I trust that you've already $blob.rewind() if that's what you want. */
			$preint.putInt($blob.remaining());
			$preint.rewind();
		}

		if ($preint.remaining() > 0) $channel.write($preint);	/* we might have done this already in a previous invocation, but so?  the buffer remembers how much is remaining. */
		/* ohgod: even if you're treating this channel single-threadedly in the jvm,
		 * the *kernel* is still draining at random, so you can't just happily jump
		 * onto trying to write the blob and assume that if the preint didn't finish
		 * writing then the blob will also fail properly. */
		if ($preint.remaining() > 0) return false;
		$channel.write($blob);
		if ($blob.remaining() == 0) {
			$blob = null;
			$preint.rewind();
			return true;
		} else return false;
	}
}
