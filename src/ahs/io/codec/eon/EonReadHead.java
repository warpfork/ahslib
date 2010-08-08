package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Translations ByteBuffer into EonObject.
 * 
 * @author hash
 * 
 */
public class EonReadHead implements ReadHead<EonObject> {
	public EonReadHead(ReadHead<ByteBuffer> $bio) {
		this.$bio = $bio;
	}
	
	private final ReadHead<ByteBuffer>	$bio;
	
	
	public Pump getPump() {
		return null;
	}

	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		// i just thought of a radicially new way of dealing with this.
		// perhaps i should have two pipes in that danged adapter... or rather a Pipe<Pair<$T, IOException>>
		// then reads can throw exceptions like they were their own happening now, and pumps can also still respond as needed
		// though i suppose you never have a $T and an exception in the same round
		// and you never really have multiple exceptions
		// except in translator setups like this where there's processing of the message beyond just byte io stuff
		// which is the more general case and thus the one we should be designing for
	}

	public void setListener(Listener<ReadHead<EonObject>> $el) {
		//TODO
		
	}

	public EonObject read() {
		//TODO
		return null;
	}

	public EonObject readNow() {
		//TODO
		return null;
	}

	public boolean hasNext() {
		//TODO
		return false;
	}

	public List<EonObject> readAll() {
		//TODO
		return null;
	}

	public List<EonObject> readAllNow() {
		//TODO
		return null;
	}

	public boolean isClosed() {
		//TODO
		return false;
	}

	public void close() throws IOException {
		//TODO
		
	}
	
}
