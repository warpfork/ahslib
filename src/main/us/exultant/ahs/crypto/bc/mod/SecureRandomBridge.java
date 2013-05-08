package us.exultant.ahs.crypto.bc.mod;

import us.exultant.ahs.util.*;
import java.security.*;
import org.bouncycastle.crypto.prng.*;

/**
 * Bridges the Bouncy Castle {@link RandomGenerator} interface to a functioning
 * {@link SecureRandom} implementation that can be dropped in to any cryptosystem.
 */
public class SecureRandomBridge extends SecureRandom {
	public SecureRandomBridge(RandomGenerator $engine) {
		super(new Spi($engine), new Provider($engine));
	}



	private static class Spi extends SecureRandomSpi {
		Spi(RandomGenerator $engine) {
			this.$engine = $engine;
		}

		private final RandomGenerator	$engine;

		protected byte[] engineGenerateSeed(int $arg0) {
			byte[] $bats = new byte[$arg0];
			$engine.nextBytes($bats);
			return $bats;
		}

		protected void engineNextBytes(byte[] $arg0) {
			$engine.nextBytes($arg0);
		}

		protected void engineSetSeed(byte[] $arg0) {
			$engine.addSeedMaterial($arg0);
		}
	}



	private static class Provider extends java.security.Provider {
		Provider(RandomGenerator $engine) {
			super("SecureRandomBridge", 1, Reflect.getShortClassName($engine));
		}
	}
}
