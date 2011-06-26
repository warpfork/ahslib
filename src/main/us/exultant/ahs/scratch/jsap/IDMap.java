/*
 * Copyright (c) 2002-2004, Martian Software, Inc.
 * This file is made available under the LGPL as described in the accompanying
 * LICENSE.TXT file.
 */

package us.exultant.ahs.scratch.jsap;

import java.util.*;

/**
 * A utility class to allow lookups of parameter IDs by short flag or long flag.
 * This class is used by DefaultSource in order to populate Defaults objects.
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see ahs.scratch.jsap.Flagged
 * @see ahs.scratch.jsap.DefaultSource
 * @see ahs.scratch.jsap.Defaults
 */
public class IDMap {

    /**
     * A list of the unique IDs of all the parameters stored in this IDMap.
     */
    private List<String> ids = null;

    /**
     * A Map associating short flags with parameter IDs.
     */
    private Map<String,String> byShortFlag = null;

    /**
     * A Map associating long flags with parameter IDs.
     */
    private Map<String,String> byLongFlag = null;

    /**
     * Creates a new IDMap.
     * @param ids a List of the unique IDs of all the parameters to store
     * in this IDMap.
     * @param byShortFlag a Map with keys equal to the short flags of the
     * parameters (as Character objects),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     * @param byLongFlag a Map with keys equal to the long flags of the
     * parameters (as Strings),
     * and values equal to the unique IDs of the parameters associated with
     * those short flags.
     */
    public IDMap(List<String> ids, Map<String,String> byShortFlag, Map<String,String> byLongFlag) {
        this.ids = new ArrayList<String>(ids);
        this.byShortFlag = new HashMap<String,String>(byShortFlag);
        this.byLongFlag = new HashMap<String,String>(byLongFlag);
    }

    /**
     * Returns an Iterator over all parameter IDs stored in this IDMap.
     * @return an Iterator over all parameter IDs stored in this IDMap.
     * @see java.util.Iterator
     */
    public Iterator<String> idIterator() {
        return (ids.iterator());
    }

    /**
     * Returns true if the specified ID is stored in this IDMap, false if not.
     * @param id the id to search for in this IDMap
     * @return true if the specified ID is stored in this IDMap, false if not.
     */
    public boolean idExists(String id) {
        return (ids.contains(id));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(Character c) {
        return (byShortFlag.get(c));
    }

    /**
     * Returns the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     * @param c the short flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified short flag, or
     * null if the specified short flag is not defined in this IDMap.
     */
    public String getIDByShortFlag(char c) {
        return (getIDByShortFlag(new Character(c)));
    }

    /**
     * Returns the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     * @param s the long flag to search for in this IDMap.
     * @return the unique ID of the parameter with the specified long flag, or
     * null if the specified long flag is not defined in this IDMap.
     */
    public String getIDByLongFlag(String s) {
        return (byLongFlag.get(s));
    }
}
