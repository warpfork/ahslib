package ahs.crypto;

/**
 * <p>
 * CryptoContainer objects provide methods for wrapping data in cryptographic envelopes as
 * well as unwrapping cryptographic envelopes to reveal clear text, and are designed to be
 * translating interfaces and serialization targets (not long term storage). Typically
 * their wrap methods will cause the object to be updated to store all of the freshly
 * generated ciphertext, while their unwrap methods will attempt to return the cleartext.
 * CryptoContainer types that provide more properties (i.e. integrity in addition to
 * confidentiality) will...erm, either cache that from the decrypt or else run it when you
 * call it; not sure yet since different approaches are more apt for different ciphers and
 * macs. Initialization of a CryptoContainer typically causes the allocation of one or
 * more ciphers.
 * </p>
 * 
 * <p>
 * Implementers are likely to cache a reference to the last key used as well as maintain a
 * cipher for both encryption and decryption; thus, they will typically be able to
 * automatically optimize cipher re-configuration when the same keys are used
 * consecutively.
 * </p>
 * 
 * <p>
 * Implementers should never be assumed to be thread safe.
 * </p>
 * 
 * <p>
 * These interfaces were not designed with "online" decryption in mind; i.e. developers
 * are expected to queue all ciphertext data packets before invoking decryption. It's the
 * opinion of the designer that if such performance constraints apply, the best solution
 * is encrypting in multiple chunk/packets, since this removes ordering constraints
 * entirely and becomes trivially parallelizable.
 * </p>
 * 
 * 
 * @author hash
 * 
 */
public interface CryptoContainer {
	
}
