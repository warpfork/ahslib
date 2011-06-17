package us.exultant.ahs.crypto;

public interface Ks extends Key {
	public byte[] getBytes();
	
	public static class Basic implements Ks {
		public Basic(byte[] $bats) {
			this.$bats = $bats;
		}
		
		private byte[] $bats;
		
		public byte[] getBytes() {
			return $bats;
		}
	}
}
