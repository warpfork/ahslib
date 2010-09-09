package ahs.io;

import ahs.io.codec.eon.*;
import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.util.*;

public class ReadHeadTranslator<$FROM, $TO> implements ReadHead<$TO> {
	public ReadHeadTranslator(ReadHead<$FROM> $bottom, Translator<$FROM,$TO> $middle) {
		this.$bottom = $bottom;
		this.$middle = $middle;
	}
	
	private ReadHead<$FROM>			$bottom;
	private Translator<$FROM,$TO>		$middle;
	private ExceptionHandler<IOException>	$eh;
	
	public Pump getPump() {
		return null;
	}
	
	public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
		// shit.  read the contract of this function again.  exceptions are supposed to be reported from the PUMP THREAD.
		// i HAVE been doing it wrong.  the buffer needs to be at the top layer and all of the readhead implementations i've made so far need to be made more piecewise (and probably just made by cute factory methods in some handy class).
		
		this.$eh = $eh;
	}
	
	public void setListener(Listener<ReadHead<$TO>> $el) {
		// nah
	}
	
	public $TO read() {
		while (true) {
			try {
				return $middle.translate($bottom.read());
			} catch (TranslationException $e) {
				ExceptionHandler<IOException> $dated_eh = $eh;
				if ($dated_eh != null) $dated_eh.hear($e);
			}
		}
	}
	
	public $TO readNow() {
		try {
			return $middle.translate($bottom.readNow());
		} catch (TranslationException $e) {
			ExceptionHandler<IOException> $dated_eh = $eh;
			if ($dated_eh != null) $dated_eh.hear($e);
			return null;
		}
	}
	
	public boolean hasNext() {
		return $bottom.hasNext();
	}
	
	public List<$TO> readAll() {
		List<$FROM> $t = $bottom.readAll();
		List<$TO> $v = new ArrayList<$TO>($t.size());
		for ($FROM $f : $t) {
			try {
				$v.add($middle.translate($f));
			} catch (TranslationException $e) {
				ExceptionHandler<IOException> $dated_eh = $eh;
				if ($dated_eh != null) $dated_eh.hear($e);
			}
		}
		return $v;
	}
	
	public List<$TO> readAllNow() {
		List<$FROM> $t = $bottom.readAllNow();
		List<$TO> $v = new ArrayList<$TO>($t.size());
		for ($FROM $f : $t) {
			try {
				$v.add($middle.translate($f));
			} catch (TranslationException $e) {
				ExceptionHandler<IOException> $dated_eh = $eh;
				if ($dated_eh != null) $dated_eh.hear($e);
			}
		}
		return $v;
	}
	
	public boolean isClosed() {
		return $bottom.isClosed();
	}
	
	public void close() throws IOException {
		$bottom.close();
	}
}
