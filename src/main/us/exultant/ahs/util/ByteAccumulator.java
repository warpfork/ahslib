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

package us.exultant.ahs.util;

import java.io.*;

/**
 * Use this class in place of a ByteArrayOutputStream in order to get access to the
 * backing byte array when you're done writing without without jumping through a heinously
 * unnecessary copy operation.
 */
public class ByteAccumulator extends ByteArrayOutputStream {
	public ByteAccumulator() { super(); }
	public ByteAccumulator(int $size) { super($size); }
	
	/**
	 * Returns a pointer to the backing array of this accumulator. If more writes are
	 * performed on this accumulator, this array may be mutated; or a new array may be
	 * created, in which case future calls to this method would return a different
	 * pointer than before. {@code getByteArray().length} may be greater than
	 * {@link #size()}.
	 * 
	 * @return a pointer to the backing array of this accumulator.
	 */
	@us.exultant.ahs.anno.DNMR
	public byte[] getByteArray() { return buf; }
}
