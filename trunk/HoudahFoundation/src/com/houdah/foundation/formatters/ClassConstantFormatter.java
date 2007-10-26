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

package com.houdah.foundation.formatters;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class ClassConstantFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= 7079214282254871730L;
	
	
	
	// Private instance variables
	
	private Class				owner;
	
	
	
	
	// Constructor
	
	
	/**
	 * Designated constructor
	 */
	public ClassConstantFormatter(Class owner)
	{
		this.owner = owner;
	}
	
	
	
	// Public methods
	
	/**
	 * Formats an object to produce a string.
	 */
	public StringBuffer format(Object object, StringBuffer toAppendTo, FieldPosition pos)
	{
		StringBuffer result = (toAppendTo != null) ? toAppendTo : new StringBuffer();
		
		return (object != null) ? result.append(object.toString()) : result;
	}
	
	
	public Object parseObject(String string, ParsePosition status)
	{
		String name = string.substring(status.getIndex());
		Object value;
		
		try {
			value = this.owner.getField(name).get(this.owner);
		} catch (Exception e) {
			System.err.println(e);
			
			value = null;
		}
		
		if (value != null) {
			status.setIndex(string.length() + 1);
		} else {
			status.setErrorIndex(status.getIndex());
		}
		
		return value;
	}
}