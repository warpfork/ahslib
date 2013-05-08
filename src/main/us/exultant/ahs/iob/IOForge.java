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

package us.exultant.ahs.iob;

import us.exultant.ahs.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

/**
 * <p>
 * IOForge largely just contains ghastly huge batches of convenience methods making
 * facades around java.io &mdash; getting rid of the dead-simple boilerplate code that
 * otherwise often shows up in numerous places in any sizable project. In particular,
 * these methods are oriented around the following axioms:
 * <ul>
 * <li>Fuctions don't do anything piecemeal, they just give you the answer you wanted and
 * don't make you worry about it.
 * <li>Zero threading, nonblocking, or anything interesting &mdash; just dead-simple
 * stuff.
 * <li>Includes all the little edge cases that often throw a novice coder, such as closing
 * streams even if their creation threw exceptions (which if neglected can deplete the
 * range of file descriptors the OS is willing to allocate), etc.
 * <li>Charset always defaults to UTF-8 (instead of the "platform default").
 * <li>Operations are always sensibly buffered.
 * </ul>
 * </p>
 *
 * <p>
 * If you're looking for higher-powered APIs that do more powerful network stuff,
 * high-performance nonblocking operations, or better lend themselves to protocol
 * composition, you'll want to look to the {@link us.exultant.ahs.io} package. These
 * functions are mostly just intended to be great for rapid application development,
 * really small volumes of data where setting up a bigger infrastructure isn't worth the
 * time, or for use by Java novices who don't want to get into the more powerful APIs yet.
 * </p>
 *
 * <p>
 * note: the difference between methods that read a "resource" and a read a "file" is
 * small. The former uses a classloader's concept of resolving paths; the latter looks
 * only in the exact path you give.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class IOForge {
	/** This OutputStream is simply an effective /dev/null in portable java. */
	public static final OutputStream silentOutputStream = new OutputStreamDiscard();
	/** This PrintStream is simply an effective /dev/null in portable java. */
	public static final PrintStream silentPrintStream = new PrintStream(new OutputStreamDiscard());

	private static final int CHUNK_SIZE = 8192;


	/** Read an entire file into an array as raw bytes. */
	public static byte[] readFileRaw(File $file) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($file));
	}
	/** Read an entire file into an array as raw bytes. */
	public static byte[] readFileRaw(String $filename) throws FileNotFoundException, IOException {
		return readRaw(new FileInputStream($filename));
	}
	/** Read an entire file into an array as raw bytes. */
	public static byte[] readResourceRaw(String $resource) throws FileNotFoundException, IOException {
		return readRaw(getResourceAsStream($resource));
	}
	/** Read an entire file into a UTF-8 string. */
	public static String readFileAsString(String $filename) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($filename));
	}
	/** Read an entire file into a string. */
	public static String readFileAsString(String $filename, Charset $cs) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($filename), $cs);
	}
	/** Read an entire file into a UTF-8 string. */
	public static String readFileAsString(File $file) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($file));
	}
	/** Read an entire file into a string. */
	public static String readFileAsString(File $file, Charset $cs) throws FileNotFoundException, IOException {
		return readString(new FileInputStream($file), $cs);
	}
	/** Read an entire file into a UTF-8 string. */
	public static String readResourceAsString(String $resource) throws FileNotFoundException, IOException {
		return readString(getResourceAsStream($resource));
	}
	/** Read an entire file into a string. */
	public static String readResourceAsString(String $resource, Charset $cs) throws FileNotFoundException, IOException {
		return readString(getResourceAsStream($resource), $cs);
	}
	/** Read an entire file into an array of UTF-8 strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readFileAsStringLines(String $filename) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($filename));
	}
	/** Read an entire file into an array of strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readFileAsStringLines(String $filename, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($filename), $cs);
	}
	/** Read an entire file into an array of UTF-8 strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readFileAsStringLines(File $file) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($file));
	}
	/** Read an entire file into an array of strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readFileAsStringLines(File $file, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(new FileInputStream($file), $cs);
	}
	/** Read an entire file into an array of UTF-8 strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readResourceAsStringLines(String $resource) throws FileNotFoundException, IOException {
		return readStringLines(getResourceAsStream($resource));
	}
	/** Read an entire file into an array of strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.) */
	public static String[] readResourceAsStringLines(String $resource, Charset $cs) throws FileNotFoundException, IOException {
		return readStringLines(getResourceAsStream($resource), $cs);
	}



	/** Read an input stream into an array as raw bytes.  Closes the input stream when done, even if IOException. */
	public static byte[] readRaw(InputStream $ins) throws IOException {
		try {
			byte[] $buf = new byte[CHUNK_SIZE];
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

	/** Read an entire input stream into a UTF-8 string. Closes the input stream when done, even if IOException. */
	public static String readString(InputStream $ins) throws IOException {
		return readString($ins, Strings.UTF_8);
	}

	/** Read an entire input stream into a string.  Closes the input stream when done, even if IOException. */
	public static String readString(InputStream $ins, Charset $cs) throws IOException {
		try {
			char[] $buf = new char[CHUNK_SIZE];
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

	/** Read an entire file into an array of UTF-8 strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.)  Closes the input stream when done, even if IOException. */
	public static String[] readStringLines(InputStream $ins) throws IOException {
		return readStringLines($ins, Strings.UTF_8);
	}

	/** Read an entire file into an array of strings, one array entry for each line in the file.  (A 'line' is defined exactly as per {@link BufferedReader#readLine()}.)  Closes the input stream when done, even if IOException. */
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

	/** Returns an InputStream for the named resource (this is shorthand for accessing {@link ClassLoader#getResource(String)}).  If possible, the system classloader is used to resolve the resource; otherwise the classloader for the IOForge class is used. */
	public static InputStream getResourceAsStream(String $resource) throws FileNotFoundException {
		InputStream $ins = CL.getResourceAsStream($resource);
		if ($ins == null) throw new FileNotFoundException();
		return $ins;
	}

	private static final ClassLoader CL;
	static {
		ClassLoader $cl;
		try {
			$cl = ClassLoader.getSystemClassLoader();
		} catch (java.security.AccessControlException $e) {
			$cl = null;
		}
		CL = ($cl == null) ? IOForge.class.getClassLoader() : $cl;
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

	/** Write an array of raw bytes to a file. */
	public static void saveFile(byte[] $bah, File $dest) throws IOException {
		writeFile($bah, $dest, false);
	}
	/** Write a string to a file in UTF-8 encoding. */
	public static void saveFile(String $bah, File $dest) throws IOException {
		writeFile($bah, $dest, false);
	}
	/** Write a string to a file. */
	public static void saveFile(String $bah, Charset $cs, File $dest) throws IOException {
		writeFile($bah, $cs, $dest, false);
	}
	/** Append an array of raw bytes to a file. */
	public static void appendFile(byte[] $bah, File $dest) throws IOException {
		writeFile($bah, $dest, true);
	}
	/** Append a string to a file in UTF-8 encoding. */
	public static void appendFile(String $bah, File $dest) throws IOException {
		writeFile($bah, $dest, true);
	}
	/** Append a string to a file. */
	public static void appendFile(String $bah, Charset $cs, File $dest) throws IOException {
		writeFile($bah, $cs, $dest, true);
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
		writeFile($bah, Strings.UTF_8, $dest, $append);

	}
	private static void writeFile(String $bah, Charset $cs, File $dest, boolean $append) throws IOException {
		OutputStreamWriter $os = null;
		try {
			$os = new OutputStreamWriter(new FileOutputStream($dest, $append), $cs);
			$os.write($bah);
		} finally {
			if ($os != null) $os.close();
		}
	}

	/**
	 * Creates an {@link InputStream} that when read will return the bytes of the
	 * given string in utf-8 encoding.
	 *
	 * @param $str
	 * @return an input stream that will read off the bytes of the string in utf-8
	 *         encoding
	 */
	public static InputStream convertStringToInputStream(String $str) {
		return convertStringToInputStream($str, Strings.UTF_8);
	}

	public static InputStream convertStringToInputStream(String $str, Charset $cs) {
		return new ByteArrayInputStream($str.getBytes($cs));
	}

	/**
	 * <p>
	 * Shifts data from an {@link InputStream} to an {@link OutputStream}. The
	 * operation is performed in buffered chunks and continues until the input stream
	 * is closed or the output stream rejects writes. Both streams are closed after
	 * completion.
	 * </p>
	 *
	 * <p>
	 * Useful for in situations like moving the data from a URL connection to a file
	 * output, or redirection stdout of a forked process to our own stdout.
	 * </p>
	 *
	 * @param $src
	 * @param $sink
	 * @throws IOException
	 *                 if problems reading bytes from the source, writing bytes to the
	 *                 sink, or closing either stream.
	 */
	public static void shift(InputStream $src, OutputStream $sink) throws IOException {
		try {
			byte[] $buf = new byte[CHUNK_SIZE];
			int $k;
			int $p = 0;
			while (($k = $src.read($buf)) != -1) {
				$sink.write($buf,0,$k);
				$p += $k;
			}
		} finally {
			// i'm unsure how i feel about having these here.  they almost always have to be repeated by the caller anyway (i.e. you still need a giant try/finally to close the input stream if you fail to open the output stream after you've already opened the input.
			try {
				$src.close();
			} finally {
				$sink.close();
			}
		}
	}

	public static void shift(String $src, OutputStream $sink) throws IOException {
		shift($src, Strings.UTF_8, $sink);
	}

	public static void shift(String $src, Charset $cs, OutputStream $sink) throws IOException {
		$sink.write($src.getBytes($cs));
	}

	/**
	 * <p>
	 * Simple method to save information from http to the local filesystem. Binary
	 * safe.
	 * </p>
	 *
	 * <p>
	 * There are almost always smarter ways to go about this, but if you're willing to
	 * pay the price throwing a whole thread at doing nothing but wait for this for
	 * the sake of simplicity and rapid application development, then this will do
	 * just fine.
	 * <p>
	 *
	 * @param $request
	 *                URL pointing to the http path to save.
	 * @param $dest
	 *                Local file to store to.
	 * @throws IOException
	 */
	public static void saveUrlToFile(URL $request, File $dest) throws IOException {
		InputStream $in = null;
		OutputStream $out = null;
		try {
			URLConnection $conn = $request.openConnection();
			$conn.connect();
			checkErrorCode($conn);
			$in = new BufferedInputStream($conn.getInputStream());
			$out = new BufferedOutputStream(new FileOutputStream($dest));
			shift($in, $out);
		} finally {
			try {
				if ($in != null) $in.close();
			} finally {
				if ($out != null) $out.close();
			}
		}
	}

	public static String readUrlAsString(URL $request) throws IOException {
		InputStream $in = null;
		try {
			URLConnection $conn = $request.openConnection();
			$in = $conn.getInputStream();
			checkErrorCode($conn);
			return readString($in);
		} finally {
			if ($in != null) $in.close();
		}
	}

	public static byte[] readUrlRaw(URL $request) throws IOException {
		InputStream $in = null;
		try {
			URLConnection $conn = $request.openConnection();
			$in = $conn.getInputStream();
			checkErrorCode($conn);
			return readRaw($in);
		} finally {
			if ($in != null) $in.close();
		}
	}

	private static void checkErrorCode(URLConnection $conn) throws IOException {
		if ($conn instanceof HttpURLConnection) {
			int code = ((HttpURLConnection) $conn).getResponseCode();
			if (code / 100 != 2) throw new IOException("Could not connect to '" + $conn.getURL() + "' via HTTP; gave error code " + code + ".");
		}
	}
}
