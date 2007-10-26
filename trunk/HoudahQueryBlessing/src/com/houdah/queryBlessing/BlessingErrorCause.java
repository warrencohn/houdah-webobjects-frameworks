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

package com.houdah.queryBlessing;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;

public class BlessingErrorCause
{
	// Protected instance variables
	
	protected String		errorMessage;
	
	
	protected String		key;
	
	
	protected Object		value;
	
	
	protected NSSelector	selector;
	
	
	
	
	// Constructor
	
	public BlessingErrorCause(String errorMessage, String key, Object value, NSSelector selector)
	{
		this.errorMessage = errorMessage;
		this.key = key;
		this.value = value;
		this.selector = selector;
	}
	
	
	
	// Public instance methods
	
	public String errorMessage()
	{
		return this.errorMessage;
	}
	
	
	public String key()
	{
		return this.key;
	}
	
	
	public Object value()
	{
		return this.value;
	}
	
	
	public NSSelector selector()
	{
		return this.selector;
	}
	
	
	public NSDictionary dictionary()
	{
		NSMutableDictionary dictionary = new NSMutableDictionary();
		
		if (errorMessage != null) {
			dictionary.setObjectForKey(errorMessage, "errorMessage");
		}
		if (key != null) {
			dictionary.setObjectForKey(key, "key");
		}
		if (value != null) {
			dictionary.setObjectForKey(value, "value");
		}
		if (selector != null) {
			dictionary.setObjectForKey(selector, "selector");
		}
		
		return dictionary;
	}
}