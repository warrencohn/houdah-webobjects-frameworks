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

import java.util.Enumeration;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

/**
 * Utility class for manipulating NSDictionary objects
 * 
 */
public class DictionaryUtilities
{
	// Protected class constants
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private DictionaryUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Creates a dictionary from an array and a keyPath.<br/>
	 * 
	 * The keys are computed by following the keyPath on each item of the array.
	 * 
	 * @param array
	 *            array of values
	 * @param keyKeyPath
	 *            keyPath to the desired key
	 * @param multipleValues
	 *            true if the values should be NSArray instances containing all
	 *            matches
	 * @return a dictionary a dictionary with values from the array
	 */
	public static NSDictionary dictionaryFromArrayWithKeyPath(NSArray array, String keyKeyPath,
			boolean multipleValues)
	{
		return dictionaryFromArrayWithKeyPath(array, keyKeyPath, null, multipleValues);
	}
	
	
	
	/**
	 * Creates a dictionary from an array and two keyPaths.<br/>
	 * 
	 * The keys are computed by following the key keyPath on each item of the
	 * array.<br/>
	 * 
	 * The values are computed by following the value keyPath on each item of
	 * the array. If the value keyPath is null, the array's items are used as
	 * values for the dictionary.
	 * 
	 * @param array
	 *            array of values
	 * @param keyKeyPath
	 *            keyPath to the desired key
	 * @param valueKeyPath
	 *            keyPath to the desired value, may be null
	 * @param multipleValues
	 *            true if the values should be NSArray instances containing all
	 *            matches
	 * @return a dictionary a dictionary with keys and values derived from the
	 *         array
	 */
	public static NSDictionary dictionaryFromArrayWithKeyPath(NSArray array, String keyKeyPath,
			String valueKeyPath, boolean multipleValues)
	{
		int aCount = array.count();
		NSMutableDictionary dictionary = new NSMutableDictionary(aCount);
		
		for (int a = 0; a < aCount; a++) {
			Object object = array.objectAtIndex(a);
			Object key = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, keyKeyPath);
			Object value = object;
			
			if (valueKeyPath != null) {
				value = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, valueKeyPath);
			}
			
			if (key != null) {
				if (value == null) {
					value = NSKeyValueCoding.NullValue;
				}
				
				if (multipleValues) {
					NSMutableArray valueArray = (NSMutableArray) dictionary.objectForKey(key);
					
					if (valueArray == null) {
						dictionary.setObjectForKey(valueArray = new NSMutableArray(), key);
					}
					valueArray.addObject(value);
				} else {
					dictionary.setObjectForKey(value, key);
				}
			}
		}
		
		return dictionary;
	}
	
	
	
	/**
	 * Alternative setObjectForKey(object, key) implemenation.<br/>
	 * 
	 * Interprets a null value as instruction to remove value for key.
	 * 
	 * @param dictionary
	 *            the dictionary to update
	 * @param object
	 *            the value to store, may be null
	 * @param key
	 *            the key for which a value is to store or to remove
	 * @return the modified original dictionary
	 */
	public static NSMutableDictionary safeSetObjectForKey(NSMutableDictionary dictionary,
			Object object, Object key)
	{
		if (object != null) {
			dictionary.setObjectForKey(object, key);
		} else {
			dictionary.removeObjectForKey(key);
		}
		
		return dictionary;
	}
	
	
	
	/**
	 * Custom implementation of "takeValueForKeyPath", applied to
	 * NSMutableDictionary.<br/>
	 * 
	 * Will create NSMutableDictionary recursively to complete the keyPath if
	 * required. I.e. at each itermediary step of the keyPath a dictionary is
	 * created if no value is present yet.
	 * 
	 * @param dictionary
	 *            dictionary to fill
	 * @param value
	 *            value to set
	 * @param keyPath
	 *            keyPath to follow
	 */
	public static void takeValueForKeyPath(NSMutableDictionary dictionary, Object value,
			String keyPath)
	{
		int index = keyPath.indexOf('.');
		if (index < 0) {
			dictionary.takeValueForKey(value, keyPath);
			return;
		} else {
			String firstKey = keyPath.substring(0, index - 1);
			String remainder = keyPath.substring(index);
			Object object = dictionary.valueForKey(firstKey);
			
			if (object == null) {
				object = new NSMutableDictionary();
				dictionary.setObjectForKey(object, firstKey);
			}
			
			if (object instanceof NSMutableDictionary) {
				takeValueForKeyPath((NSMutableDictionary) object, value, remainder);
			} else {
				NSKeyValueCodingAdditions.Utility.takeValueForKeyPath(object, value, remainder);
			}
		}
	}
	
	
	
	/**
	 * Flattens out all values, including those from nested dictionaries
	 * 
	 * @param dictionary
	 *            dictionary to flatten
	 * @return set of values of any type other than NSDictionary
	 */
	public static NSSet allValues(NSDictionary dictionary)
	{
		Enumeration values = dictionary.objectEnumerator();
		NSMutableSet result = new NSMutableSet();
		
		while (values.hasMoreElements()) {
			Object object = values.nextElement();
			
			if (object instanceof NSDictionary) {
				result.unionSet(DictionaryUtilities.allValues((NSDictionary) object));
			} else {
				result.addObject(object);
			}
		}
		
		return result;
	}
}