package ahs.io.codec.eon;

import ahs.io.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Pretty much just a home for factories at this point.
 * 
 * @author hash
 * 
 */
public final class EonWriteHead {
	public static WriteHead<EonObject> make(WritableByteChannel $wbc, EonCodec $co) {
		return WriteHeadAdapter.make(
				$wbc,
				TranslatorStack.make(
						new Eon.TranslatorToByteBuffer($co),
						new WriteHeadAdapter.ChannelwiseUnbuffered.BabbleTranslator()
				)
		);
	}
	
	
	
	
	
	private EonWriteHead() {}
}
