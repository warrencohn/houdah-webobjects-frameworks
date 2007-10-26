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

package com.houdah.eovalidation.control;

import com.houdah.messages.MessageException;
import com.houdah.messages.MessageFactory;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSValidation;
import com.webobjects.foundation.NSValidation.ValidationException;


public class HEVValidationException extends NSValidation.ValidationException implements
		MessageException
{
	// Public class constants
	
	public static final String	EXCEPTION_KEY		= "exception";
	
	
	public static final String	KEYS_KEY			= "keys";
	
	
	public static final String	MESSAGE_CODE_KEY	= MessageFactory.MESSAGE_CODE_KEY;
	
	
	
	// Private class constants
	
	private static final long	serialVersionUID	= 5811035069546088280L;
	
	
	
	// Private instance variables
	
	private NSDictionary		context;
	
	
	private NSArray				allKeys;
	
	
	
	
	// Constructor
	
	public HEVValidationException(String message, Object object, String key)
	{
		super(message, object, key);
	}
	
	
	
	// Public instance methods
	
	public String messageCode()
	{
		return getMessage();
	}
	
	
	public NSDictionary context()
	{
		return this.context;
	}
	
	
	public void setContext(NSDictionary context)
	{
		this.context = context.immutableClone();
	}
	
	
	public NSArray allKeys()
	{
		if (this.allKeys == null) {
			setAllKeys(new NSArray(key()));
		}
		
		return this.allKeys;
	}
	
	
	public void setAllKeys(NSArray allKeys)
	{
		this.allKeys = allKeys;
	}
	
	
	
	// Public class methods
	
	public static ValidationException exception(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext)
	{
		String message = (String) validationContext.valueForKey(MESSAGE_CODE_KEY);
		Object keys = validationContext.valueForKey(KEYS_KEY);
		NSArray keyArray;
		
		if (keys instanceof NSArray) {
			keyArray = (NSArray) keys;
			
			if (keyArray.count() == 0) {
				keyArray = new NSArray(key);
			}
		} else if (keys instanceof String) {
			keyArray = new NSArray(keys);
		} else {
			keyArray = new NSArray(key);
		}
		
		HEVValidationException exception = new HEVValidationException(message, record,
				(String) keyArray.objectAtIndex(0));
		
		exception.setContext(validationContext.localValues());
		exception.setAllKeys(keyArray);
		
		return exception;
	}
}