package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Exception handlers are advised to deregisters the base channel from teh selector; this
 * can be done by telling the PumperSelector to deregister the pump from getPump().
 * 
 * @author hash
 * 
 */
public class ReadHeadChannelToBabble extends ReadHeadAdapter<ByteBuffer> {
	/**
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 */
	public ReadHeadChannelToBabble(DatagramChannel $base, PumperSelector $ps) {
		this($base);
		$ps.register($base, getPump());
	}
	
	/**
	 * @param $base
	 *                should already be connected and in a non-blocking state.
	 */
	public ReadHeadChannelToBabble(SocketChannel $base, PumperSelector $ps) {
		this($base);
		$ps.register($base, getPump());
	}
	
	private ReadHeadChannelToBabble(ByteChannel $base) {
		super();
		this.$base = $base;
		this.$messlen = -1;
	}
	
	private final ByteChannel	$base;
	private final ByteBuffer	$preint	= ByteBuffer.allocate(4);
	private int			$messlen;
	private ByteBuffer		$mess;
	
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
		//TODO:AHS: we should probably be cancelling the channel too, but so far we haven't needed to hold on to the pumperselector
	}
}
