/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.scratch.jsap;

/**
 * An interface describing an object as being able to produce a set of default
 * values.
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see ahs.scratch.jsap.Defaults
 */
public interface DefaultSource {

    /**
     * Returns a set of default values given the configuration described by the
     * specified IDMap.  Any encountered exceptions are stored in the specified
     * JSAPResult.
     * @param idMap an IDMap containing JSAP configuration information.
     * @param exceptionMap the ExceptionMap object within which any Exceptions
     * will be returned.
     * @return a set of default values given the configuration described by the
     * specified IDMap.
     * @see ahs.scratch.jsap.ExceptionMap#addException(String,Exception)
     */
    Defaults getDefaults(IDMap idMap, ExceptionMap exceptionMap);

}
