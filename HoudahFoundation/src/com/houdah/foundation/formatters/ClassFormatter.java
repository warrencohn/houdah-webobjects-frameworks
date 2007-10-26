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

public class ClassFormatter extends Format
{
	// Private class constants
	
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4205136786198577923L;
	
	
	
	
	// Constructor
	
	
	/**
	 * Creates a new instance. Nothing to initialize.
	 */
	public ClassFormatter()
	{
		super();
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
		Class valueClass;
		
		try {
			valueClass = Class.forName(name);
		} catch (Exception e) {
			System.err.println(e);
			
			valueClass = null;
		}
		
		if (valueClass != null) {
			status.setIndex(string.length() + 1);
		} else {
			status.setErrorIndex(status.getIndex());
		}
		
		return valueClass;
	}
}