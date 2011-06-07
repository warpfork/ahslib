package ahs.crypto.jce.ibe.fak;

import ahs.crypto.jce.ibe.*;
import ahs.io.codec.ebon.*;
import ahs.io.codec.eon.*;

/**
 * @author hash
 *
 */
public interface KeySystemIbeFak extends KeySystemIbe<KeyFakPub, KeyFakPrv> {
	public static final String ALGORITHM = "FAK";
	static final EonCodec HACK = new EbonCodec();	// gross
}
