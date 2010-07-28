package ahs.io.codec.eon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.json.*;
import ahs.util.*;

// it's not impossible that a large number of these fields will eventually want to throw Unencodable exceptions
//   i'm worried about key portability -- for example, i suspect xml will be less permissive than json.
//       i really don't understand why ANYONE uses xml EVER.

public interface EonObject<$TMAP extends EonObject<$TMAP,$TARR>, $TARR extends EonArray<$TMAP,$TARR>> {
	public void    assertKlass(Object $x)   throws TranslationException;
	public void    assertKlass(Class<?> $x) throws TranslationException;
	public void    assertKlass(String $x)   throws TranslationException;
	public void    putKlass   (Object $x);
	public void    putKlass   (Class<?> $x);
	public void    putKlass   (String $x);
	
	public void    putName(String $x);
	public String  getName() throws TranslationException;
	
	public void    putData($TMAP $x);
	public void    putData($TARR $x);
	public void    putData(String $x);
	public void    putData(byte[] $x);
	public $TMAP   getData      () throws TranslationException;
	public $TARR   getArrayData () throws TranslationException;
	public String  getStringData() throws TranslationException;
	public byte[]  getByteData  () throws TranslationException;
	
	public boolean has(String $key);
	// "opt" methods either return defaults or null in case of errors; their "get" breathren throw exceptions if the requested value is missing or untranslatable.
	public void    put(String $key, byte[] $val);
	public byte[]  getBytes(String $key) throws TranslationException;
	public byte[]  optBytes(String $key);		// different.  returns EMPTY_BYTE.
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
	public void    put(String $key, $TMAP $val);
	public $TMAP   getObj(String $key) throws TranslationException;
	public $TMAP   optObj(String $key);		// null
	public void    put(String $key, $TARR $val);
	public $TARR   getArr(String $key) throws TranslationException;
	public $TARR   optArr(String $key);		// null
	
	
	
	
	/**
	 * This adapter provides wrappers around most of the basic methods that provide
	 * "opt"-like functionality, but still leaves the basic data-oriented methods to
	 * be implemented by concrete subclasses.
	 * 
	 * @author hash
	 * 
	 * @param <$TMAP>
	 * @param <$TARR>
	 */
	public abstract static class Adapter<$TMAP extends EonObject<$TMAP,$TARR>, $TARR extends EonArray<$TMAP,$TARR>> implements EonObject<$TMAP,$TARR> {
		
	}
}
