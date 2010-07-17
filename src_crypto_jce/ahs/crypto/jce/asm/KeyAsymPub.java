package ahs.crypto.jce.asm;

import ahs.crypto.jce.*;
import ahs.crypto.jce.est.*;

import java.security.*;
import javax.crypto.*;

public interface KeyAsymPub extends PublicKey {
	public static abstract class KeyWrapper<$BODY extends PublicKey> extends KeySystem.KeyWrapper<$BODY> implements KeyAsymPub {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
