package us.exultant.ahs.codec.eon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.io.*;

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
