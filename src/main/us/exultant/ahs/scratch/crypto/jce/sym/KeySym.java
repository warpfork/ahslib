package us.exultant.ahs.scratch.crypto.jce.sym;

import us.exultant.ahs.scratch.crypto.jce.*;
import javax.crypto.*;

public interface KeySym extends SecretKey {
	public static abstract class KeyWrapper<$BODY extends SecretKey> extends KeySystem.KeyWrapper<$BODY> implements KeySym {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
