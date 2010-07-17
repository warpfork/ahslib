package ahs.crypto.jce.sym.aes;

import ahs.crypto.jce.sym.*;

import javax.crypto.*;

public class KeyAes extends KeySym.KeyWrapper<SecretKey> {
	public KeyAes(SecretKey $k) {
		super($k);
		if (!$k.getAlgorithm().equals("AES")) throw new IllegalArgumentException("Must rap a SecretKey for the AES algorithm.");
	}
	
	public String getAlgorithm() {
		return "AES";
	}
}
