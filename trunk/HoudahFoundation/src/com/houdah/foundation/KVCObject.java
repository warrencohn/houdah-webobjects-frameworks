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

import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSKeyValueCoding.ErrorHandling;

public class KVCObject implements NSKeyValueCoding, NSKeyValueCodingAdditions, ErrorHandling
{
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 */
	public KVCObject()
	{
		super();
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding#valueForKey(java.lang.String)
	 */
	public Object valueForKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding#takeValueForKey(java.lang.Object,
	 *      java.lang.String)
	 */
	public void takeValueForKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCodingAdditions#valueForKeyPath(java.lang.String)
	 */
	public Object valueForKeyPath(String keyPath)
	{
		return com.webobjects.foundation.NSKeyValueCodingAdditions.DefaultImplementation
				.valueForKeyPath(this, keyPath);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCodingAdditions#takeValueForKeyPath(java.lang.Object,
	 *      java.lang.String)
	 */
	public void takeValueForKeyPath(Object value, String keyPath)
	{
		NSKeyValueCodingAdditions.DefaultImplementation.takeValueForKeyPath(this, value, keyPath);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding.ErrorHandling#handleQueryWithUnboundKey(java.lang.String)
	 */
	public Object handleQueryWithUnboundKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.handleQueryWithUnboundKey(this, key);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding.ErrorHandling#handleTakeValueForUnboundKey(java.lang.Object,
	 *      java.lang.String)
	 */
	public void handleTakeValueForUnboundKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.handleTakeValueForUnboundKey(this, value, key);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding.ErrorHandling#unableToSetNullForKey(java.lang.String)
	 */
	public void unableToSetNullForKey(String key)
	{
		NSKeyValueCoding.DefaultImplementation.unableToSetNullForKey(this, key);
	}
}
