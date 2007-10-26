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

import com.webobjects.foundation.NSKeyValueCoding;

/**
 * A KeyValueFormatter allows for formatting objects that support key-value
 * coding. <br/>
 * 
 * Reverse conversion is not possible. <br/>
 * 
 * @author Bernard
 */
public class KeyValueFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= -4875508362991654303L;
	
	
	
	// Protected instance variables
	
	protected char[]			format;
	
	
	
	
	// Constructor
	
	/**
	 * Constructor. <br/>
	 * 
	 * @param a
	 *            format String as stringForObjectValue method
	 * @see stringForObjectValue
	 */
	public KeyValueFormatter(String format)
	{
		this(format.toCharArray());
	}
	
	
	
	/**
	 * Alternate constructor that accepts a char array representation of the
	 * format String. <br/>
	 * 
	 * @param a
	 *            char array representation of the format String
	 */
	public KeyValueFormatter(char[] format)
	{
		this.format = format;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Formats an object that supports key-value coding. <br/>
	 * 
	 * To do so it uses the the format String passed to the constructor. The
	 * resulting String is a copy of the format String where any substring that
	 * has the form "%keyName" is replaced by the value that is returned by
	 * calling valueForKey(keyName) on the object to format. <br/>
	 * 
	 * E.g. The following format String could be used to format an NSArray:
	 * "%count elements"
	 * 
	 * @param object
	 *            the object to produce a display String for
	 * @param toAppendTo
	 *            where the text is to be appended
	 * @param pos
	 *            On input: an alignment field, if desired. On output: the
	 *            offsets of the alignment field.
	 * @return the value passed in as toAppendTo (this allows chaining, as with
	 *         StringBuffer.append())
	 */
	public StringBuffer format(Object object, StringBuffer toAppendTo,
			FieldPosition pos)
	{
		if (object == null) {
			return null;
		} else {
			char current;
			StringBuffer identifier = null, result = (toAppendTo != null) ? toAppendTo
					: new StringBuffer();
			boolean wasPercent = false;
			
			for (int i = 0; i < format.length; i++) {
				current = format[i];
				
				if (current == '%') {
					if (wasPercent) {
						result.append('%');
						identifier = null;
					} else {
						identifier = new StringBuffer();
					}
					
					wasPercent = !wasPercent;
				} else {
					if (identifier != null) {
						if (Character.isJavaIdentifierPart(current)) {
							identifier.append(current);
						} else {
							Object value = NSKeyValueCoding.Utility
									.valueForKey(object, identifier.toString());
							
							result.append(value);
							result.append(current);
							
							identifier = null;
						}
					} else {
						result.append(current);
					}
					
					wasPercent = false;
				}
			}
			
			return result;
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
		throw new RuntimeException(
				"KeyValueFormatter does not support String parsing");
	}
}
