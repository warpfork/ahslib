//TODO:AHS:REFACTOR: these convenience methods can no longer exist here because of the modularization requirements.
//
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
//public final class EonReadHead {
//	public static ReadHead<EonObject> make(ReadableByteChannel $rbc, EonCodec $co) {
//		return ReadHeadAdapter.make(
//				$rbc, 
//				TranslatorStack.make(
//						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
//						new Eon.TranslatorFromByteBuffer($co)
//				)
//		);
//	}
//	
//	public static ReadHead<EonObject> make(DatagramChannel $base, PumperSelector $ps, EonCodec $co) {
//		return ReadHeadAdapter.make(
//				$base,
//				$ps,
//				TranslatorStack.make(
//						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
//						new Eon.TranslatorFromByteBuffer($co)
//				)
//		);
//	}
//	
//	public static ReadHead<EonObject> make(SocketChannel $base, PumperSelector $ps, EonCodec $co) {
//		return ReadHeadAdapter.make(
//				$base,
//				$ps,
//				TranslatorStack.make(
//						new ReadHeadAdapter.Channelwise.BabbleTranslator(),
//						new Eon.TranslatorFromByteBuffer($co)
//				)
//		);
//	}
//	
//	
//	
//	
//	
//	private EonReadHead() {}
//}
