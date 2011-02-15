package ahs.crypto.bc;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

public class SymEncMac_AesSha1 {
	public static SymEncMac_AesSha1 wrap(byte[] $cleartext, KeyParameter $key) {
		// build the system
		BufferedBlockCipher $cipher = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(new AESFastEngine()),
				new PKCS7Padding()
		);
		
		// init the bitch
		// FIXME:AHS:CRYPTO: whether or not you have an IV is actually determined right inside of here by instanceof, which i find totally insane.   ParametersWithIV doesn't extend KeyParameter at least, so that's sane.
		//		p.s. IV's tend to get eaten by the BlockCipher component, and never noticed by the Engine... or at least i think that's how BC is organized.
	        $cipher.init(true, $key);
		
	        // crunch the numbers
		int $size = $cipher.getOutputSize($cleartext.length);
		byte[] $ciphertext = new byte[$size];
		int $olen = $cipher.processBytes($cleartext, 0, $cleartext.length, $ciphertext, 0);
		try {
			$olen += $cipher.doFinal($ciphertext, $olen);
		} catch (InvalidCipherTextException $e) {
			throw new MajorBug("Not only does this not even make sense for encryption mode, it's not even actually possible in the BC source last time I checked.", $e);
		}
		
		if ($olen < $size)	// $cipher.getOutputSize(*) lied to us!  now we have to make a new smaller array so we aren't returning evil nulls :(
			Arr.copyFromBeginning($ciphertext, $olen);
	        
		// victory
	        return new SymEncMac_AesSha1($ciphertext);
	}
	
	// so what i want is a way to have all of this setup things of the ciphers and padding and etc be encoded quickly, and also demuxable easily.
	// and my muxing has historically been built on class names, but within the crypto package i can do things differently and have things demux to cryptocontainer_sym or something and THEN the setup is...more so.
	
	// my api: you're never allowed to not have an IV, and it has to be its own arg.  primary reason: doesn't make any god damn sense to serialize the iv and the key together, so they shouldnt be in a type together either.
	
	
	
	// favored modes:
	//	SICBlockCipher		CTR
	//	CBCBlockCipher
	//	OFBBlockCipher
	//	CFBBlockCipher
	
	
	
	
	
	
	
	
	private SymEncMac_AesSha1(byte[] $ciphertext) {
		this.$ciphertext = $ciphertext;
	}
	
	public byte[]	$ciphertext;
	
	/* BEGIN EON CODEC BLOCK */
	public static final Encoder<EonCodec,EonObject,SymEncMac_AesSha1> ENCODER;
	public static final Decoder<EonCodec,EonObject,SymEncMac_AesSha1> DECODER;
	static { EonDencoder $t = new EonDencoder(); ENCODER = $t; DECODER = $t; }
	public static class EonDencoder implements Dencoder<EonCodec,EonObject,SymEncMac_AesSha1> {
		public EonObject encode(EonCodec $codec, SymEncMac_AesSha1 $x) throws TranslationException {
			return $codec.simple(SymEncMac_AesSha1.class, null, $x.$ciphertext);
		}
		public SymEncMac_AesSha1 decode(EonCodec $codec, EonObject $x) throws TranslationException {
			$x.assertKlass(SymEncMac_AesSha1.class);
			return new SymEncMac_AesSha1($x.getByteData());
		}
	}
	/* END EON CODEC BLOCK */
	
}
