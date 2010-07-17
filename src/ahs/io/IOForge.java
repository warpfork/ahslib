package ahs.io;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;

import ahs.util.Strings;
import ahs.util.thread.*;

public class IOForge {
	public static StreamReadHead readFile(String $filename) throws FileNotFoundException {
		return new StreamReadHead(new FileInputStream($filename), 4096);
	}
	public static StreamReadHead readFile(File $file) throws FileNotFoundException {
		return new StreamReadHead(new FileInputStream($file), 4096);
	}
	public static StreamReadHead readResource(String $resource) throws FileNotFoundException {
		return new StreamReadHead(getResourceAsStream($resource), 4096);
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
	
	public static StringStreamReadHead readFileAsStringStream(File $file) throws FileNotFoundException {
		return new StringStreamReadHead(new FileInputStream($file), Strings.UTF_8);
	}
	public static StringStreamReadHead readFileAsStringStream(File $file, Charset $cs) throws FileNotFoundException {
		return new StringStreamReadHead(new FileInputStream($file), $cs);
	}
	public static StringStreamReadHead readFileAsStringStream(String $filename) throws FileNotFoundException {
		return new StringStreamReadHead(new FileInputStream($filename), Strings.UTF_8);
	}
	public static StringStreamReadHead readFileAsStringStream(String $filename, Charset $cs) throws FileNotFoundException {
		return new StringStreamReadHead(new FileInputStream($filename), $cs);
	}
	public static StringStreamReadHead readResourceAsStringStream(String $resource) throws FileNotFoundException {
		return new StringStreamReadHead(getResourceAsStream($resource), Strings.UTF_8);
	}
	public static StringStreamReadHead readResourceAsStringStream(String $resource, Charset $cs) throws FileNotFoundException {
		return new StringStreamReadHead(getResourceAsStream($resource), $cs);
	}
	
	public static String readFileAsString(String $filename) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($filename));
	}
	public static String readFileAsString(File $file) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($file));
	}
	public static String readResourceAsString(String $resource) throws FileNotFoundException, IOException {
		return readString(getResourceAsStream($resource));
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
		try {
			char[] $buf = new char[512];
			int $k;
			StringBuffer $sb = new StringBuffer();
			InputStreamReader $isr = new InputStreamReader($ins, Strings.UTF_8);
			while (($k = $isr.read($buf)) != -1) {
				$sb.append($buf, 0, $k);
			}
			return $sb.toString();
		} finally {
			$ins.close();
		}
	}
	
	public static InputStream getResourceAsStream(String $resource) throws FileNotFoundException {
		InputStream $ins = CL.getResourceAsStream($resource);
		if ($ins == null) throw new FileNotFoundException();
		return $ins;
	}
	
	private static final ClassLoader CL = ClassLoader.getSystemClassLoader();
	//private static final ClassLoader CL = IOForge.class.getClassLoader();
	
	
	
	
	
	
	
	
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
	public static void saveFile(String $request, File $dest) throws IOException {
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
