package us.exultant.ahs.crypto;

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
 * Serialization of CryptoContainer instances includes only ciphertext fields (and
 * initialization vectors, if applicable), and never key material or cleartext. If the
 * last operation performed on a CryptoContainer instance was a decryption, it is typical
 * for the CryptoContainer to throw an exception during subsequent serialization because
 * this state is unexpected.
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
	// the bummer about this interface is that we have no ability to declare actual prototypes for the wrap and unwrap functions, because they have totally different signatures between sym and asym systems, and often even per engine type.
	// the closest i could come to standardizing something is by having the wrap and unwrap functions have no args and all configuration be done by other calls, and that's a little masturbatory, isn't it?  it just pushes the question around.
}



// so what i want is a way to have all of this setup things of the ciphers and padding and etc be encoded quickly, and also demuxable easily.
// and my muxing has historically been built on class names, but within the crypto package i can do things differently and have things demux to cryptocontainer_sym or something and THEN the setup is...more so.

// my api: you're never allowed to not have an IV, and it has to be its own arg.  primary reason: doesn't make any god damn sense to serialize the iv and the key together, so they shouldnt be in a type together either.
//	of course, i'm not actually going to be that much of a hardass about it, because then there'd be tons of stupid byte-copying flying around, but it's tempting.



// biggest fuck right now: you really do have to have the serializable ciphertext objects be completely separate from the cipher-holding objects, because codecs tend to want to return new objects (you have no way to give them one for filling, really).
// maybe the simplest answer is to have a nested class to hold the ciphertext.  would want to do a quick sanity check on how codecs handle nested class names by default.  also might just plain make sense to share the class at that point, since all enc-then-mac things for example will store ciphertext in essentially the same way.
// note that we can have different encoder implementations; some give away the scheme used, and some just cat bytes and look like raw binary and don't work unless you know what you're looking for already (which would in turn probably require some specialized invocations of codecs outside of normal message processing pathways).
