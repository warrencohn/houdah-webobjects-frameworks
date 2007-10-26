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

import com.houdah.foundation.KeyFormatter;

import com.webobjects.foundation.NSArray;

/**
 * Wrapper around the KeyFormatter mechanism
 */
public class KeyFormatterFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= 6301038382664184385L;
	
	
	
	// Protected instance variables
	
	protected NSArray			keyFormatters;
	
	
	
	
	// Constructor
	
	
	/**
	 * Convenience constructor
	 * 
	 * @param keyPath
	 *            a path to the value
	 * @param formatter
	 *            formatter for the value
	 */
	public KeyFormatterFormatter(String keyPath, Format formatter)
	{
		this(new KeyFormatter(keyPath, formatter));
	}
	
	
	
	/**
	 * Convenience constructor
	 * 
	 * @param keyFormatter
	 *            a KeyFormatter instance
	 * @see com.houdah.foundation.KeyFormatter
	 */
	public KeyFormatterFormatter(KeyFormatter keyFormatter)
	{
		this(new NSArray(keyFormatter));
	}
	
	
	
	/**
	 * Designated constructor
	 * 
	 * @param keyFormatters
	 *            array of KeyFormatter instances
	 * @see com.houdah.foundation.KeyFormatter
	 */
	public KeyFormatterFormatter(NSArray keyFormatters)
	{
		this.keyFormatters = (keyFormatters == null) ? null : keyFormatters.immutableClone();
	}
	
	
	
	// Public instance methods
	
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		String string = KeyFormatter.format(obj, this.keyFormatters);
		
		if (string != null) {
			toAppendTo.append(string);
		}
		
		return toAppendTo;
	}
	
	
	public Object parseObject(String source, ParsePosition pos)
	{
		return KeyFormatter.parse(source, pos, this.keyFormatters);
	}
}