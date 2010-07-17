package ahs.crypto.jce.dig;

/**
 * @author hash
 *
 */
public interface Digester {
	/**
	 * @param $x
	 * @return the hash
	 */
	public byte[] digest(byte[] $x);
	
	public byte[] digest(byte[]... $x);

	/**
	 * @return the number of bytes output by a particular implementation.
	 */
	public int getOutputSize();
}
