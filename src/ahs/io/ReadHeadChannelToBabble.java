package ahs.io;

import ahs.util.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class ReadHeadChannelToBabble extends ReadHeadAdapter<ByteBuffer> {
	public ReadHeadChannelToBabble(ByteChannel $base) {
		super();
		this.$base = $base;
	}
	
	private final ByteChannel	$base;
	private final ByteBuffer	$preint	= ByteBuffer.allocate(4);
	
	// oh god.  we still have an issue to watch out for.  one select fire from the underlying might still need multiple pump cycles.  should a selector pump just go until the kid returns a null chunk and interrupts itself or what?  i guess so.
	protected ByteBuffer getChunk() throws IOException {
		// figure out what length of message we expect
		if ($base.read($preint) == -1) baseEof();
			if ($preint.remaining() != 4) throw new IOException("malformed babble -- message length header not read");
		if ($preint.remaining() > 0)
			return null;	// keep waiting for more data
		int $messlen = Primitives.intFromByteArray($preint.array());
		$preint.rewind();
		if ($messlen < 1) throw new IOException("malformed babble -- negative message length header");
		
		// get the message
		ByteBuffer $bb = ByteBuffer.allocate($messlen);
		if ($base.read($bb) == -1) {
			// we're pissed
			throw new IOException("babble of unexpected length");
		}
		
		if ($bb.remaining() > 0) return null;	// we just don't have as much information as this chunk should contain yet
		
		$bb.rewind();
		return $bb;
	}
	
	public void close() throws IOException {
		$base.close();
	}	
}