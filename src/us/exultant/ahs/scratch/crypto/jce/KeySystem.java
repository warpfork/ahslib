package us.exultant.ahs.scratch.crypto.jce;

import java.security.*;

/**
 * Just for grouping.
 * 
 * @author hash
 *
 */
public interface KeySystem {
	public static final String IMPOSSIBLE = "Really F'd up crypto environment.";
	
	
	
	public static abstract class KeyWrapper<$BODY extends Key> implements Key {
		public KeyWrapper($BODY $k) {
			this.$k = $k;
		}
		
		protected $BODY $k;
		
		/**
		 * @see java.security.Key#getAlgorithm()
		 */
		public String getAlgorithm() {
			return $k.getAlgorithm();
		}
		
		/**
		 * @see java.security.Key#getEncoded()
		 */
		public byte[] getEncoded() {
			return $k.getEncoded();
		}
		
		/**
		 * @see java.security.Key#getFormat()
		 */
		public String getFormat() {
			return $k.getFormat();
		}
		
		public boolean equals(Object $obj) {
			return $k.equals($obj);
		}
		
		public int hashCode() {
			return $k.hashCode();
		}
	}
}
