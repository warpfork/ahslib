package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.eon.*;
import java.nio.channels.*;

/**
 * Pretty much just a home for factories at this point.
 * 
 * FIXME: this functionality clearly belongs in IO more than codec... IO doesn't depend on codec yet, but i'm not opposed to allowing it to do so.
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
