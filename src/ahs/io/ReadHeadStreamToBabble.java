package ahs.io;

import ahs.util.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

/**
 * <p>
 * This isn't really all that different from ReadHeadStreamToByteBuffer (obviously, since
 * the datatype it reads out to you is the same), but has the critical difference that
 * where ReadHeadStreamToByteBuffer just gives you blocks of a fixed size, this talks in
 * "babble"... variable length binary blobs, composed of a four-byte integer specifying
 * chunk size, followed by the data chunk itself of as many bytes as the interger
 * specifies.
 * </p>
 * 
 * <p>
 * If EOF is reached when only part of a chunk has been read, that partial chunk is
 * discarded and an IOException is thrown for unexpected end of stream.
 * </p>
 * 
 * @author hash
 * 
 */
@Deprecated()
public class ReadHeadStreamToBabble extends ReadHeadAdapterSimple<ByteBuffer> {
	public ReadHeadStreamToBabble(Socket $sock) throws IOException {
		this(new BufferedInputStream($sock.getInputStream()));
	}
	
	public ReadHeadStreamToBabble(InputStream $base) {
		super();
		this.$is = $base;
	}
	
	private final InputStream			$is;
	
	protected ByteBuffer getChunk() throws IOException {
		// figure out what length of message we expect
		byte[] $preint = new byte[4];
		int $k = $is.read($preint);
		if ($k == -1) baseEof();
		if ($k != 4) throw new IOException("malformed babble -- message length header not read");
		int $messlen = Primitives.intFromByteArray($preint);
		if ($messlen < 1) throw new IOException("malformed babble -- negative message length header");
		
		// get the message
		byte[] $buf = new byte[$messlen];
		int $p = 0;
		while ($p < $messlen) {
			$k = $is.read($buf,$p,$buf.length-$p);
			if ($k == -1) {
				baseEof();
				break;
			}
			$p += $k;
		}
		if ($p != $messlen) throw new EOFException("babble of unexpected length");
		
		return ByteBuffer.wrap($buf);
	}
	
	public void close() throws IOException {
		$is.close();
	}	
}
