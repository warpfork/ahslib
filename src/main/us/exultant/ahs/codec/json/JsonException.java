package us.exultant.ahs.codec.json;

import us.exultant.ahs.core.*;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * 
 * @author JSON.org
 * @version 2008-09-18
 */
public class JsonException extends TranslationException {
	public JsonException() {
		super();
	}

	public JsonException(String $message, Throwable $cause) {
		super($message, $cause);
	}

	public JsonException(String $message) {
		super($message);
	}

	public JsonException(Throwable $cause) {
		super($cause);
	}
}
