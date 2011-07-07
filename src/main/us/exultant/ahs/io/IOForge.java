package us.exultant.ahs.io;

import us.exultant.ahs.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class IOForge {
	//
	//  Ghastly huge batches of convenience methods making facades around java.io.
	//    For local filesystem operations.
	//    Zero threading, nonblocking, or anything interesting.
	//    Includes all the little edge cases that often throw a novice coder, such as closing streams even if their creation threw exceptions (which if neglected can deplete the range of file descriptors the OS is willing to allocate), etc.
	//    Charset always defaults to UTF-8 (instead of the "platform default").
	//
	public static byte[] readFileRaw(File $file) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($file));
	}
	public static byte[] readFileRaw(String $filename) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($filename));
	}
	public static byte[] readResourceRaw(String $resource) throws FileNotFoundException, IOException {
		return readRaw(getResourceAsStream($resource));
	}
	public static String readFileAsString(String $filename) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($filename));
	}
	public static String readFileAsString(String $filename, Charset $cs) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($filename), $cs);
	}
	public static String readFileAsString(File $file) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($file));
	}
	public static String readFileAsString(File $file, Charset $cs) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($file), $cs);
	}
	public static String readResourceAsString(String $resource) throws FileNotFoundException, IOException {
		return readString(getResourceAsStream($resource));
	}
	public static String readResourceAsString(String $resource, Charset $cs) throws FileNotFoundException, IOException {
		return readString(getResourceAsStream($resource), $cs);
	}
	public static String[] readFileAsStringLines(String $filename) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($filename));
	}
	public static String[] readFileAsStringLines(String $filename, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($filename), $cs);
	}
	public static String[] readFileAsStringLines(File $file) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($file));
	}
	public static String[] readFileAsStringLines(File $file, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($file), $cs);
	}
	public static String[] readResourceAsStringLines(String $resource) throws FileNotFoundException, IOException {
		return readStringLines(getResourceAsStream($resource));
	}
	public static String[] readResourceAsStringLines(String $resource, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(getResourceAsStream($resource), $cs);
	}
	
	/** Closes the input stream when done, even if IOException. */
	public static byte[] readRaw(InputStream $ins) throws IOException {
		try {
			byte[] $buf = new byte[512];
			int $k;
			ByteArrayOutputStream $bs = new ByteArrayOutputStream();
			while (($k = $ins.read($buf)) != -1) {
				$bs.write($buf, 0, $k);
			}
			return $bs.toByteArray();
		} finally {
			$ins.close();
		}
	}
	
	/** Closes the input stream when done, even if IOException. */
	public static String readString(InputStream $ins) throws IOException {
		return readString($ins, Strings.UTF_8);
	}
	
	/** Closes the input stream when done, even if IOException. */
	public static String readString(InputStream $ins, Charset $cs) throws IOException {
		try {
			char[] $buf = new char[512];
			int $k;
			StringBuffer $sb = new StringBuffer();
			InputStreamReader $isr = new InputStreamReader($ins, $cs);
			while (($k = $isr.read($buf)) != -1) {
				$sb.append($buf, 0, $k);
			}
			return $sb.toString();
		} finally {
			$ins.close();
		}
	}

	
	/** Closes the input stream when done, even if IOException. */
	public static String[] readStringLines(InputStream $ins) throws IOException {
		return readStringLines($ins, Strings.UTF_8);
	}
	
	/** Closes the input stream when done, even if IOException. */
	public static String[] readStringLines(InputStream $ins, Charset $cs) throws IOException {
		try {
			List<String> $lines = new ArrayList<String>();
			BufferedReader $br = new BufferedReader(new InputStreamReader($ins, $cs));
			for (String $next = $br.readLine(); $next != null; $next = $br.readLine())
				$lines.add($next);
			return $lines.toArray(Primitives.EMPTY_STRING);
		} finally {
			$ins.close();
		}
	}
	
	public static InputStream getResourceAsStream(String $resource) throws FileNotFoundException {
		InputStream $ins;
		if (CL != null)
			$ins = CL.getResourceAsStream($resource);
		else
			$ins = CL2.getResourceAsStream($resource);
		if ($ins == null) throw new FileNotFoundException();
		return $ins;
	}
	
	private static final ClassLoader CL;
	private static final ClassLoader CL2;
	static {
		ClassLoader $cl;
		try {
			$cl = ClassLoader.getSystemClassLoader();
		} catch (java.security.AccessControlException $e) {
			//$cl = IOForge.class.getClassLoader();
			//ClassLoader $next = $cl.getParent();
			//while ($next != null) {
			//	$cl = $next;
			//	$next = $cl.getParent();
			//}
			$cl = null;	// we detect and deal with this elsewhere
		}
		CL = $cl;
		CL2 = IOForge.class.getClassLoader();
	};
	
	/**
	 * <p>
	 * Convenience method for times when you don't care about code quality and just
	 * want to make a damn PrintStream in a chained constructor call or when declaring
	 * a static final variable without making seven lines of static initializer just
	 * to catch an exception that you're going to throw up your hands in despair and
	 * crash on anyway.
	 * </p>
	 * 
	 * <p>
	 * In other words, this throws an Error if it can't give you the stream.
	 * </p>
	 * 
	 * @throws Error if FileNotFoundException.
	 */
	public static PrintStream makePrintStreamNoGuff(File $file) {
		try {
			return new PrintStream($file);
		} catch (FileNotFoundException $e) {
			throw new Error($e);
		}
	}
	
	public static void saveFile(byte[] $bah, File $dest) throws IOException {
		writeFile($bah, $dest, false);
	}
	public static void saveFile(String $bah, File $dest) throws IOException {
		writeFile($bah, $dest, false);
	}
	public static void appendFile(byte[] $bah, File $dest) throws IOException {
		writeFile($bah, $dest, true);
	}
	public static void appendFile(String $bah, File $dest) throws IOException {
		writeFile($bah, $dest, true);
	}
	private static void writeFile(byte[] $bah, File $dest, boolean $append) throws IOException {
		OutputStream $os = null;
		try {
			$os = new BufferedOutputStream(new FileOutputStream($dest, $append));
			$os.write($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}
	private static void writeFile(String $bah, File $dest, boolean $append) throws IOException {
		OutputStreamWriter $os = null;
		try {
			$os = new OutputStreamWriter(new FileOutputStream($dest, $append), Strings.UTF_8);
			$os.write($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}
	
	/**
	 * <p>
	 * Simple method to save information from http to the local filesystem.
	 * </p>
	 * 
	 * <p>
	 * There are almost always smarter ways to go about this, but if you're willing to
	 * pay the price throwing a whole thread at doing nothing but wait for this for
	 * the sake of simplicity, then this will do just fine.
	 * <p>
	 * 
	 * @param $request
	 *                URL pointing to the http path to save.
	 * @param $dest
	 *                Local file to store to.
	 * @throws IOException
	 */
	public static void saveFile(URL $request, File $dest) throws IOException {
		InputStream $in = null;
		OutputStream $out = null;
		try {
			URLConnection $conn = $request.openConnection();
			$conn.connect();
			$in = $conn.getInputStream();
			if ($conn instanceof HttpURLConnection) {
				int code = ((HttpURLConnection) $conn).getResponseCode();
				if (code / 100 != 2) throw new IOException("Could not connect to '" + $request + "' via HTTP; gave error code " + code + ".");
			}
			
			$in = new BufferedInputStream($in);
			$out = new BufferedOutputStream(new FileOutputStream($dest));
			byte[] $buf = new byte[512];
			int $k;
			int $p = 0;
			while (($k = $in.read($buf)) != -1) {
				$out.write($buf,0,$k);
				$p += $k;
			}
		} finally {
			try {
				if ($in != null) $in.close();
			} finally {
				if ($out != null) $out.close();
			}
		}
	}
}
