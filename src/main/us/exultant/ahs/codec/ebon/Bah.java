package us.exultant.ahs.codec.ebon;

import java.io.*;

/** I need this class purely in order to get access to the backing byte array when i'm done writing without jumping through a heinously unnecessary copy operation. */
class Bah extends ByteArrayOutputStream {
	public Bah(int $size) { super($size); }
	
	/** DNMR */
	public byte[] getByteArray() { return buf; }
}