package ahs.crypto.jce.asm.rsa;

import ahs.crypto.jce.asm.*;

import java.math.*;
import java.security.interfaces.*;

public class KeyRsaPub extends KeyAsymPub.KeyWrapper<RSAPublicKey> implements RSAPublicKey {
	public KeyRsaPub(RSAPublicKey $k) {
		super($k);
	}

	public BigInteger getPublicExponent() {
		return $k.getPublicExponent();
	}

	public BigInteger getModulus() {
		return $k.getModulus();
	}
}
