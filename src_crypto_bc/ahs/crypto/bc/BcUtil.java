package ahs.crypto.bc;

import ahs.util.*;

import org.bouncycastle.crypto.*;

public class BcUtil {
	/**
	 * @param $cipher must already be initialized.  May be in either encrypt or decrypt mode.
	 * @param $bletch bytes to work on.  will not be modified.
	 * @return the processed bytes (in new memory).
	 * @throws DataLengthException if the input is not block size aligned and should be.
	 * @throws IllegalStateException if the underlying cipher is not initialized.
	 * @throws InvalidCipherTextException if padding is expected and not found.
	 */
	static byte[] invokeCipher(BufferedBlockCipher $cipher, byte[] $bletch) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
	        // crunch the numbers
		int $size = $cipher.getOutputSize($bletch.length);
		byte[] $park = new byte[$size];
		int $olen = $cipher.processBytes($bletch, 0, $bletch.length, $park, 0);		// we're quite confident there's no DataLengthException from here, but there is a possibility on the next line.
		$olen += $cipher.doFinal($park, $olen);						// there's no super easy way to tell in this context whether or not input must be block aligned.
		
		if ($olen < $size)	// $cipher.getOutputSize(*) lied to us!  now we have to make a new smaller array so we aren't returning evil nulls :(
			Arr.copyFromBeginning($park, $olen);
		
		return $park;
	}
}
