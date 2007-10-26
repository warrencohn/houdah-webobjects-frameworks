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
 * A StaticFormatter presents the very same string for every object formatted,
 * except for null. <br/>
 * 
 * Reverse conversion is not possible. <br/>
 * 
 * @author Bernard
 */
public class StaticFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= -5831673366972540924L;
	
	
	
	// Protected instance variables
	
	protected String			format;
	
	
	
	
	// Constructor
	
	/**
	 * Constructor. <br/>
	 * 
	 * @param string
	 *            to return when formatting
	 */
	public StaticFormatter(String format)
	{
		this.format = format;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Formats an object. <br/>
	 * 
	 * @param object
	 *            the object to produce a display String for
	 * @param toAppendTo
	 *            where the text is to be appended
	 * @param pos
	 *            On input: an alignment field, if desired. On output: the
	 *            offsets of the alignment field.
	 * @return null if object is null, the format passed to the constructor
	 *         otherwise
	 */
	public StringBuffer format(Object object, StringBuffer toAppendTo, FieldPosition pos)
	{
		if (object == null) {
			return null;
		} else {
			return new StringBuffer(this.format);
		}
	}
	
	
	
	/**
	 * Not supported.
	 * 
	 * @throws RuntimeException
	 *             always does, always will
	 */
	public Object parseObject(String string, ParsePosition status)
	{
		throw new RuntimeException("StaticFormatter does not support String parsing");
	}
}