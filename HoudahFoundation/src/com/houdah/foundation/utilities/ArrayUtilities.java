/*
 * Modified MIT License
 * 
 * Copyright (c) 2006-2007 Houdah Software s.à r.l.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or other 
 * dealings in this Software without prior written authorization.
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
**/

package com.houdah.foundation.utilities;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Utility class for manipulating NSArray objects
 * 
 * @author bernard
 */
public class ArrayUtilities
{
	// Protected class constants
	
	/**
	 * Character used for separating the different components in string
	 * representation of an array of strings
	 */
	protected static final char		SEPARATOR_CHAR		= ';';
	
	
	
	/**
	 * String used for separating the different components in string
	 * representation of an array of strings
	 */
	protected static final String	SEPARATOR_STRING	= new String(new char[] { SEPARATOR_CHAR });
	
	
	
	/**
	 * Character used to escape special chars in string representation of an
	 * array of strings
	 */
	protected static final char		ESCAPE_CHAR			= '\\';
	
	
	
	/**
	 * String used to escape special chars in string representation of an array
	 * of strings
	 */
	protected static final String	ESCAPE_STRING		= new String(new char[] { ESCAPE_CHAR });
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private ArrayUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Creates a string by concatenating all elements of an array.<br/>
	 * 
	 * In the resulting string the elements are separated by a semi-colon
	 * character. Semi-colon characters found in the input strings are escaped
	 * by prefixing a backslash character. So are the backslash characters
	 * themselves.
	 * 
	 * @param strings
	 *            the array of elements to encode
	 * @return the string representation of the array
	 * @see #stringArrayFromString
	 */
	public static String stringFromStringArray(NSArray strings)
	{
		if (strings == null) {
			return null;
		} else {
			int count = strings.count();
			
			if (count == 0) {
				return "";
			} else {
				StringBuffer buffer = new StringBuffer(count * 10);
				
				buffer.append(StringUtilities.escapeString(strings.objectAtIndex(0).toString(), SEPARATOR_CHAR, ESCAPE_CHAR));
				
				for (int i = 1; i < count; i++) {
					buffer.append(SEPARATOR_CHAR);
					buffer.append(StringUtilities.escapeString(strings.objectAtIndex(i).toString(), SEPARATOR_CHAR, ESCAPE_CHAR));
				}
				
				return buffer.toString();
			}
		}
	}
	
	
	
	/**
	 * Restores an array from its string representation.<br/>
	 * 
	 * The restored array is equals() equivalent to the originally encoded array
	 * only if the original array held only string objects. The decoded array
	 * holds only strings which may be the result of calling toString() on an
	 * element of the original array.
	 * 
	 * @param string
	 *            the string representation of the array
	 * @return the restored array
	 * @see #stringArrayFromString
	 */
	public static NSArray stringArrayFromString(String string)
	{
		if (string == null) {
			return null;
		} else {
			int length = string.length();
			NSMutableArray array = new NSMutableArray();
			StringBuffer buffer = new StringBuffer(10);
			char[] chars = new char[length];
			int i = 0;
			
			
			// Copy the string into an array
			string.getChars(0, length, chars, 0);
			
			while (i < length) {
				char c = chars[i++];
				
				if (c == SEPARATOR_CHAR) {
					// Store the current string and move on to the next one
					array.addObject(buffer.toString());
					buffer = new StringBuffer(10);
				} else if (c == ESCAPE_CHAR) {
					char next = chars[i];
					
					
					// Handle escaped special chars
					if ((next == SEPARATOR_CHAR) || (next == ESCAPE_CHAR)) {
						buffer.append(next);
						i++;
					}
				} else {
					// Plain chars get appended to the current string
					buffer.append(c);
				}
			}
			
			
			// Append the last element
			array.addObject(buffer.toString());
			
			return array;
		}
	}
	
	
	
	/**
	 * Flatten nested arrays into one single array.
	 * 
	 * @param object
	 *            the object or array to flatten
	 * @return the flattened array if object is an NSArray or a Collection.
	 *         Otehrwise the object itself
	 */
	public static Object flatten(Object object)
	{
		if (object instanceof NSArray) {
			NSArray array = (NSArray) object;
			Enumeration enumeration = array.objectEnumerator();
			NSMutableArray outArray = new NSMutableArray(array.count());
			
			while (enumeration.hasMoreElements()) {
				Object item = enumeration.nextElement();
				Object flattenedItem = ArrayUtilities.flatten(item);
				
				if (flattenedItem instanceof NSArray) {
					outArray.addObjectsFromArray((NSArray) flattenedItem);
				} else {
					outArray.addObject(flattenedItem);
				}
			}
			
			return outArray;
		} else if (object instanceof Collection) {
			Collection collection = (Collection) object;
			Iterator iterator = collection.iterator();
			NSMutableArray outArray = new NSMutableArray(collection.size());
			
			while (iterator.hasNext()) {
				Object item = iterator.next();
				Object flattenedItem = ArrayUtilities.flatten(item);
				
				if (flattenedItem instanceof NSArray) {
					outArray.addObjectsFromArray((NSArray) flattenedItem);
				} else {
					outArray.addObject(flattenedItem);
				}
			}
			
			return outArray;
		}
		
		return object;
	}
}