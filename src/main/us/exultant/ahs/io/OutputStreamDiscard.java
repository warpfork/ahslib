package us.exultant.ahs.io;

import java.io.*;

/**
 * <p>
 * Ever wanted an effective <tt>/dev/null</tt> in portable java? Here ya go.
 * </p>
 * 
 * <p>
 * See also {@link IOForge#silentOutputStream} for a singleton instance of this &mdash
 * there's very little point in ever creating more than one instance of this class, seeing
 * as how it really truly does nothing at all.
 * </p>
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
