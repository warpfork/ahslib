package us.exultant.ahs.test;

import us.exultant.ahs.util.Strings;

public class StringsTest extends JUnitTestCase {
	public void testParting() {
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
	
	public void testFileNameManipulation() {
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
	
	public void testPadding() {
		assertEquals("   hi!",		Strings.padLeft("hi!", 3));
		assertEquals("   hi! ",		Strings.padLeft("hi! ", 3));
		assertEquals("   hi!",		Strings.padLeftToWidth("hi!", 6));
		assertEquals("  hi! ",		Strings.padLeftToWidth("hi! ", 6));
		assertEquals("hi!   ",		Strings.padRight("hi!", 3));
		assertEquals("hi!    ",		Strings.padRight("hi! ", 3));
		assertEquals("hi!   ",		Strings.padRightToWidth("hi!", 6));
		assertEquals("hi!   ",		Strings.padRightToWidth("hi! ", 6));
	}
	
	public void testToHex() {
		assertEquals("FF",Strings.toHex(new byte[] {-1}));
		assertEquals("00",Strings.toHex(new byte[] {0}));
		assertEquals("0C",Strings.toHex(new byte[] {12}));
		assertEquals("78",Strings.toHex(new byte[] {120}));
		assertEquals("780CFF00",Strings.toHex(new byte[] {120,12,-1,0}));
	}
	
	public void testFromHex() {
		assertEquals(new byte[] {120,12,-1,0},Strings.fromHex(Strings.toHex(new byte[] {120,12,-1,0})));
	}
}
