package ahs.io;

import java.io.*;

/**
 * Ever wanted an effective /dev/null in portable java? Here ya go.
 * 
 * @author hash
 * 
 */
public final class OutputStreamDiscard extends OutputStream {
	public OutputStreamDiscard() {
		
	}
	
	
	public void close(){
		; // shh.
	}

	
	public void flush() {
		; // shh.
	}

	public void write(int $arg0) {
		; // shh.
	}

	
	public void write(byte[] $b, int $off, int $len) {
		; // shh.
	}

	
	public void write(byte[] $b) throws IOException {
		; // shh.
	}

	
	public boolean equals(Object $obj) {
		return ($obj instanceof OutputStreamDiscard);
	}

	
	public int hashCode() {
		return 1;
	}
}
