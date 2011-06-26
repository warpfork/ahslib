//TODO:AHS:REFACTOR: these convenience methods can no longer exist here because of the modularization requirements.
//package us.exultant.ahs.io;
//
//import us.exultant.ahs.core.*;
//import us.exultant.ahs.codec.eon.*;
//import java.nio.channels.*;
//
///**
// * Pretty much just a home for factories at this point.
// * 
// * @author hash
// * 
// */
//public final class EonWriteHead {
//	public static WriteHead<EonObject> make(WritableByteChannel $wbc, EonCodec $co) {
//		return WriteHeadAdapter.make(
//				$wbc,
//				TranslatorStack.make(
//						new Eon.TranslatorToByteBuffer($co),
//						new WriteHeadAdapter.ChannelwiseUnbuffered.BabbleTranslator()
//				)
//		);
//	}
//	
//	
//	
//	
//	
//	private EonWriteHead() {}
//}
