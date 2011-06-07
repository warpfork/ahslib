package ahs.crypto.jce.est;

import ahs.crypto.jce.*;
import ahs.crypto.jce.sym.*;
import ahs.io.*;
import ahs.util.*;

import java.security.*;

import javax.crypto.interfaces.*;

public interface KeySystemEst<$KEYPUB extends KeyEstPub, $KEYPRV extends KeyEstPrv, $KEYEST extends KeySym> extends KeySystem  {
	public Pair<$KEYPUB,$KEYPRV> generateKeys();
	
	public $KEYEST reachSecret($KEYPRV $mykx, $KEYPUB $theirko);
	
	
	
	public byte[] encode($KEYPUB $ko);
	
	public $KEYPUB decodePublicKey(byte[] $koe) throws TranslationException;
	
	public byte[] encode($KEYPRV $kx);
	
	public $KEYPRV decodePrivateKey(byte[] $kxe) throws TranslationException;
}
