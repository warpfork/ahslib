package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Translates ByteBuffer into EonObject.
 * 
 * @author hash
 * 
 */
public class EonReadHead implements ReadHead<EonObject> {
	public EonReadHead(ReadHead<ByteBuffer> $bio, EonCodec $co) {
		this.$bio = $bio;
		this.$co = $co;
	}
	
	private final ReadHead<ByteBuffer>	$bio;
	private final EonCodec			$co;
	
	protected EonObject convert(ByteBuffer $bb) throws TranslationException {
		if ($bb == null) return null;
		return $co.deserialize($bb.array());
	}
	
	protected List<EonObject> convertAll(List<ByteBuffer> $bbs) {
		final int $s = $bbs.size();
		List<EonObject> $v = new ArrayList<EonObject>($s);
		for (int $i = 0; $i < $s; $i++)
			$v.add(convert($bbs.get($i)));
		return $v;
	}
	
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
		ByteBuffer $bb = $bio.readNow();
		return ($bb == null) ? $bb : new EonObject
		return null;
	}

	public boolean hasNext() {
		return $bio.hasNext();
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
		return $bio.isClosed();
	}

	public void close() throws IOException {
		$bio.close();
	}
	
}
