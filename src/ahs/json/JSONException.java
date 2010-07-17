package ahs.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * 
 * @author JSON.org
 * @version 2008-09-18
 */
public class JSONException extends ahs.io.TranslationException {
	public JSONException() {
		super();
	}

	public JSONException(String $message, Throwable $cause) {
		super($message, $cause);
	}

	public JSONException(String $message) {
		super($message);
	}

	public JSONException(Throwable $cause) {
		super($cause);
	}
}
