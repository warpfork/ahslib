package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
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
public final class EonReadHead {
	public static ReadHead<EonObject> make(ReadableByteChannel $rbc, EonCodec $co) {
		return ReadHeadAdapter.make(
				$rbc, 
				TranslatorStack.make(
						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
						new Eon.TranslatorFromByteBuffer($co)
				)				
		);
	}
	
	
	
	
	
	private EonReadHead() {}
}
