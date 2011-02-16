package ahs.crypto.jce.asm;

import ahs.crypto.jce.*;
import ahs.crypto.jce.est.*;

import java.security.*;
import javax.crypto.*;

public interface KeyAsymPrv extends PrivateKey {
	public static abstract class KeyWrapper<$BODY extends PrivateKey> extends KeySystem.KeyWrapper<$BODY> implements KeyAsymPrv {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
