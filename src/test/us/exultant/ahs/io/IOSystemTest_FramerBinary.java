/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.io;

import us.exultant.ahs.util.*;
import java.nio.*;
import java.util.*;

public class IOSystemTest_FramerBinary extends IOSystemTest<ByteBuffer> {	// a class just for BinaryMessages would be reasonable here.  er, or the others can extend this and just override some of the methods.
	public static void main(String... $args) { new IOSystemTest_FramerBinary().run(); }
	
	protected ChannelReader<ByteBuffer> defineReader() {
		return new ChannelReader.BinaryFramer();
	}
	
	protected ChannelWriter<ByteBuffer> defineWriter() {
		return new ChannelWriter.BinaryFramer();
	}
	
	protected ByteBuffer defineTestMessage1() {
		return ByteBuffer.wrap(new byte[] {0x10, 0x20, 0x30, 0x40, 0x50});
	}
	
	protected ByteBuffer defineTestMessage2() {
		return ByteBuffer.wrap(new byte[] {0x70, 0x7F, 0x10, 0x00, -0x1});
	}
	
	protected ByteBuffer defineTestMessage3() {
		return ByteBuffer.wrap(new byte[] {0x70, 0x7F, 0x10, 0x00, -0x80, 0x00, -0x1, 0x7F, 0x0A, 0x0D, 0x00, 0x65, 0x30, 0x40, 0x70});
	}
	
	private final ByteBuffer $big = ByteBuffer.allocate(1024 * 1024 * 10); { Random $r = new Random(); while ($big.hasRemaining()) $big.putInt($r.nextInt()); $big.rewind(); }
	
	protected ByteBuffer defineTestMessageBig() {
		return ByteBuffer.wrap(Arr.copy($big.array()));
	}
}
