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
	
	// okay, this is hard.
	// just because the channel tells the selector to power this pump doesn't mean there's actually -enough- data for a semantic event.
	// and that adapter we're extending doesn't expect us to do anything except 
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
		//NOTHING AFTER THIS LINE MAKES SENSE
		int $r = $base.read($bb);
		if ($bb.remaining() > 0) return null;
		int $p = 0;	// use an internal field of bb for this
		while ($p < $messlen) {
			$k = $base.read($buf,$p,$buf.length-$p);
			if ($k == -1) break;	// it might be more appropriate to ReadHead to just quietly close without returning a semantic event or fireing an exception
			$p += $k;
		}
		if ($p != $messlen) throw new IOException("babble of unexpected length");
		
		$bb.rewind();
		return $bb;
	}
	
	public void close() throws IOException {
		$base.close();
	}	
}