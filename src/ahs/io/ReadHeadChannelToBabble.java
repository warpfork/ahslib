package ahs.io;

import ahs.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class ReadHeadChannelToBabble extends ReadHeadAdapter<ByteBuffer> {
	public ReadHeadChannelToBabble(ByteChannel $base) {
		super();
		this.$base = $base;
		this.$messlen = -1;
	}
	
	private final ByteChannel	$base;
	private final ByteBuffer	$preint	= ByteBuffer.allocate(4);
	private int			$messlen;
	private ByteBuffer		$mess;
	
	// oh god.  we still have an issue to watch out for.  one select fire from the underlying might still need multiple pump cycles.  should a selector pump just go until the kid returns a null chunk and interrupts itself or what?  i guess so.
	protected ByteBuffer getChunk() throws IOException {
		if ($messlen < 0) {
			// figure out what length of message we expect
			if ($base.read($preint) == -1) {
				baseEof();
				if ($preint.remaining() != 4) throw new IOException("malformed babble -- message length header not read");
			}
			if ($preint.remaining() > 0) return null; // don't have a size header yet.  keep waiting for more data.
			$messlen = Primitives.intFromByteArray($preint.array());
			$preint.rewind();
			if ($messlen < 1) throw new IOException("malformed babble -- negative message length header");
			$mess = ByteBuffer.allocate($messlen);
		}
		// if procedure gets here, we either had messlen state from the last round or we have it now.
		
		// get the message
		if ($base.read($mess) == -1) {
			// we're pissed
			throw new IOException("babble of unexpected length");
		}
		
		if ($mess.remaining() > 0) return null; // we just don't have as much information as this chunk should contain yet.  keep waiting for more data.
		
		$messlen = -1;
		$preint.rewind();
		$mess.rewind();
		return $mess;
	}
	
	public void close() throws IOException {
		$base.close();
	}
}
