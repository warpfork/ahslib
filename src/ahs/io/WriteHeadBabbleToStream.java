package ahs.io;

import ahs.util.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

public class WriteHeadBabbleToStream implements WriteHead<ByteBuffer> {
	public WriteHeadBabbleToStream(Socket $sock) throws IOException {
		this($sock.getOutputStream());
	}
	
	public WriteHeadBabbleToStream(OutputStream $base) {
		$os = $base;
		$closed = false;
	}
	
	private final OutputStream	$os;
	private boolean			$closed;
	
	public void write(ByteBuffer $chunk) throws IOException {
		boolean $t = true;
		int $len = $chunk.remaining();
		X.saye($len+" len; "+$chunk.arrayOffset()+$chunk.position()+" off; "+$chunk.array().length+" arrlen.");
		try {
			X.saye("fuck1");
			$os.write(Primitives.byteArrayFromInt($len));
			X.saye("fuck2");
			if ($chunk.hasArray())
				$os.write($chunk.array(), $chunk.arrayOffset()+$chunk.position(), $len);
			else
				$os.write(Arr.toArray($chunk));
			X.saye("fuck3");
			$os.flush();
			X.saye("fuck4");
			$t = false;
		} finally {
			$closed = $t;

			X.saye("ultrafuck");
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
		return $closed;
	}
	
	public void close() throws IOException {
		$closed = true;
		$os.close();
	}
}