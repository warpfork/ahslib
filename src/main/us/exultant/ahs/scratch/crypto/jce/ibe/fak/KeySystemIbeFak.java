package us.exultant.ahs.scratch.crypto.jce.ibe.fak;

import us.exultant.ahs.scratch.crypto.jce.ibe.*;
import us.exultant.ahs.codec.ebon.*;
import us.exultant.ahs.codec.eon.*;

/**
 * @author hash
 *
 */
public interface KeySystemIbeFak extends KeySystemIbe<KeyFakPub, KeyFakPrv> {
	public static final String ALGORITHM = "FAK";
	static final EonCodec HACK = new EbonCodec();	// gross
}
