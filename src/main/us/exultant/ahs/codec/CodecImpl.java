/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;

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
}
