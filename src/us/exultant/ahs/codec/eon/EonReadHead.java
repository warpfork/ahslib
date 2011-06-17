package us.exultant.ahs.codec.eon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.io.*;
import java.nio.channels.*;

/**
 * Pretty much just a home for factories at this point.
 * 
 * FIXME: this functionality clearly belongs in IO more than codec... IO doesn't depend on codec yet, but i'm not opposed to allowing it to do so.
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
	
	public static ReadHead<EonObject> make(DatagramChannel $base, PumperSelector $ps, EonCodec $co) {
		return ReadHeadAdapter.make(
				$base,
				$ps,
				TranslatorStack.make(
						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
						new Eon.TranslatorFromByteBuffer($co)
				)
		);
	}
	
	public static ReadHead<EonObject> make(SocketChannel $base, PumperSelector $ps, EonCodec $co) {
		return ReadHeadAdapter.make(
				$base,
				$ps,
				TranslatorStack.make(
						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
						new Eon.TranslatorFromByteBuffer($co)
				)
		);
	}
	
	
	
	
	
	private EonReadHead() {}
}
