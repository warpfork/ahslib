package us.exultant.ahs.io;

import us.exultant.ahs.util.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * <p>
 * Note that if you attach multiple WriteHead to this WritableByteChannel and are using
 * them from different threads, you must implement locking outside of this WriteHead or
 * suffer. Consider simply making a Pipe in front of this WriteHead as a buffer.
 * </p>
 * 
 * <p>
 * This implementation does not make use of a selector system when doing writes, so beware
 * any channel that has a small buffer that may fill and refuse writes for long periods of
 * time, as this may cause undesireably long blocks of the write methods. Again, solve
 * this by placing a Pipe in front of this WriteHead as a buffer.
 * </p>
 * 
 * @author hash
 */
public class WriteHeadBabbleToChannel implements WriteHead<ByteBuffer> {
	public WriteHeadBabbleToChannel(WritableByteChannel $sock) {
		$base = $sock;
		$preint	= ByteBuffer.allocate(4);
	}
	
	private final WritableByteChannel	$base;
	private ByteBuffer			$preint;
	 
	public void write(ByteBuffer $chunk) throws IOException {
		$preint.clear();
		$preint.putInt($chunk.remaining());
		$preint.rewind();
		subwrite($preint);
		subwrite($chunk);
	}
	
	private void subwrite(ByteBuffer $lit) throws IOException {
		$base.write($lit);
		while ($lit.remaining() > 0) {
			X.chill(7);
			$base.write($lit);
		}
	}
	
	public void writeAll(Collection<? extends ByteBuffer> $chunks) throws IOException {
		for (ByteBuffer $chunk : $chunks)
			write($chunk);
	}
	
	public boolean hasRoom() {
		return true;
	}
	
	public boolean isClosed() {
		return !$base.isOpen();
	}
	
	public void close() throws IOException {
		$base.close();
	}
}
