package us.exultant.ahs.scratch.crypto.jce.asm.rsa;

import us.exultant.ahs.scratch.crypto.jce.asm.*;

import java.math.*;
import java.security.interfaces.*;

public class KeyRsaPrv extends KeyAsymPrv.KeyWrapper<RSAPrivateKey> implements RSAPrivateKey {
	public KeyRsaPrv(RSAPrivateKey $k) {
		super($k);
	}
	
	public BigInteger getPrivateExponent() {
		return $k.getPrivateExponent();
	}
	
	public BigInteger getModulus() {
		return $k.getModulus();
	}
}
