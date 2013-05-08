/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 *
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
