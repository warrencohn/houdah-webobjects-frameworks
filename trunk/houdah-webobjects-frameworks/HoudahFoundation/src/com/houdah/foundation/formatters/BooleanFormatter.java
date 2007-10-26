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

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * BooleanFormatter is a very simple formatter that converts Booleans into
 * display Strings.<br/>
 * 
 * @author Bernard
 */
public class BooleanFormatter extends SetFormatter
{
	// Private class constants
	
	private static final long	serialVersionUID	= 7985630938181383895L;
	
	
	
	
	// Constructors
	
	
	/**
	 * Designated constructor
	 * 
	 * @param values
	 *            string representations
	 * @param defaultRepresentation
	 *            representation of integers not in the lookup table
	 */
	public BooleanFormatter(String falseString, String trueString)
	{
		super(BooleanFormatter.dictionaryFromStrings(falseString, trueString),
				null);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Formats an object to produce a string.<br/>
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
		if (object instanceof Boolean) {
			return super.format(object, toAppendTo, pos);
		} else {
			throw new IllegalArgumentException("Object is not a boolean ");
		}
	}
	
	
	
	// Protected class methods
	
	protected static NSDictionary dictionaryFromStrings(String falseString,
			String trueString)
	{
		NSMutableDictionary lookup = new NSMutableDictionary(2);
		
		lookup.setObjectForKey(falseString, Boolean.FALSE);
		lookup.setObjectForKey(trueString, Boolean.TRUE);
		
		return lookup;
	}
}