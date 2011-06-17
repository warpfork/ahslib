package us.exultant.ahs.codec.eon;

import us.exultant.ahs.core.*;
import java.util.*;

// it's not impossible that a large number of these fields will eventually want to throw Unencodable exceptions
//   i'm worried about key portability -- for example, i suspect xml will be less permissive than json.
//      i really don't understand why ANYONE uses xml EVER.

public interface EonObject {
	public void    assertKlass(Object $x)   throws TranslationException;
	public void    assertKlass(Class<?> $x) throws TranslationException;
	public void    assertKlass(String $x)   throws TranslationException;
	public void    putKlass   (Object $x);
	public void    putKlass   (Class<?> $x);
	public void    putKlass   (String $x);
	public String  getKlass();
	
	public void    putName(String $x);
	public String  getName() throws TranslationException;
	
	public void    putData(EonObject $x);
	public void    putData(EonArray $x);
	public void    putData(String $x);
	public void    putData(byte[] $x);
	public EonObject  getData      () throws TranslationException;
	public EonArray   getArrayData () throws TranslationException;
	public String  getStringData() throws TranslationException;
	public byte[]  getByteData  () throws TranslationException;
	
	public boolean has(String $key);
	public int     size();
	// "opt" methods either return defaults or null in case of errors; their "get" breathren throw exceptions if the requested value is missing or untranslatable.
	public void    put(String $key, byte[] $val);
	public byte[]  getBytes(String $key) throws TranslationException;
	public byte[]  optBytes(String $key);
	public byte[]  optBytes(String $key, byte[] $default);
	public void    put(String $key, boolean $val);
	public boolean getBoolean(String $key) throws TranslationException;
	public boolean optBoolean(String $key, boolean $default);
	public void    put(String $key, double $val) throws UnencodableException;
	public double  getDouble(String $key)        throws TranslationException;
	public double  optDouble(String $key, double $default);
	public void    put(String $key, int $val) throws UnencodableException;
	public int     getInt(String $key)        throws TranslationException;
	public int     optInt(String $key, int $default);
	public void    put(String $key, long $val) throws UnencodableException;
	public long    getLong(String $key)        throws TranslationException;
	public long    optLong(String $key, long $default);
	public void    put(String $key, String $val);
	public String  getString(String $key) throws TranslationException;
	public String  optString(String $key);		// null
	public String  optString(String $key, String $default);
	public void    put(String $key, EonObject $val);
	public EonObject   getObj(String $key) throws TranslationException;
	public EonObject   optObj(String $key);		// null
	public void    put(String $key, EonArray $val);
	public EonArray   getArr(String $key) throws TranslationException;
	public EonArray   optArr(String $key);		// null
	
	public byte[] serialize() throws TranslationException;
	public void deserialize(byte[] $bats) throws TranslationException;
	
	public Set<Map.Entry<String,Object>> entrySet();
	
	/**
	 * This adapter provides wrappers around most of the basic methods that provide
	 * "opt"-like functionality, but still leaves the basic data-oriented methods to
	 * be implemented by concrete subclasses.
	 * 
	 * @author hash
	 */
	public abstract static class Adapter {
		//TODO:AHS: omg really.  this would make so much json stuff SO much more clear, to say nothing of consistent across other platforms to come.
	}
}
