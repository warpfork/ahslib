package us.exultant.ahs.scratch.crypto.jce.est;

import us.exultant.ahs.scratch.crypto.jce.*;
import java.security.*;

public interface KeyEstPub extends PublicKey {
	public static abstract class KeyWrapper<$BODY extends PublicKey> extends KeySystem.KeyWrapper<$BODY> implements KeyEstPub {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
