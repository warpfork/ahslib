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

package us.exultant.ahs.test;

import us.exultant.ahs.util.*;
import us.exultant.ahs.test.junit.*;

public class WaitTest extends JUnitTestCase {
	public void doLoop(final int $pause, final int $times, final int $error) {
		long $start = X.time();
		for (int $i = 0; $i < $times; $i++)
			X.chill($pause);
		assertEquals($start+($pause*$times), X.time(), $error);
	}
	
	public void testA() {
		doLoop(50,10,10);
	}
	
	public void testA2() {
		doLoop(50,10,3);
	}
	
	public void testA3() {
		doLoop(50,10,1);
	}
	
	public void testB() {
		doLoop(20,20,2);
	}
	
	public void testB10() {
		for (int $i = 0; $i < 10; $i++)
			doLoop(20,20,2);
	}
	
	public void testC() {
		doLoop(500,10,5);
	}
}
