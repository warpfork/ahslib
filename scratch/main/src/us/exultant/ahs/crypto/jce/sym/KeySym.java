package ahs.crypto.jce.sym;

import ahs.crypto.jce.*;

import java.security.*;
import javax.crypto.*;

public interface KeySym extends SecretKey {
	public static abstract class KeyWrapper<$BODY extends SecretKey> extends KeySystem.KeyWrapper<$BODY> implements KeySym {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
