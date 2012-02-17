/**
 * <p>
 * The codec module declares generic types and general tools for describing encoding
 * schemas and serialization/deserialization tools. A particular form for encoding that
 * regards arrays and maps in a general form and that is widely understood due to its
 * relationship with JSON is declared in the {@link us.exultant.ahs.codec.eon} package; it
 * is implemented by classes in the {@link us.exultant.ahs.codec.json} and
 * {@link us.exultant.ahs.codec.ebon} packages.
 * </p>
 * 
 * <p>
 * This package ships as part of the <code>Codec</code> module of AHSlib. The
 * {@link us.exultant.ahs.codec.eon Eon} package ships along with this package as part of
 * the essential Codec module. The {@link us.exultant.ahs.codec.json JSON} and
 * {@link us.exultant.ahs.codec.ebon EBON} packages each ship in their own jars, so you
 * only need to include whichever jar is relevant for the Eon encoding implementation your
 * application uses.
 * </p>
 */
package us.exultant.ahs.codec;

