package ahs.io;

import java.io.*;

import ahs.util.*;

/**
 * Babble is a basic encoding scheme for moving strings across a binary transport.
 * 
 * @author hash
 * 
 */
@Deprecated
public class Babble extends java.io.ByteArrayOutputStream {
	public Babble() {
		// t's coo, we chll.
	}
	
	public Babble(String $x) {
		write($x);
	}
	
	public Babble(InputStream $is) throws IOException {
		receive($is);
	}
	
	public void receive(InputStream $is) throws IOException {
		// figure out what length of message we expect
		byte[] $preint = new byte[4];
		int $k = $is.read($preint);
		if ($k != 4) throw new IOException("malformed babble -- message length header not read");
		int $messlen = Primitives.intFromByteArray($preint);
		if ($messlen < 1) throw new IOException("malformed babble -- negative message length header");
		if ($is.read() != '\n') throw new IOException("malformed babble -- leading break not read");
		
		// get the message
		byte[] $buf = new byte[$messlen];
		int $p = 0;
		while ($p < $messlen) {
			$k = $is.read($buf,$p,$buf.length-$p);
			$p += $k;
		}
		if ($p != $messlen) throw new IOException("babble of unexpected length");
		this.write($buf);
		if ($is.read() != '\n') throw new IOException("malformed babble -- trailing break not read");
	}
	
	public void send(OutputStream $os) throws IOException {
		$os.write(Primitives.byteArrayFromInt(size()));
		$os.write((byte)'\n');
		$os.write(this.toByteArray());
		$os.write((byte)'\n');
		$os.flush();
	}
	
	public void write(String $s) {
		try {
			this.write($s.getBytes(Strings.UTF_8));
		} catch (IOException e) {}	// honestly not possible
	}
	
	public String read() {
		return new String(this.toByteArray(),Strings.UTF_8);
	}
}
