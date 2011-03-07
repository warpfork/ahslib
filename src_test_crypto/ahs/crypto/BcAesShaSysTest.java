package ahs.crypto;

import ahs.io.*;
import ahs.log.*;
import ahs.test.*;
import ahs.util.*;
import ahs.util.Strings;
import ahs.crypto.bc.*;

import java.nio.*;
import java.util.*;
import org.bouncycastle.util.*;

public class BcAesShaSysTest extends TestCase {
	public static void main(String... $args) {
		new BcAesShaSysTest().run();
	}
	
	public BcAesShaSysTest() {
		super(new Logger(Logger.LEVEL_INFO), true);
	}
	
	public BcAesShaSysTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}

	protected void runTests() throws Exception {
		testAnything();
		testInvalidIv();
		testInvalidKey();
		testInvalidMacKey();
		testCiphertextConsistency();
		testKeyReuse();
	}
	
	private static final byte[]	$c1	= Strings.toBytes("oh god I'm covered in bees");
	private static final Ks		$ks1	= new Ks.Basic(new byte[] { 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F });
	private static final Ks		$ks2	= new Ks.Basic(new byte[] { 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, 0x2E, 0x2F });
	private static final Ks		$ks11	= new Ks.Basic(new byte[] { 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F });	// same as ks1 but not peq
	private static final Ks		$ksws	= new Ks.Basic(new byte[] { 0x33, 0x29, 0x58, 0x76 });										// weird size
	private static final Kc		$kc1	= new Kc(new byte[] { 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF });
	private static final Kc		$kc0	= new Kc(new byte[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 });
	private static final Kc		$kcws	= new Kc(new byte[] { 0x0, 0x0, 0x0 });											// weird size
	private static final byte[]	$c10m;
	static {
		Random $r = new java.security.SecureRandom();
		ByteBuffer $t = ByteBuffer.allocate(1024 * 1024 * 10);	// 10MB
		while ($t.hasRemaining())
			$t.putInt($r.nextInt());
		$c10m = $t.array();
	}
	
	private void testAnything() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		CiphertextSymmetric $cph = $sys.encrypt(
				$ks1,
				$kc1,
				$ks2,
				$c1
		);
	}
	
	private void testInvalidIv() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		try {
			CiphertextSymmetric $cph = $sys.encrypt(
					$ks1,
					$kcws,
					$ks2,
					$c1
			);
			exceptionExpected(ArrayIndexOutOfBoundsException.class);
		} catch (ArrayIndexOutOfBoundsException $e) {
			/* win */
		}
	}
	
	private void testInvalidKey() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		try {
			CiphertextSymmetric $cph = $sys.encrypt(
					$ksws,
					$kc1,
					$ks2,
					$c1
			);
			exceptionExpected(IllegalArgumentException.class);
		} catch (IllegalArgumentException $e) {
			/* win */
		}
	}
	
	private void testInvalidMacKey() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		CiphertextSymmetric $cph = $sys.encrypt(
				$ks1,
				$kc1,
				$ksws,
				$c1
		);
		// It's pretty hard to come up with something that actually qualifies as an invalid MAC key.
		// Having a MAC key longer than needed results in hashing and then trimming of the hash; having a MAC key shorter than needed results in padding with zeros.
		// For SHA1, the appropriate HMAC key length is 64 bytes. 
	}

	private void testCiphertextConsistency() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		CiphertextSymmetric $cph = $sys.encrypt(
				$ks1,
				$kc1,
				$ks2,
				$c1
		);
		assertEquals(
				$cph.getIv().getBytes(),
				$kc1.getBytes()
		);
		assertEquals(
				$cph.getBody(),
				Strings.fromHex("F33CF5161F48DA46249E4276081DCE0A5BF00A95F96284F753E524FCAFA9BDFE")
		);
		assertEquals(
				$cph.getMac(),
				Strings.fromHex("A2E025C02A21392EA17ADC2C0FE12CDC66F5C3C9")
		);
	}

	private void testKeyReuse() {
		AesCtrPkcs7Sha1 $sys = new AesCtrPkcs7Sha1();
		CiphertextSymmetric $cph1 = $sys.encrypt(
				$ks1,
				$kc1,
				$ks2,
				$c1
		);
		CiphertextSymmetric $cph2 = $sys.encrypt(
				$ks1,
				$kc1,
				$ks2,
				$c1
		);
		assertEquals(
				$cph1.getBody(),
				$cph2.getBody()
		);
	}
}
