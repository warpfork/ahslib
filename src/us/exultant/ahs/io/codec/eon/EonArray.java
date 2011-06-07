package us.exultant.ahs.io.codec.eon;

import us.exultant.ahs.io.*;
import us.exultant.ahs.io.codec.*;

public interface EonArray {
	public int     size();
	// "opt" methods either return defaults or null in case of errors; their "get" breathren throw exceptions if the requested value is missing or untranslatable.
	public void    put(int $index, byte[] $val);
	public byte[]  getBytes(int $index) throws TranslationException;
	public byte[]  optBytes(int $index);
	public byte[]  optBytes(int $index, byte[] $default);
	public void    put(int $index, boolean $val);
	public boolean getBoolean(int $index) throws TranslationException;
	public boolean optBoolean(int $index, boolean $default);
	public void    put(int $index, double $val) throws UnencodableException;
	public double  getDouble(int $index)        throws TranslationException;
	public double  optDouble(int $index, double $default);
	public void    put(int $index, int $val) throws UnencodableException;
	public int     getInt(int $index)        throws TranslationException;
	public int     optInt(int $index, int $default);
	public void    put(int $index, long $val) throws UnencodableException;
	public long    getLong(int $index)        throws TranslationException;
	public long    optLong(int $index, long $default);
	public void    put(int $index, String $val);
	public String  getString(int $index) throws TranslationException;
	public String  optString(int $index);
	public String  optString(int $index, String $default);
	public void    put(int $index, EonObject $val);
	public EonObject   getObj(int $index) throws TranslationException;
	public EonObject   optObj(int $index);
	public void    put(int $index, EonArray $val);
	public EonArray   getArr(int $index) throws TranslationException;
	public EonArray   optArr(int $index);
}
