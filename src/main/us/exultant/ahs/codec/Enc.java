/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.codec;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Enc {
	String value() default "";	// I wish this could just be null.
	String[] selected() default { DEFAULT };
	public static final String DEFAULT = "$";
	public static final String SELECTED = "!";
	// someday it might behoove us to have an instantiableClass field here.  otherwise, how can we deal with generic interfaces (List<?> being a prime example)?
	//   i mean, we can detect that specific case since it's so common, but what about when people specifically want a linked list for performance reasons?
	//      they can revert to having some sort of assertInvariants method that takes care of it, sure (such a method should exist anyway), but still, ugh.
}
