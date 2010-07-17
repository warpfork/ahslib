package ahs.io.codec;

import ahs.io.*;
import ahs.io.codec.Codec.*;
import ahs.json.*;
import ahs.util.*;

import java.util.*;

public class CodecImpl<$CODE> implements Codec<$CODE> {
	public CodecImpl() {
		$ed = new EncoderDispatch<$CODE>();
		$dd = new DecoderDispatch<$CODE>();
	}
	
	private EncoderDispatch<$CODE> $ed;
	private DecoderDispatch<$CODE> $dd;
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Encoder<$CODE, $TARG> $encoder) {
		$ed.putHook($datclrclass, $encoder);
	}
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Decoder<$CODE, $TARG> $decoder) {
		$dd.putHook($datclrclass, $decoder);
	}
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Dencoder<$CODE, $TARG> $dencoder) {
		putHook($datclrclass, (Encoder<$CODE, $TARG>)$dencoder);
		putHook($datclrclass, (Decoder<$CODE, $TARG>)$dencoder);
	}
	
	public <$TARG> $CODE encode($TARG $datclr) throws TranslationException {
		return $ed.encode(this, $datclr);
	}
	
	public <$TARG> $TARG decode($CODE $datenc, Class<$TARG> $datclrclass) throws TranslationException {
		try {
			return $dd.decode(this, $datenc, $datclrclass);
		} catch (TranslationException $e) {
			throw new TranslationException("Decoding failed for class "+$datclrclass.getName()+".", $e);
		}
	}
	
	
	
	public static class Brutemux<$C> {
        	public <$T> void putHook(Class<$T> $c, Listener<$T> $d) {
        		$hooks.put($c, $d);
        	}
        	
		private Map<Class<?>,Listener<?>>	$hooks	= new HashMap<Class<?>,Listener<?>>();
		
		public boolean disbatch(Codec<$C> $codec, $C $x) throws TranslationException {
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
