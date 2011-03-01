package ahs.crypto.bc.mod;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.params.*;

/**
 * Purpose is simple: override the initization method on AESFastEngine so that if the same
 * key material was used last time, we don't waste time discarding the old key schedule
 * and generating a new one that's exactly the same. Why BC wasn't smart enough to do this
 * itself I do not know.
 * 
 * This same-key check is implemented as a pointer-equality check on the byte array in the
 * KeyParameter (which we presume is what CipherParameters is; if not, we let the BC AES
 * engine scream)... which means in order to gain the efficiency here, the calling code
 * must either be caching that KeyParameter object, or use the hacked KeyParameter
 * subclass I made to allow avoidance of that ridiculous array copy in the constructor of
 * the original KeyParameter. However, this also means that if you ever mutate that byte
 * array, you're going to get yourself in a world of trouble, because now the engine won't
 * notice, and it's not going to update its key schedule even though you probably think
 * you gave it a new key, and boy are you going to be in for a sorry surprise.
 * 
 * Also, though encryption and decryption mode both use the same key schedule in AES,
 * switching the between these two modes does unfortunately still require regenerating the
 * key schedule, due to limits on what I can really do here with this extending hack.
 * 
 * @author hash
 */
public class AESEngineMod extends AESFastEngine {
	private byte[]	$lastKey;
	private boolean	$lastEdMode;	// and can you believe they don't even bother to have a function for asking if it's in encrypt mode or not?!  jesus.
					
	public void init(boolean $forEncryption, CipherParameters $params) {
		tryToHelp: {
			if ($lastEdMode != $forEncryption) break tryToHelp;
			if (!($params instanceof KeyParameter)) break tryToHelp;
			if ($lastKey != ((KeyParameter) $params).getKey()) break tryToHelp;
			return;
		}
		super.init($forEncryption, $params); // this is just going to throw anyway, but i'll let them do it rather than replicate the exception here.
	}
}
