package ahs.crypto.jce.est;

import ahs.crypto.jce.*;

import java.security.*;
import javax.crypto.*;

public interface KeyEstPrv extends PrivateKey {
	public static abstract class KeyWrapper<$BODY extends PrivateKey> extends KeySystem.KeyWrapper<$BODY> implements KeyEstPrv {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
