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

/**
 * DummyFormatter is a very, very simple formatter that does no more than
 * calling the toString() method on the object to format. <br/>
 * 
 * The reverse conversion parseObject does nothing: it simply returns the String
 * it recieved as argument. <br/>
 * 
 * This formatter serves as stand-in in situations where having a formatter is
 * required, but no typical formatting behavior is needed.
 * 
 * @author Bernard
 */
public class DummyFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= -3497738286243031774L;
	
	
	// Constructor
	
	
	/**
	 * Creates a new instance. Nothing to initialize.
	 */
	public DummyFormatter()
	{
		super();
	}
	
	
	
	// Public methods
	
	/**
	 * Formats an object to produce a string.
	 */
	public StringBuffer format(Object object, StringBuffer toAppendTo,
			FieldPosition pos)
	{
		StringBuffer result = (toAppendTo != null) ? toAppendTo
				: new StringBuffer();
		
		return (object != null) ? result.append(object.toString()) : result;
	}
	
	
	
	/**
	 * See class description.
	 */
	public Object parseObject(String string, ParsePosition status)
	{
		status.setIndex(string.length() + 1);
		
		return string;
	}
}