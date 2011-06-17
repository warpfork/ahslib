package us.exultant.ahs.scratch.crypto.jce.est.dh;

import us.exultant.ahs.scratch.crypto.jce.est.*;

import java.math.*;
import java.security.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

public class KeyDhPrv extends KeyEstPrv.KeyWrapper<DHPrivateKey> implements DHPrivateKey {
	public KeyDhPrv(DHPrivateKey $k) {
		super($k);
	}
	
	public BigInteger getX() {
		return $k.getX();
	}
	
	public DHParameterSpec getParams() {
		return $k.getParams();
	}
}
