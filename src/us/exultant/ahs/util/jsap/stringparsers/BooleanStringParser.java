/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.util.jsap.stringparsers;

import us.exultant.ahs.util.jsap.StringParser;
import us.exultant.ahs.util.jsap.ParseException;

/**
 * <p>A {@link ahs.util.jsap.StringParser} for parsing Booleans.  This StringParser is also used
 * internally
 * by the Switch class.</p>
 *
 * <p>When parsing, the following arguments are interpreted as TRUE:
 * <ul>
 *         <li>null</i>
 *         <li>"t" (case-insensitive)</li>
 *         <li>"true" (case-insensitive)</li>
 *         <li>"y" (case-insensitive)</li>
 *         <li>"yes" (case-insensitive)</li>
 *         <li>"1"</li>
 * </ul>
 * <p>The following arguments are interpreted as FALSE:
 * <ul>
 *         <li>"f" (case-insensitive)</li>
 *         <li>"false" (case-insensitive)</li>
 *         <li>"n" (case-insensitive)</li>
 *         <li>"no" (case-insensitive)</li>
 *         <li>"0"</li>
 * </ul>
 * 
 * <p>All other input throws a ParseException.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see ahs.util.jsap.StringParser
 * @see java.lang.Boolean
 */
public class BooleanStringParser extends StringParser {
	private static final BooleanStringParser	INSTANCE	= new BooleanStringParser();
	
	/**
	 * Returns a {@link BooleanStringParser}.
	 * 
	 * <p>
	 * Convenient access to the only instance returned by this method is available
	 * through {@link ahs.util.jsap.JSAP#BOOLEAN_PARSER}.
	 * 
	 * @return a {@link BooleanStringParser}.
	 */
	
	public static BooleanStringParser getParser() {
		return INSTANCE;
	}
	
	/**
	 * Creates a new BooleanStringParser
	 * 
	 * @deprecated Use {@link #getParser()} or, even better,
	 *             {@link ahs.util.jsap.JSAP#BOOLEAN_PARSER}.
	 */
	public BooleanStringParser() {
		super();
	}

    /**
     * Converts the specified argument into a Boolean.
     *
     * <p>When parsing, the following arguments are interpreted as TRUE:
     * <ul>
     *         <li>null</i>
     *         <li>"t" (case-insensitive)</li>
     *         <li>"true" (case-insensitive)</li>
     *         <li>"y" (case-insensitive)</li>
     *         <li>"yes" (case-insensitive)</li>
     *         <li>"1"</li>
     * <ul>
     * <p>The following arguments are interpreted as FALSE:
     * <ul>
     *         <li>"f" (case-insensitive)</li>
     *         <li>"false" (case-insensitive)</li>
     *         <li>"n" (case-insensitive)</li>
     *         <li>"no" (case-insensitive)</li>
     *         <li>"0"</li>
     * </ul>
     * 
     * <p>All other input throws a ParseException.
     * @param arg the argument to convert to a Boolean.
     * @return a Boolean as described above.
     * @throws ParseException if none of the above cases are matched.
     */
    public Boolean parse(String arg) throws ParseException {
        boolean result = false;
        if ((arg == null)
            || arg.equalsIgnoreCase("t")
            || arg.equalsIgnoreCase("true")
            || arg.equalsIgnoreCase("y")
            || arg.equalsIgnoreCase("yes")
            || arg.equals("1")) {
            result = true;
        } else if (
            arg.equalsIgnoreCase("f")
                || arg.equalsIgnoreCase("false")
                || arg.equalsIgnoreCase("n")
                || arg.equalsIgnoreCase("no")
                || arg.equals("0")) {
            result = false;
        } else {
            throw (
                new ParseException(
                    "Unable to convert '" + arg + "' to a boolean value."));
        }
        return new Boolean(result);
    }
}
