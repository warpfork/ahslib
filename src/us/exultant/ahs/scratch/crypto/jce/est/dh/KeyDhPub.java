package us.exultant.ahs.scratch.crypto.jce.est.dh;

import us.exultant.ahs.scratch.crypto.jce.est.*;

import java.math.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

public class KeyDhPub extends KeyEstPub.KeyWrapper<DHPublicKey> implements DHPublicKey {
	public KeyDhPub(DHPublicKey $k) {
		super($k);
	}
	
	public BigInteger getY() {
		return $k.getY();
	}
	
	public DHParameterSpec getParams() {
		return $k.getParams();
	}
}