/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.util;

import us.exultant.ahs.test.*;
import java.util.*;

public class StringsTest extends TestCase {
	public static void main(String... $args) {
		new StringsTest().run();
	}

	public List<Unit> getUnits() {
		return Arr.asList(
				new TestParting(),
				new TestFileNameManipulation(),
				new TestPadding(),
				new TestToHex(),
				new TestFromHex()
		);
	}



	private class TestParting extends TestCase.Unit {
		public void call() {
			//getPartAfter
			assertEquals("qwerzjkl;",	Strings.getPartAfter("asdfzqwerzjkl;","z"));
			assertEquals("sdfzqwerzjkl;",	Strings.getPartAfter("asdfzqwerzjkl;","a"));
			assertEquals("kl;",		Strings.getPartAfter("asdfzqwerzjkl;","zj"));
			assertEquals("",		Strings.getPartAfter("asdfzqwerzjkl;","kl;"));
			assertEquals("asdfzqwerzjkl;",	Strings.getPartAfter("asdfzqwerzjkl;","t"));

			//getPartBefore
			assertEquals("asdf",		Strings.getPartBefore("asdfzqwerzjkl;","z"));
			assertEquals("asdfzqwerzjkl",	Strings.getPartBefore("asdfzqwerzjkl;",";"));
			assertEquals("asdfzqwer",	Strings.getPartBefore("asdfzqwerzjkl;","zj"));
			assertEquals("",		Strings.getPartBefore("asdfzqwerzjkl;","asdf"));

			//getPartBetween
			assertEquals("sdfzqwerzjkl",	Strings.getPartBetween("asdfzqwerzjkl;","a",";"));
			assertEquals("k",		Strings.getPartBetween("asdfzqwerzjkl;","zj","l;"));
			assertEquals("",		Strings.getPartBetween("asdfzqwerzjkl;","asdf","zqwe"));
			assertEquals("qwer",		Strings.getPartBetween("asdfzqwerzjkl;","z","z"));
			assertEquals("qwzr",		Strings.getPartBetween("asdfzqwzrzjkl;","z","z"));

			//getPartAfterLast
			assertEquals("jkl;",		Strings.getPartAfterLast("asdfzqwerzjkl;","z"));
			assertEquals("sdfzqwerzjkl;",	Strings.getPartAfterLast("asdfzqwerzjkl;","a"));
			assertEquals("kl;",		Strings.getPartAfterLast("asdfzqwerzjkl;","zj"));
			assertEquals("",		Strings.getPartAfterLast("asdfzqwerzjkl;","kl;"));

			//getPartBeforeLast
			assertEquals("asdfzqwer",	Strings.getPartBeforeLast("asdfzqwerzjkl;","z"));
			assertEquals("asdfzqwerzjkl",	Strings.getPartBeforeLast("asdfzqwerzjkl;",";"));
			assertEquals("asdfzqwer",	Strings.getPartBeforeLast("asdfzqwerzjkl;","zj"));
			assertEquals("",		Strings.getPartBeforeLast("asdfzqwerzjkl;","asdf"));

			//splitOnNext
			String[] $sa = Strings.splitOnNext("asdfzqwerzjkl;", "z");
			assertEquals("asdf", $sa[0]);
			assertEquals("qwerzjkl;", $sa[1]);

			//splitOnLast
			$sa = Strings.splitOnLast("asdfzqwerzjkl;", "z");
			assertEquals("asdfzqwer", $sa[0]);
			assertEquals("jkl;", $sa[1]);
		}
	}

	private class TestFileNameManipulation extends TestCase.Unit {
		public void call() {
			String $path1 = "/ahs/d/data/screwAll.txt";
			String $path2 = "/ahs/d/data/";
			String $path3 = "/ahs/d/data/screwAll.txt.bak";
			String $path4 = "/";
			String $path5 = "/ahs/d/data/noext";

			assertEquals("/ahs/d/data/",	Strings.dirname($path1));
			assertEquals("/ahs/d/data/",	Strings.dirname($path2));
			assertEquals("/ahs/d/data/",	Strings.dirname($path3));
			assertEquals("/",		Strings.dirname($path4));
			assertEquals("/ahs/d/data/",	Strings.dirname($path5));

			assertEquals("screwAll.txt",	Strings.fullname($path1));
			assertEquals("",		Strings.fullname($path2));
			assertEquals("screwAll.txt.bak",Strings.fullname($path3));
			assertEquals("",		Strings.fullname($path4));
			assertEquals("noext",		Strings.fullname($path5));

			assertEquals("screwAll",	Strings.basename($path1));
			assertEquals("",		Strings.basename($path2));
			assertEquals("screwAll",	Strings.basename($path3));
			assertEquals("",		Strings.basename($path4));
			assertEquals("noext",		Strings.basename($path5));

			assertEquals("txt",	Strings.extension($path1));
			assertEquals("",	Strings.extension($path2));
			assertEquals("bak",	Strings.extension($path3));
			assertEquals("",	Strings.extension($path4));
			assertEquals("",	Strings.extension($path5));
		}
	}

	private class TestPadding extends TestCase.Unit {
		public void call() {
			assertEquals("   hi!",		Strings.padLeft("hi!", 3));
			assertEquals("   hi! ",		Strings.padLeft("hi! ", 3));
			assertEquals("   hi!",		Strings.padLeftToWidth("hi!", 6));
			assertEquals("  hi! ",		Strings.padLeftToWidth("hi! ", 6));
			assertEquals("hi!   ",		Strings.padRight("hi!", 3));
			assertEquals("hi!    ",		Strings.padRight("hi! ", 3));
			assertEquals("hi!   ",		Strings.padRightToWidth("hi!", 6));
			assertEquals("hi!   ",		Strings.padRightToWidth("hi! ", 6));
		}
	}

	private class TestToHex extends TestCase.Unit {
		public void call() {
			assertEquals("FF",Strings.toHex(new byte[] {-1}));
			assertEquals("00",Strings.toHex(new byte[] {0}));
			assertEquals("0C",Strings.toHex(new byte[] {12}));
			assertEquals("78",Strings.toHex(new byte[] {120}));
			assertEquals("780CFF00",Strings.toHex(new byte[] {120,12,-1,0}));
		}
	}

	private class TestFromHex extends TestCase.Unit {
		public void call() {
			assertEquals(new byte[] {120,12,-1,0},Strings.fromHex(Strings.toHex(new byte[] {120,12,-1,0})));
		}
	}
}
