/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.scratch.jsap.stringparsers;

import us.exultant.ahs.scratch.jsap.StringParser;
import us.exultant.ahs.scratch.jsap.ParseException;

/**
 * A {@link us.exultant.ahs.scratch.jsap.StringParser} for parsing Shorts.  The parse() method delegates the actual
 * parsing to <code>Short.decode(String)</code>.  If <code>Short.decode()</code>
 * throws a
 * NumberFormatException, it is encapsulated in a ParseException and re-thrown.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see us.exultant.ahs.scratch.jsap.StringParser
 * @see java.math.BigDecimal
 */
public class ShortStringParser extends StringParser {

	private static final ShortStringParser INSTANCE = new ShortStringParser();	

	/** Returns a {@link ShortStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link us.exultant.ahs.scratch.jsap.JSAP#SHORT_PARSER}.
	 *  
	 * @return a {@link ShortStringParser}.
	 */

    public static ShortStringParser getParser() {
		return INSTANCE;
	}

	/**
     * Creates a new ShortStringParser.
     * @deprecated Use {@link #getParser()} or, even better, {@link us.exultant.ahs.scratch.jsap.JSAP#SHORT_PARSER}.
     */
    @Deprecated
    public ShortStringParser() {
        super();
    }

    /**
     * Parses the specified argument into a Short.  This method delegates the
     * parsing to <code>Short.decode(String)</code>.  If
     * <code>Short.decode()</code> throws a
     * NumberFormatException, it is encapsulated in a ParseException and
     * re-thrown.
     *
     * @param arg the argument to parse
     * @return a Short object with the value contained in the specified
     * argument.
     * @throws ParseException if <code>Short.decode(arg)</code> throws a
     * NumberFormatException.
     * @see java.lang.Short
     * @see us.exultant.ahs.scratch.jsap.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Short result = null;
        try {
            result = Short.decode(arg);
        } catch (NumberFormatException e) {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a Short.",
                    e));
        }
        return (result);
    }
}
