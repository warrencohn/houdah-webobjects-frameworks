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

package com.houdah.eovalidation.validation;

import com.houdah.eovalidation.control.HEVValidationException;
import com.houdah.eovalidation.control.ValidatingRecord;
import com.houdah.eovalidation.control.ValidationContext;

public class StringValidation
{
	// Public class constants
	
	public static final String	MAX_LENGTH_KEY	= "maxLength";
	
	
	public static final String	MIN_LENGTH_KEY	= "minLength";
	
	
	
	
	// Public class methods
	
	public static Object maxLength(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if (value != null) {
			String maxLengthString = (String) validationContext
					.valueForKey(StringValidation.MAX_LENGTH_KEY);
			int maxLength = Integer.parseInt(maxLengthString);
			
			if (((String) value).length() > maxLength) {
				throw HEVValidationException.exception(record, key, value, validationContext);
			}
		}
		
		return value;
	}
	
	
	public static Object minLength(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if (value != null) {
			String minLengthString = (String) validationContext
					.valueForKey(StringValidation.MIN_LENGTH_KEY);
			int minLength = Integer.parseInt(minLengthString);
			
			if (((String) value).length() < minLength) {
				throw HEVValidationException.exception(record, key, value, validationContext);
			}
		}
		
		return value;
	}
}