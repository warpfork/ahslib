package ahs.io;

import ahs.util.*;
import java.io.*;
import java.nio.charset.*;

@Deprecated()
public class ReadHeadStreamToString extends ReadHeadAdapterSimple<String> {
	public ReadHeadStreamToString(InputStream $base, Charset $cs) {
		super();
		this.$base = new BufferedReader(new InputStreamReader($base, $cs));
	}
	public ReadHeadStreamToString(InputStream $base) {
		this($base, Strings.UTF_8);
	}
	
	private final BufferedReader			$base;
	
	protected String getChunk() throws IOException {
		String $v = $base.readLine();
		if ($v == null) baseEof();
		return $v;
	}
	
	public void close() throws IOException {
		$base.close();
	}
	
	

	/**
	 * <p>
	 * This method is similar to <code>readAll()</code> in that it will not return
	 * until the stream is closed and no more data will be available after it returns.
	 * However, it merges all of the String objects back into a single contiguous
	 * String before returning it, and it does not wait until the stream is closed to
	 * begin consuming reads.
	 * </p>
	 * 
	 * <p>
	 * All line breaks become the UNIX standard line break (i.e., the '\n' character),
	 * and the returned string will include a trailing line break, regardless of
	 * whether the original stream ended in one or become closed because of exception
	 * (even mid-line).
	 * </p>
	 */
	public String readCompletely() {
		StringBuilder $sb = new StringBuilder();
		while (hasNext() || !isClosed())
			$sb.append(read()).append('\n');
		return $sb.toString();
	}
}
