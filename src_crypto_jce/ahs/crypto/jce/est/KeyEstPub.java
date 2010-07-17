package ahs.crypto.jce.est;

import ahs.crypto.jce.*;

import java.security.*;
import javax.crypto.*;

public interface KeyEstPub extends PublicKey {
	public static abstract class KeyWrapper<$BODY extends PublicKey> extends KeySystem.KeyWrapper<$BODY> implements KeyEstPub {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
