package us.exultant.ahs.scratch.crypto.jce.asm;

import us.exultant.ahs.scratch.crypto.jce.*;
import java.security.*;

public interface KeyAsymPub extends PublicKey {
	public static abstract class KeyWrapper<$BODY extends PublicKey> extends KeySystem.KeyWrapper<$BODY> implements KeyAsymPub {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
