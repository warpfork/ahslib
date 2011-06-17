package us.exultant.ahs.scratch.crypto.jce.asm.rsa;

import us.exultant.ahs.scratch.crypto.jce.asm.*;

import java.security.interfaces.*;

/**
 * @author hash
 *
 */
public interface KeySystemAsymRsa extends KeySystemAsym<KeyRsaPub, KeyRsaPrv> {
	public static final String ALGORITHM = "RSA";
}
