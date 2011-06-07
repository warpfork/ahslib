package ahs.crypto;

public class Kc implements Key {
	public Kc(byte[] $bytes) {
		this.$bytes = $bytes;
	}
	
	public final byte[]	$bytes;

	public byte[] getBytes() {
		return this.$bytes;
	}
}
