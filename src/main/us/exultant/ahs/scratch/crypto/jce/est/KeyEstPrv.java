package us.exultant.ahs.scratch.crypto.jce.est;

import us.exultant.ahs.scratch.crypto.jce.*;
import java.security.*;

public interface KeyEstPrv extends PrivateKey {
	public static abstract class KeyWrapper<$BODY extends PrivateKey> extends KeySystem.KeyWrapper<$BODY> implements KeyEstPrv {
		public KeyWrapper($BODY $k) {
			super($k);
		}
	}
}
