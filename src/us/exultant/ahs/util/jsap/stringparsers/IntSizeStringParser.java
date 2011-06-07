/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.util.jsap.stringparsers;

import us.exultant.ahs.util.jsap.ParseException;
import us.exultant.ahs.util.jsap.StringParser;

/** A {@link ahs.util.jsap.StringParser} that works like {@link LongSizeStringParser}, but
 * additionally checks that the result is not larger than {@link Integer#MAX_VALUE}.
 * 
 * @author Sebastiano Vigna
 */

public class IntSizeStringParser extends StringParser {

	/** The only instance of this parser. Aliased to <code>JSAP.INT_SIZE_PARSER</code>. */
	final static IntSizeStringParser INSTANCE = new IntSizeStringParser();
	
	private IntSizeStringParser() {}
	
	
	/** Returns the only instance of an {@link IntSizeStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link ahs.util.jsap.JSAP#INTSIZE_PARSER}.
	 *  
	 * @return the only instance of an {@link IntSizeStringParser}.
	 */
	public static IntSizeStringParser getParser() {
		return INSTANCE;
	}

	public Object parse( String arg ) throws ParseException {
		final long size = LongSizeStringParser.parseSize( arg );
		if ( size > Integer.MAX_VALUE ) throw new ParseException( "Integer size '" + arg + "' is too big." );
		return new Integer( (int)size );
	}
}
