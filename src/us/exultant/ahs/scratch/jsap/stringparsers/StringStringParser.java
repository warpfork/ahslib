/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.scratch.jsap.stringparsers;

import us.exultant.ahs.scratch.jsap.StringParser;

/**
 * A {@link ahs.scratch.jsap.StringParser} for parsing Strings.  This is the simplest possible
 * StringParser, simply returning
 * the specified argument in all cases.  This class never throws a
 * ParseException under any circumstances.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see ahs.scratch.jsap.StringParser
 * @see java.lang.String
 */
public class StringStringParser extends StringParser {

	private static final StringStringParser INSTANCE = new StringStringParser();	

	/** Returns a {@link StringStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link ahs.scratch.jsap.JSAP#STRING_PARSER}.
	 *  
	 * @return a {@link StringStringParser}.
	 */

    public static StringStringParser getParser() {
		return INSTANCE;
	}

	/**
     * Creates a new StringStringParser.
     * @deprecated Use {@link #getParser()} or, even better, {@link ahs.scratch.jsap.JSAP#STRING_PARSER}.
     */
    public StringStringParser() {
        super();
    }

    /**
     * Returns the specified argument as a String.
     *
     * @param arg the argument to parse
     * @return the specified argument as a String.
     * @see java.lang.String
     * @see ahs.scratch.jsap.StringParser#parse(String)
     */
    public Object parse(String arg) {
        return (arg);
    }
}
