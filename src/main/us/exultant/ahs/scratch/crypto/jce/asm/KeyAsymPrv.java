package us.exultant.ahs.scratch.crypto.jce.asm;

import us.exultant.ahs.scratch.crypto.jce.*;
import java.security.*;

public interface KeyAsymPrv extends PrivateKey {
	public static abstract class KeyWrapper<$BODY extends PrivateKey> extends KeySystem.KeyWrapper<$BODY> implements KeyAsymPrv {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
