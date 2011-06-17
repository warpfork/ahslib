package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.Codec.*;

import java.util.*;

public class CodecImpl<$CODEC extends Codec<$CODEC, $CODE>, $CODE> implements Codec<$CODEC,$CODE> {
	/**
	 * Constructs a new Codec that contains no encode or decode hooks.
	 */
	public CodecImpl() {
		$ed = new EncoderDispatch<$CODEC, $CODE>();
		$dd = new DecoderDispatch<$CODEC, $CODE>();
	}
	
	private final EncoderDispatch<$CODEC,$CODE> $ed;
	private final DecoderDispatch<$CODEC,$CODE> $dd;
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Encoder<$CODEC, $CODE, $TARG> $encoder) {
		$ed.putHook($datclrclass, $encoder);
	}
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Decoder<$CODEC, $CODE, $TARG> $decoder) {
		$dd.putHook($datclrclass, $decoder);
	}
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Dencoder<$CODEC, $CODE, $TARG> $dencoder) {
		putHook($datclrclass, (Encoder<$CODEC, $CODE, $TARG>)$dencoder);
		putHook($datclrclass, (Decoder<$CODEC, $CODE, $TARG>)$dencoder);
	}

	@SuppressWarnings("unchecked")	// we're casting to the generic arg of the exact same thing.  it's FINE.
	public <$TARG> $CODE encode($TARG $datclr) throws TranslationException {
		return $ed.encode(($CODEC)this, $datclr);
	}
	
	@SuppressWarnings("unchecked")	// we're casting to the generic arg of the exact same thing.  it's FINE.
	public <$TARG> $CODE encode($TARG $datclr, Class<$TARG> $datclrclass) throws TranslationException {
		return $ed.encode(($CODEC)this, $datclr, $datclrclass);
	}
	
	@SuppressWarnings("unchecked")	// we're casting to the generic arg of the exact same thing.  it's FINE.
	public <$TARG> $TARG decode($CODE $datenc, Class<$TARG> $datclrclass) throws TranslationException {
		try {
			return $dd.decode(($CODEC)this, $datenc, $datclrclass);
		} catch (TranslationException $e) {
			throw new TranslationException("Decoding failed for class "+$datclrclass.getName()+".", $e);
		}
	}
	
	
	
	public static class Brutemux<$CO extends Codec<$CO,$C>, $C> {
        	public <$T> void putHook(Class<$T> $c, Listener<$T> $d) {
        		$hooks.put($c, $d);
        	}
        	
		private Map<Class<?>,Listener<?>>	$hooks	= new HashMap<Class<?>,Listener<?>>();
		
		public boolean disbatch(Codec<$CO,$C> $codec, $C $x) throws TranslationException {
			//for (Map.Entry<Class<?>,Listener<?>> $ent : $hooks.entrySet()) {
			for (Class<?> $c : $hooks.keySet()) {
				try {
					return disbatch($codec.decode($x, $c));
				} catch (TranslationException $e) { /* just go on and try the next. */ }
			}
			return false;
		}
		
		@SuppressWarnings("unchecked")	// yes, the following method is technically unsafe.  at runtime, it should be absolutely reliable.
		public <$T> boolean disbatch($T $x) {
			Listener<$T> $hook = (Listener<$T>)$hooks.get($x.getClass());
			if ($hook == null) return false; 
			$hook.hear($x);
			return true;
		}
	}
}
