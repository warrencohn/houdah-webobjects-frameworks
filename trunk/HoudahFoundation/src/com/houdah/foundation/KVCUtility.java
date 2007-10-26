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

package com.houdah.foundation;

import java.util.Collection;
import java.util.Iterator;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;

public class KVCUtility
{
	// Public class constants
	
	public static final String	KEY_PATH_SEPARATOR		= NSKeyValueCodingAdditions.KeyPathSeparator;
	
	
	public static final char	KEY_PATH_SEPARATOR_CHAR	= KEY_PATH_SEPARATOR.charAt(0);
	
	
	
	// Private class variables
	
	private static KVCUtility	sharedInstance			= new KVCUtility();
	
	
	
	
	// Constructor
	
	protected KVCUtility()
	{
		super();
	}
	
	
	
	// Public class methods
	
	public Object valueForKey(Object object, String key)
	{
		return NSKeyValueCoding.Utility.valueForKey(object, key);
	}
	
	
	public void takeValueForKey(Object object, Object value, String key)
	{
		NSKeyValueCoding.Utility.takeValueForKey(object, value, key);
	}
	
	
	public Object valueForKeyPath(Object object, String keyPath)
	{
		return NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, keyPath);
	}
	
	
	public void takeValueForKeyPath(Object object, Object value, String keyPath)
	{
		NSKeyValueCodingAdditions.Utility.takeValueForKeyPath(object, value, keyPath);
	}
	
	
	public Object flatten(Object object)
	{
		if (object instanceof Collection) {
			Collection collection = (Collection) object;
			Iterator iterator = collection.iterator();
			NSMutableArray outArray = new NSMutableArray(collection.size());
			
			while (iterator.hasNext()) {
				Object item = iterator.next();
				Object flattenedItem = flatten(item);
				
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
	
	
	public String allButLastPathComponent(String path)
	{
		int i = path.lastIndexOf(KEY_PATH_SEPARATOR_CHAR);
		
		return (i < 0) ? null : path.substring(0, i);
	}
	
	
	public String lastPathComponent(String path)
	{
		int i = path.lastIndexOf(KEY_PATH_SEPARATOR_CHAR);
		
		return (i < 0) ? path : path.substring(i + 1);
	}
	
	
	public String firstPathComponent(String path)
	{
		int i = path.indexOf(KEY_PATH_SEPARATOR_CHAR);
		
		return (i < 0) ? path : path.substring(0, i);
	}
	
	
	
	// Public class methods
	
	public static KVCUtility sharedInstance()
	{
		return KVCUtility.sharedInstance;
	}
	
	
	public static void setSharedInstance(KVCUtility utility)
	{
		KVCUtility.sharedInstance = utility;
	}
}
