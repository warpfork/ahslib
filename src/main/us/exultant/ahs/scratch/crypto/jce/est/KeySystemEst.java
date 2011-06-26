package us.exultant.ahs.scratch.crypto.jce.est;

import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.scratch.crypto.jce.sym.*;
import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;

import java.security.*;

import javax.crypto.interfaces.*;

public interface KeySystemEst<$KEYPUB extends KeyEstPub, $KEYPRV extends KeyEstPrv, $KEYEST extends KeySym> extends KeySystem  {
	public Tup2<$KEYPUB,$KEYPRV> generateKeys();
	
	public $KEYEST reachSecret($KEYPRV $mykx, $KEYPUB $theirko);
	
	
	
	public byte[] encode($KEYPUB $ko);
	
	public $KEYPUB decodePublicKey(byte[] $koe) throws TranslationException;
	
	public byte[] encode($KEYPRV $kx);
	
	public $KEYPRV decodePrivateKey(byte[] $kxe) throws TranslationException;
}
