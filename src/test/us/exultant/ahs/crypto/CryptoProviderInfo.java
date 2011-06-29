package us.exultant.ahs.crypto;

import java.security.*;
import java.util.*;

/**
 * Show information about installed providers.
 */
public class CryptoProviderInfo {
	public static void main(String[] args) {
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			Provider provider = providers[i];
			System.out.println("Provider name: " + provider.getName());
			System.out.println("Provider information: " + provider.getInfo());
			System.out.println("Provider version: " + provider.getVersion());
			Set<?> entries = provider.entrySet();
			Iterator<?> iterator = entries.iterator();
			while (iterator.hasNext()) {
				System.out.println("===========================================");
				System.out.println("Property entry: " + iterator.next());
			}
			System.out.println("\n\n");
		}
	}
}
