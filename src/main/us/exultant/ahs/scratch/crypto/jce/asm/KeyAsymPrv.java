package us.exultant.ahs.scratch.crypto.jce.asm;

import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.scratch.crypto.jce.est.*;

import java.security.*;
import javax.crypto.*;

public interface KeyAsymPrv extends PrivateKey {
	public static abstract class KeyWrapper<$BODY extends PrivateKey> extends KeySystem.KeyWrapper<$BODY> implements KeyAsymPrv {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
