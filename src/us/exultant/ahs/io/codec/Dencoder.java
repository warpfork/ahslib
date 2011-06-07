package us.exultant.ahs.io.codec;

public interface Dencoder<$CODEC extends Codec<$CODEC,$CODE>, $CODE,$TARG> extends Encoder<$CODEC,$CODE,$TARG>, Decoder<$CODEC,$CODE,$TARG> {
	// yep, empty.  it's just grouping.
}
