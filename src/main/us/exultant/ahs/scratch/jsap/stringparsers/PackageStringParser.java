/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.scratch.jsap.stringparsers;

import us.exultant.ahs.scratch.jsap.StringParser;
import us.exultant.ahs.scratch.jsap.ParseException;

/**
 * A {@link us.exultant.ahs.scratch.jsap.StringParser} for parsing Packages.  The parse() method delegates the actual
 * parsing to <code>Package.getPackage(String)</code>, and returns the resulting
 * Package object.
 * If <code>Package.getPackage()</code> returns null, a ParseException is
 * thrown.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see us.exultant.ahs.scratch.jsap.StringParser
 * @see java.lang.Package
 */
public class PackageStringParser extends StringParser {
	
	private static final PackageStringParser INSTANCE = new PackageStringParser();	

	/** Returns a {@link PackageStringParser}.
	 * 
	 * <p>Convenient access to the only instance returned by
	 * this method is available through
	 * {@link us.exultant.ahs.scratch.jsap.JSAP#PACKAGE_PARSER}.
	 *  
	 * @return a {@link PackageStringParser}.
	 */
    public static PackageStringParser getParser() {
		return INSTANCE;
	}

	/**
     * Creates a new PackageStringParser
     * @deprecated Use {@link #getParser()} or, even better, {@link us.exultant.ahs.scratch.jsap.JSAP#PACKAGE_PARSER}.
     */
    @Deprecated
    public PackageStringParser() {
        super();
    }

    /**
     * Parses the specified argument into a Package object.  This method
     * delegates the
     * parsing to <code>Package.getPackage(String)</code>, and returns the
     * resulting Package object.
     * If <code>Package.getPackage()</code> returns null, a ParseException is
     * thrown.
     *
     * @param arg the argument to parse
     * @return a Package object representing the specified package.
     * @throws ParseException if <code>Package.getPackage(arg)</code> returns
     * null.
     * @see java.lang.Package
     * @see us.exultant.ahs.scratch.jsap.StringParser#parse(String)
     */
    public Object parse(String arg) throws ParseException {
        Package result = Package.getPackage(arg);
        if (result == null) {
            throw (
                new ParseException("Unable to locate Package '" + arg + "'."));
        }
        return (result);
    }
}
