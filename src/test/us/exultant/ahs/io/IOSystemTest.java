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
import us.exultant.ahs.test.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * General tests that any InputSystem and OutputSystem with matching framers attached to
 * connected channels should be able to pass... and only for networky stuff that uses
 * schedulers and selectors, because the signatures of the factory methods makes it hard
 * to unite tests for those with tests for simpler systems. Does not include tests for
 * framers like HTTP that have metadata as well as a straight data channel, because it's
 * impossible to generalize those.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class IOSystemTest extends TestCase {
	public static void main(String... $args) { new IOSystemTest().run(); }
	public List<Unit> getUnits() {
		return Arrays.asList(new Unit[] {
				new TestBasic(),
		});
	}
	
	
	
	abstract class TestTemplate extends TestCase.Unit {
		protected WorkScheduler $scheduler = new WorkSchedulerFlexiblePriority(8);
		protected SelectionSignaller $selector = new SelectionSignaller(0);
		private WorkScheduler $ssws = new WorkSchedulerFlexiblePriority(1).start();
		{ $selector.schedule($ssws, ScheduleParams.NOW); }
		
		/**
		 * call this at the end of a test in order to stop the schedulers that
		 * were created for this unit. failure to call this method results in
		 * wasted resources and threads outliving their usefulness, but is
		 * otherwise not actually very important.
		 */
		protected void cleanup() {
			$ssws.stop(true);
			$scheduler.stop(true);
		}
		
		protected Tup2<SocketChannel,SocketChannel> makeSocketChannelPair() throws IOException {
			ServerSocketChannel $ssc = ServerSocketChannel.open();
			$ssc.socket().bind(null);
			SocketChannel $outsock = SocketChannel.open($ssc.socket().getLocalSocketAddress());
			SocketChannel $insock = $ssc.accept();
			$ssc.close();
			return new Tup2<SocketChannel,SocketChannel>($outsock, $insock);
		}
	}
	
	
	
	private class TestBasic extends TestTemplate {
		public Object call() throws IOException {
			// set up ye olde sockets to stuff to test against
			Tup2<SocketChannel,SocketChannel> $socks = makeSocketChannelPair();
			SocketChannel $outsock = $socks.getA();
			SocketChannel $insock = $socks.getB();
			
			// set up the input system!
			$log.debug("setting up InputSystem");
			DataPipe<ByteBuffer> $incomingPipe = new DataPipe<ByteBuffer>();
			InputSystem<ByteBuffer> $insys = InputSystem.setup(
					$scheduler,
					$selector,
					$incomingPipe.sink(),
					$insock,
					new ChannelReader.BinaryFramer()
			);
			
			// set up the output system!
			$log.debug("setting up OutputSystem");
			DataPipe<ByteBuffer> $outgoingPipe = new DataPipe<ByteBuffer>();
			OutputSystem<ByteBuffer> $outsys = OutputSystem.setup(
					$scheduler,
					$selector,
					$outgoingPipe.source(),
					$outsock,
					new ChannelWriter.BinaryFramer()
			);
			
			// make test messages
			ByteBuffer $m1 = ByteBuffer.wrap(new byte[] {0x10, 0x20, 0x30, 0x40, 0x50});
			
			// start scheduler behind the IO systems
			$scheduler.start();
			
			// do some writes
			$log.debug("writing chunks...");
			$outgoingPipe.sink().write($m1);
			
			// do some reads, and make assertion
			$log.debug("reading chunks...");
			assertEquals($m1.array(), $incomingPipe.source().read().array());
			
			cleanup();
			return null;
		}
	}
}
