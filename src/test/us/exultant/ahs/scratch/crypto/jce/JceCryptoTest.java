package us.exultant.ahs.scratch.crypto.jce;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.log.*;
import us.exultant.ahs.scratch.crypto.jce.asm.rsa.*;
import us.exultant.ahs.scratch.crypto.jce.dig.*;
import us.exultant.ahs.scratch.crypto.jce.est.dh.*;
import us.exultant.ahs.scratch.crypto.jce.sym.aes.*;
import us.exultant.ahs.test.*;
import java.security.*;

public class JceCryptoTest extends TestCaseRetro {
	public JceCryptoTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	protected void runTests() throws Exception {
		testDigest();
		testRsaCipherSingleKey();
		testRsaCipherMultiKey();
		testRsaSign();
		testDhAgreement();
		testDhWithCipher();
		testDhWithMac();
		testRsaSerialization();
		testAesSerialization();
		testAesSerialization();
		testDhDerivedAesSerialization();
	}
	
	public void testDigest() {
		Digester $d = new DigesterSHA1();
		
		assertEquals(
				"86f7e437faa5a7fce15d1ddcb9eaeaea377667b8".toUpperCase(),
				Strings.toHex($d.digest("a".getBytes(Strings.UTF_8)))
		);

		assertEquals(
				"c0b4bb5bca9649acd274c970f954aa21700e97e7".toUpperCase(),
				Strings.toHex($d.digest("i am a beautiful bunny.".getBytes(Strings.UTF_8)))
		);
	}
	
	public static final byte[] PLAIN1 = new byte[] { 0x0, 0x1, 0x2, 0x3, 0x4 };
	public static final byte[] PLAIN2 = new byte[] { 0x6, 0x2, 0x4, 0x3, 0x4, 0x0, 0x1, 0x2 };
	
	public void testAesWithCipher() throws GeneralSecurityException {
		KeySystemSymAes $aessys = new KeySystemSymAes();
		
		KeyAes $k1 = $aessys.generateKey();
		KeyAes $k2 = $aessys.generateKey();
		
		byte[] $e1 = $aessys.encrypt(PLAIN1, $k1);
		byte[] $e2 = $aessys.encrypt(PLAIN2, $k2);
		byte[] $p1 = $aessys.decrypt($e1,    $k1);
		byte[] $p2 = $aessys.decrypt($e2,    $k2);
		assertEquals(PLAIN1, $p1);
		assertEquals(PLAIN2, $p2);
		assertNull($aessys.decrypt($e1, $k2));
	}
	
	public void testRsaCipherSingleKey() {
		KeySystemAsymRsa $rsasys = new RSA1024();
		
		Tup2<KeyRsaPub, KeyRsaPrv> $kp;
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k1o = $kp.getA();
		KeyRsaPrv $k1x = $kp.getB();
		
		byte[] $e1 = $rsasys.encrypt(PLAIN1, $k1o);
		byte[] $p1 = $rsasys.decrypt($e1,    $k1x);
		assertEquals(Strings.toHex(PLAIN1), Strings.toHex($p1));
	}
	
	public void testRsaCipherMultiKey() {
		KeySystemAsymRsa $rsasys = new RSA1024();
		
		Tup2<KeyRsaPub, KeyRsaPrv> $kp;
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k1o = $kp.getA();
		KeyRsaPrv $k1x = $kp.getB();
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k2o = $kp.getA();
		KeyRsaPrv $k2x = $kp.getB();
		
		byte[] $e1 = $rsasys.encrypt(PLAIN1, $k1o);
		byte[] $e2 = $rsasys.encrypt(PLAIN2, $k2o);
		byte[] $p1 = $rsasys.decrypt($e1,    $k1x);
		byte[] $p2 = $rsasys.decrypt($e2,    $k2x);
		assertEquals(Strings.toHex(PLAIN1), Strings.toHex($p1));
		assertEquals(Strings.toHex(PLAIN2), Strings.toHex($p2));
	}
	
	public void testRsaSign() {
		KeySystemAsymRsa $rsasys = new RSA1024();
		
		Tup2<KeyRsaPub, KeyRsaPrv> $kp;
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k1o = $kp.getA();
		KeyRsaPrv $k1x = $kp.getB();
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k2o = $kp.getA();
		KeyRsaPrv $k2x = $kp.getB();

		assertTrue($rsasys.verify(PLAIN2, $rsasys.sign(PLAIN2, $k1x), $k1o));	// matching text and matching keys
		assertFalse($rsasys.verify(PLAIN1, $rsasys.sign(PLAIN2, $k1x), $k1o));	// no match text
		assertFalse($rsasys.verify(PLAIN2, $rsasys.sign(PLAIN2, $k1x), $k2o));	// no match keys
	}
	
	public void testDhAgreement() {
		KeySystemEstDh<KeyAes> $dhsys = new DH1024();
		
		Tup2<KeyDhPub, KeyDhPrv> $kp;
		$kp = $dhsys.generateKeys();
		KeyDhPub $k1o = $kp.getA();
		KeyDhPrv $k1x = $kp.getB();
		$kp = $dhsys.generateKeys();
		KeyDhPub $k2o = $kp.getA();
		KeyDhPrv $k2x = $kp.getB();
		
		assertEquals(
				$dhsys.reachSecret($k1x, $k2o),
				$dhsys.reachSecret($k2x, $k1o)
		);
		assertFalse(
				$dhsys.reachSecret($k1x, $k2o)	.equals(
				$dhsys.reachSecret($k2x, $k2o)
		));
	}
	
	public void testDhWithCipher() throws GeneralSecurityException {
		KeySystemEstDh<KeyAes> $dhsys = new DH1024();
		KeySystemSymAes $aessys = new KeySystemSymAes();
		
		Tup2<KeyDhPub, KeyDhPrv> $kp;
		$kp = $dhsys.generateKeys();
		KeyDhPub $k1o = $kp.getA();
		KeyDhPrv $k1x = $kp.getB();
		$kp = $dhsys.generateKeys();
		KeyDhPub $k2o = $kp.getA();
		KeyDhPrv $k2x = $kp.getB();
		
		KeyAes $k1s = $dhsys.reachSecret($k1x, $k2o);
		KeyAes $k2s = $dhsys.reachSecret($k2x, $k1o);
		
		byte[] $e1 = $aessys.encrypt(PLAIN1, $k1s);
		byte[] $e2 = $aessys.encrypt(PLAIN2, $k2s);
		byte[] $p1 = $aessys.decrypt($e1,    $k2s);
		byte[] $p2 = $aessys.decrypt($e2,    $k1s);
		assertEquals(Strings.toHex(PLAIN1), Strings.toHex($p1));
		assertEquals(Strings.toHex(PLAIN2), Strings.toHex($p2));
	}
	
	public void testDhWithMac() throws GeneralSecurityException {
		KeySystemEstDh<KeyAes> $dhsys = new DH1024();
		KeySystemSymAes $aessys = new KeySystemSymAes();
		
		Tup2<KeyDhPub, KeyDhPrv> $kp;
		$kp = $dhsys.generateKeys();
		KeyDhPub $k1o = $kp.getA();
		KeyDhPrv $k1x = $kp.getB();
		$kp = $dhsys.generateKeys();
		KeyDhPub $k2o = $kp.getA();
		KeyDhPrv $k2x = $kp.getB();
		
		KeyAes $k1s = $dhsys.reachSecret($k1x, $k2o);
		KeyAes $k2s = $dhsys.reachSecret($k2x, $k1o);
		
		assertTrue($aessys.verify(PLAIN1, $aessys.mac(PLAIN1, $k1s), $k1s));
		assertTrue($aessys.verify(PLAIN1, $aessys.mac(PLAIN1, $k1s), $k2s));
		assertFalse($aessys.verify(PLAIN2, $aessys.mac(PLAIN1, $k1s), $k2s));
	}
	
	public void testRsaSerialization() throws TranslationException {
		KeySystemAsymRsa $rsasys = new RSA1024();
		
		Tup2<KeyRsaPub, KeyRsaPrv> $kp;
		$kp = $rsasys.generateKeys();
		KeyRsaPub $k1o = $kp.getA();
		KeyRsaPrv $k1x = $kp.getB();
		
		assertEquals($k1o, $rsasys.decodePublicKey($rsasys.encode($k1o)));
		assertEquals($k1x, $rsasys.decodePrivateKey($rsasys.encode($k1x)));
	}
	
	public void testAesSerialization() throws TranslationException {
		KeySystemSymAes $aessys = new KeySystemSymAes();
		KeyAes $k = $aessys.generateKey();
		
		assertEquals($k, $aessys.decode($aessys.encode($k)));
	}
	
	public void testDhSerialization() throws TranslationException {
		KeySystemEstDh<KeyAes> $dhsys = new DH1024();
		
		Tup2<KeyDhPub, KeyDhPrv> $kp = $dhsys.generateKeys();
		KeyDhPub $k1o = $kp.getA();
		KeyDhPrv $k1x = $kp.getB();
		
		assertEquals($k1o, $dhsys.decodePublicKey($dhsys.encode($k1o)));
		assertEquals($k1x, $dhsys.decodePrivateKey($dhsys.encode($k1x)));
	}
	
	public void testDhDerivedAesSerialization() throws TranslationException {
		KeySystemEstDh<KeyAes> $dhsys = new DH1024();
		KeySystemSymAes $aessys = new KeySystemSymAes();
		
		KeyAes $k = $dhsys.reachSecret($dhsys.generateKeys().getB(), $dhsys.generateKeys().getA());
		
		assertEquals($k, $aessys.decode($aessys.encode($k)));
	}
}
