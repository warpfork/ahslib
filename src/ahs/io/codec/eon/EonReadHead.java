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
public class EonReadHead extends ReadHeadTranslator<ByteBuffer, EonObject> {
	public EonReadHead(ReadHead<ByteBuffer> $bio, EonCodec $co) {
		super($bio, new ByteBufferToEonTranslator($co));
	}
}
