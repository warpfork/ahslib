package ahs.crypto.bc;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;
import ahs.crypto.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

/**
 * Provides confidentiality, but not authentication or integrity.
 * 
 * Uses AES, CTR mode, and PKCS7 padding.
 * 
 * @author hash
 *
 */
public class SymEnc_Aes implements CryptoContainer {
	public void wrap(byte[] $cleartext, ParametersWithIV $key) {
		// build the system
		BufferedBlockCipher $cipher = new PaddedBufferedBlockCipher(
				new SICBlockCipher(new AESFastEngine()),
				new PKCS7Padding()
		);
		
		// init the bitch
		$cipher.init(true, $key);
		
		// crunch the numbers
		byte[] $ciphertext = null;
		try {
			$ciphertext = BcUtil.invokeCipher($cipher, $cleartext);
		} catch (InvalidCipherTextException $e) {
			throw new MajorBug("This doesn't even make sense for encryption mode.", $e);
		}
		
		// victory
		this.$ciphertext = $ciphertext;
	}
	
	public byte[] unwrap(ParametersWithIV $key) throws DataLengthException, InvalidCipherTextException {
		// build the system
		BufferedBlockCipher $cipher = new PaddedBufferedBlockCipher(
				new SICBlockCipher(new AESFastEngine()),
				new PKCS7Padding()
		);
		
		// init the bitch
		$cipher.init(false, $key);
		
		// crunch the numbers
		byte[] $cleartext = BcUtil.invokeCipher($cipher, $ciphertext);
		
		// victory
		return $cleartext;
		
	}
	
	
	
	
	
	
	
	
	
	private SymEnc_Aes(byte[] $ciphertext) {
		this.$ciphertext = $ciphertext;
	}
	
	public byte[]	$ciphertext;
	
	/* BEGIN EON CODEC BLOCK */
	public static final Encoder<EonCodec,EonObject,SymEnc_Aes> ENCODER;
	public static final Decoder<EonCodec,EonObject,SymEnc_Aes> DECODER;
	static { EonDencoder $t = new EonDencoder(); ENCODER = $t; DECODER = $t; }
	public static class EonDencoder implements Dencoder<EonCodec,EonObject,SymEnc_Aes> {
		public EonObject encode(EonCodec $codec, SymEnc_Aes $x) throws TranslationException {
			if ($x.$ciphertext == null) throw new TranslationException("No ciphertext to encode!");
			return $codec.simple(SymEnc_Aes.class, null, $x.$ciphertext);
		}
		public SymEnc_Aes decode(EonCodec $codec, EonObject $x) throws TranslationException {
			$x.assertKlass(SymEnc_Aes.class);
			return new SymEnc_Aes($x.getByteData());
		}
	}
	/* END EON CODEC BLOCK */
	
}
