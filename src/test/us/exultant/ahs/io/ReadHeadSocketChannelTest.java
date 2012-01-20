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

import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.channels.*;
import java.util.*;

public class ReadHeadSocketChannelTest extends TestCase {
	public static void main(String... $args) {					new ReadHeadSocketChannelTest().run();		}
	public ReadHeadSocketChannelTest() {						super(new Logger(Logger.LEVEL_DEBUG), true);	}
	public ReadHeadSocketChannelTest(Logger $log, boolean $enableConfirmation) {	super($log, $enableConfirmation);		}
	public List<Unit> getUnits() {
		List<Unit> $tests = new ArrayList<Unit>();
		$tests.add(new TestBasic());
		$tests.add(new TestSocketReceive());
		return $tests;
	}
	
	private class TestBasic extends TestCase.Unit {
		WorkTargetSelector $ps;
		ReadHeadSocketChannel $rhsc;
		public Object call() throws IOException {
			$ps = new WorkTargetSelector();
			WorkManager.getDefaultScheduler().schedule($ps, ScheduleParams.NOW);
			
			$rhsc = new ReadHeadSocketChannel(null, $ps, WorkManager.getDefaultScheduler());
			return null;
		}
	}
	
	private class TestSocketReceive extends TestBasic {
		SocketChannel $sca0;
		public Object call() throws IOException {
			super.call();
			
			$sca0 = SocketChannel.open();
			$sca0.connect($rhsc.getServerSocketChannel().getLocalAddress());
			
			SocketChannel $sca1 = $rhsc.read();	// this may block forever if something is wrong
			
			return null;
		}
	}
}
