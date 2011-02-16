package ahs.crypto.bc;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.macs.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

public class SymEncMac_AesSha1 {	// this naming scheme... make sure it's looking forward to discrimination between enc-then-mac and mac-then-enc.  (i don't expect i'll ever do the "and" pattern.)
	public static SymEncMac_AesSha1 wrap(byte[] $cleartext, ParametersWithIV $key) {
		// build the system
		BufferedBlockCipher $cipher = new PaddedBufferedBlockCipher(
				new CBCBlockCipher(new AESFastEngine()),
				new PKCS7Padding()
		);
		
		HMac $hmac = new HMac(new SHA1Digest());
		
		
		// init the bitch
	        $cipher.init(true, $key);
	        $hmac.init($key);	//FIXME:AHS:CRYPTO: there's a serious issue here with key reuse to address soon, obviously.
	        
	        // crunch the numbers
	        byte[] $ciphertext = null;
		try {
			$ciphertext = BcUtil.invokeCipher($cipher, $cleartext);
		} catch (InvalidCipherTextException $e) {
			throw new MajorBug("This doesn't even make sense for encryption mode.", $e);
		}
		byte[] $resBuf = new byte[$hmac.getMacSize()];
		$hmac.update($ciphertext, 0, $ciphertext.length);
		$hmac.doFinal($resBuf, 0);
		
		// victory
	        return new SymEncMac_AesSha1($ciphertext, $resBuf);
	}
	
	// engine class must remember the last key used
	// can we make one single cleartext type with all appropiate methods?
	//	no, because authentication is a complex question and has key type issues up the wazoo.
	//	for just sym enc and sym mac it would probs be doable though.
	// delayed computation of mac and such until asked for it: doesn't jive well with concept of a cleartext class.  jives better with just asking the engine (?).
	
	
	
	
	
	private SymEncMac_AesSha1(byte[] $ciphertext, byte[] $mac) {
		this.$ciphertext = $ciphertext;
		this.$mac = $mac;
	}
	
	public byte[]	$ciphertext;
	public byte[]	$mac;
	
	/* BEGIN EON CODEC BLOCK */
	public static final Encoder<EonCodec,EonObject,SymEncMac_AesSha1> ENCODER;
	public static final Decoder<EonCodec,EonObject,SymEncMac_AesSha1> DECODER;
	static { EonDencoder $t = new EonDencoder(); ENCODER = $t; DECODER = $t; }
	public static class EonDencoder implements Dencoder<EonCodec,EonObject,SymEncMac_AesSha1> {
		public EonObject encode(EonCodec $codec, SymEncMac_AesSha1 $x) throws TranslationException {
			EonObject $eo = $codec.newObj();
			$eo.putKlass(SymEncMac_AesSha1.class);
			$eo.putData($x.$ciphertext);
			$eo.put("m",$x.$mac);
			return $eo;
		}
		public SymEncMac_AesSha1 decode(EonCodec $codec, EonObject $x) throws TranslationException {
			$x.assertKlass(SymEncMac_AesSha1.class);
			return new SymEncMac_AesSha1($x.getByteData(), $x.getBytes("m"));
		}
	}
	/* END EON CODEC BLOCK */
	
}
