package ahs.io;

import ahs.util.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * This isn't really all that different from ReadHeadStreamToByteBuffer (obviously, since
 * the datatype it reads out to you is the same), but has the critical difference that
 * where ReadHeadStreamToByteBuffer just gives you blocks of a fixed size, this talks in
 * "babble"... variable length binary blobs, composed of a four-byte integer specifying
 * chunk size, followed by the data chunk itself of as many bytes as the interger
 * specifies.
 * 
 * @author hash
 * 
 */
public class ReadHeadStreamToBabble extends ReadHeadAdapter<ByteBuffer> {
	public ReadHeadStreamToBabble(InputStream $base) {
		super();
		this.$is = $base;
	}
	
	private final InputStream			$is;
	
	protected ByteBuffer getChunk() throws IOException {
		// figure out what length of message we expect
		byte[] $preint = new byte[4];
		int $k = $is.read($preint);
		if ($k != 4) throw new IOException("malformed babble -- message length header not read");
		int $messlen = Primitives.intFromByteArray($preint);
		if ($messlen < 1) throw new IOException("malformed babble -- negative message length header");
		
		// get the message
		byte[] $buf = new byte[$messlen];
		int $p = 0;
		while ($p < $messlen) {
			$k = $is.read($buf,$p,$buf.length-$p);
			if ($k == -1) break;
			$p += $k;
		}
		if ($p != $messlen) throw new IOException("babble of unexpected length");
		
		return ByteBuffer.wrap($buf);
	}
	
	public void close() throws IOException {
		$is.close();
	}	
}
