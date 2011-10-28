/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.exultant.ahs.codec.ebon;

import java.io.*;

/** I need this class purely in order to get access to the backing byte array when i'm done writing without jumping through a heinously unnecessary copy operation. */
class Bah extends ByteArrayOutputStream {
	public Bah(int $size) { super($size); }
	
	/** DNMR */
	public byte[] getByteArray() { return buf; }
}