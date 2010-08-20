package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * Translates ByteBuffer into EonObject by decorating the ReadHead&lt;ByteBuffer&gt; given
 * in the constructor; all read buffering remains at the level below the decorator (i.e.
 * outside of this class). Exception reporting at the two levels is separate, but
 * listeners are not; it is assumed that any event in the decorated ReadHead should be
 * reported to the listener of this ReadHead, and as such the constructor sets the
 * Listener of the decorated ReadHead.
 * 
 * @author hash
 * 
 */
public class EonReadHead implements ReadHead<EonObject> {
	// a lot of these methods end up trying to make it possible to do everything you could ever want without having to touch a single referrence to the decorated obj.
	// ...but that's kinda farcical, because you can't ever make a good interface for doing the exceptions
	
	public EonReadHead(ReadHead<ByteBuffer> $bio, EonCodec $co) {
		this.$bio = $bio;
		this.$co = $co;
	}
	
	private final ReadHead<ByteBuffer>	$bio;
	private final EonCodec			$co;
	private Listener<ReadHead<EonObject>>	$el;
	private ExceptionHandler<IOException>	$eh;
	
	protected EonObject convert(ByteBuffer $bb) throws TranslationException {
		if ($bb == null) return null;
		return $co.deserialize($bb.array());
	}
	
	/**
	 * @param $bbs
	 * @return the list of converted objects, excluding any who threw exceptions in
	 *         their individual conversion process.
	 */
	protected List<EonObject> convertAll(List<ByteBuffer> $bbs) {
		final int $s = $bbs.size();
		List<EonObject> $v = new ArrayList<EonObject>($s);
		for (int $i = 0; $i < $s; $i++)
			try {
				$v.add(convert($bbs.get($i)));
			} catch (TranslationException $e) {
				report($e);		//FIXME:AHS: consider the sync properties of the methods this is used in.  can listeners be hit by multiple threads at once?
			}
		return $v;
	}
	
	protected void report(IOException $ioe) {
		Listener<ReadHead<EonObject>> $dated_el = $el;
		if ($dated_el != null) $dated_el.hear(this);
	}
	
	/**
	 * Returns the decorated ReadHead's pump.
	 */
	public Pump getPump() {
		return $bio.getPump();
	}
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		this.$eh = $eh;
	}

	public void setListener(Listener<ReadHead<EonObject>> $el) {
		
	}

	public EonObject read() {
		//TODO
		return null;
	}

	public EonObject readNow() {
		try {
			return convert($bio.readNow());
		} catch (TranslationException $e) {
			report($e);
			return null;
		}
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
