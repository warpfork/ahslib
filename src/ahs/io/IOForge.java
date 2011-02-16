package ahs.io;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;

import ahs.util.*;
import ahs.util.thread.*;

public class IOForge {
	public static ReadHeadStreamToByteBuffer readFile(String $filename) throws FileNotFoundException {
		return new ReadHeadStreamToByteBuffer(new FileInputStream($filename), 4096);
	}
	public static ReadHeadStreamToByteBuffer readFile(File $file) throws FileNotFoundException {
		return new ReadHeadStreamToByteBuffer(new FileInputStream($file), 4096);
	}
	public static ReadHeadStreamToByteBuffer readResource(String $resource) throws FileNotFoundException {
		return new ReadHeadStreamToByteBuffer(getResourceAsStream($resource), 4096);
	}
	
	public static byte[] readFileRaw(File $file) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($file));
	}
	public static byte[] readFileRaw(String $filename) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($filename));
	}
	public static byte[] readResourceRaw(String $resource) throws FileNotFoundException, IOException {
		return readRaw(getResourceAsStream($resource));
	}
	
	public static ReadHeadStreamToString readFileAsStringStream(File $file) throws FileNotFoundException {
		return new ReadHeadStreamToString(new FileInputStream($file), Strings.UTF_8);
	}
	public static ReadHeadStreamToString readFileAsStringStream(File $file, Charset $cs) throws FileNotFoundException {
		return new ReadHeadStreamToString(new FileInputStream($file), $cs);
	}
	public static ReadHeadStreamToString readFileAsStringStream(String $filename) throws FileNotFoundException {
		return new ReadHeadStreamToString(new FileInputStream($filename), Strings.UTF_8);
	}
	public static ReadHeadStreamToString readFileAsStringStream(String $filename, Charset $cs) throws FileNotFoundException {
		return new ReadHeadStreamToString(new FileInputStream($filename), $cs);
	}
	public static ReadHeadStreamToString readResourceAsStringStream(String $resource) throws FileNotFoundException {
		return new ReadHeadStreamToString(getResourceAsStream($resource), Strings.UTF_8);
	}
	public static ReadHeadStreamToString readResourceAsStringStream(String $resource, Charset $cs) throws FileNotFoundException {
		return new ReadHeadStreamToString(getResourceAsStream($resource), $cs);
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
	
	
	
	
	
	
	
	
	// shit below this isn't direly new.
	
	
	
	
	
	
	
	/**
	 * Saves the given bytes to the destination file.
	 * 
	 * @param $bah
	 *                Bytes to save.
	 * @param $dest
	 *                File to write.
	 * @throws IOException
	 *                 If the destination is a directory or unwritable or we lack
	 *                 permissions, or in case of general IO failures.
	 */
	public static void saveFile(byte[] $bah, File $dest) throws IOException {
		OutputStream $os = null;
		try {
			$os = new BufferedOutputStream(new FileOutputStream($dest, false));
			$os.write($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}
	public static void saveFile(String $bah, File $dest) throws IOException {
		PrintStream $os = null;
		try {
			$os = new PrintStream(new FileOutputStream($dest, false));
			$os.print($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}
	public static void appendFile(byte[] $bah, File $dest) throws IOException {
		OutputStream $os = null;
		try {
			$os = new BufferedOutputStream(new FileOutputStream($dest, true));
			$os.write($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}
	
	
	
	// shit below this is direly old.
	
	
	
	
	
	
	
	public static InputStream forgeInputQ(String $request) {
		try {
			return forgeInput($request);
		} catch (IOException $e) {
			return null;
		}
	}
	
	/**
	 * Opens a simple InputStream to allow reading of arbitrary data from a local
	 * file, or conveniently over the internet via HTTP.
	 * 
	 * @param $request
	 *                URL pointing to local file path or http path.
	 * @return InputStream connected to the requested URL, or null if not available.
	 * @throws IOException
	 *                 in case of HTTP error.
	 */
	public static InputStream forgeInput(String $request) throws IOException {
		InputStream $in;
		File $file = new File($request);
		if ($file.exists()) {
			$in = new FileInputStream($file);
		} else {
			URL $url = new URL($request);
			URLConnection $conn = $url.openConnection();
			$conn.connect();
			$in = $conn.getInputStream();
			if ($conn instanceof HttpURLConnection) {
				int code = ((HttpURLConnection) $conn).getResponseCode();
				if (code / 100 != 2) throw new IOException("Could not connect to '" + $request + "' via HTTP; gave error code " + code + ".");
			}
		}
		return $in;
	}
	
	/**
	 * Simple method to save information from http to the local filesystem.
	 * 
	 * @param $request
	 *                URL pointing to local file path or http path (same as
	 *                forgeInput()).
	 * @param $dest
	 *                Local file to store to.
	 * @throws IOException
	 *                 if the destination is not writable, or in case of HTTP error.
	 */
	public static void saveRequest(String $request, File $dest) throws IOException {
		InputStream $bis = new BufferedInputStream(forgeInput($request));
		OutputStream $os = new BufferedOutputStream(new FileOutputStream($dest));
		byte[] $buf = new byte[512];
		int $k;
		int $p = 0;
		while (($k = $bis.read($buf)) != -1) {
			$os.write($buf,0,$k);
			$p += $k;
		}
		$bis.close();
		$os.close();
	}
}
