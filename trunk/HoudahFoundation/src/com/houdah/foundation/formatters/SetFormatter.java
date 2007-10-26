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
import java.util.Enumeration;

import com.houdah.foundation.utilities.IntegerFactory;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

/**
 * A SetFormatter allows for assigning display strings to a fixed set of
 * objects. <br/>
 * 
 * Reverse conversion is possible within limits. Reverse conversion is case
 * insensitive. String representations must be unique. <br/>
 * 
 * @author Bernard
 */
public class SetFormatter extends Format
{
	// Private class constants
	
	private static final long	serialVersionUID	= 6258931930396936351L;
	
	
	
	// Private instance variables
	
	private NSDictionary		forwardLookup;
	
	
	private NSDictionary		reverseLookup;
	
	
	private Integer[]			stringLengths;
	
	
	private String				defaultRepresentation;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor. <br/>
	 * 
	 * @param forwardLookup
	 *            lookup table giving String values for a set of objects
	 * @param defaultRepresentation
	 *            representation of objects not in the lookup table
	 */
	
	public SetFormatter(NSDictionary forwardLookup, String defaultRepresentation)
	{
		this.forwardLookup = forwardLookup.immutableClone();
		this.reverseLookup = null;
		this.defaultRepresentation = defaultRepresentation;
		
		Enumeration values = this.forwardLookup.allValues().objectEnumerator();
		
		while (values.hasMoreElements()) {
			if (!(values.nextElement() instanceof String)) {
				throw new IllegalArgumentException("Values need to be strings");
			}
		}
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
		String represenation = (String) this.forwardLookup.objectForKey(object);
		
		if (represenation == null) {
			represenation = this.defaultRepresentation;
		}
		
		return toAppendTo.append(represenation);
	}
	
	
	
	/**
	 * Parses text from a string to produce an object from the lookup table.
	 * <br/>
	 * 
	 * The method attempts to parse text starting at the index given by pos. If
	 * parsing succeeds, then the index of pos is updated to the index after the
	 * last character used (parsing does not necessarily use all characters up
	 * to the end of the string), and the parsed date is returned. The updated
	 * pos can be used to indicate the starting point for the next call to this
	 * method. If an error occurs, then the index of pos is not changed, the
	 * error index of pos is set to the index of the character where the error
	 * occurred, and null is returned.
	 * 
	 * @param source
	 *            a String, part of which should be parsed.
	 * @param pos
	 *            a ParsePosition object with index and error index information
	 *            as described above.
	 * @return an object parsed from the string. In case of error, returns null.
	 */
	public Object parseObject(String string, ParsePosition pos)
	{
		primeReverseLookup();
		
		int index = pos.getIndex();
		int maxLength = this.stringLengths[0].intValue();
		int available = string.length() - index;
		
		if (maxLength > available) {
			maxLength = available;
		}
		
		String maxstring = string.substring(index, index + maxLength)
				.toUpperCase();
		
		int lCount = this.stringLengths.length;
		
		for (int l = 0; l < lCount; l++) {
			int length = this.stringLengths[l].intValue();
			
			if (length <= maxLength) {
				String substring = maxstring.substring(0, length);
				Object object = this.reverseLookup.objectForKey(substring);
				
				if (object != null) {
					pos.setIndex(index + length);
					
					return object;
				}
			}
		}
		
		return null;
	}
	
	
	
	// Protected instance methods
	
	protected synchronized void primeReverseLookup()
	{
		if (this.reverseLookup == null) {
			NSArray keys = this.forwardLookup.allKeys();
			int kCount = keys.count();
			NSMutableDictionary lookup = new NSMutableDictionary(kCount);
			NSMutableSet lengths = new NSMutableSet();
			
			for (int k = 0; k < kCount; k++) {
				Object key = keys.objectAtIndex(k);
				String value = (String) this.forwardLookup.objectForKey(key);
				String ucValue = value.toUpperCase();
				
				if (lookup.objectForKey(ucValue) == null) {
					int vLength = ucValue.length();
					
					lookup.setObjectForKey(key, ucValue);
					lengths.addObject(IntegerFactory.integerForInt(vLength));
				} else {
					throw new IllegalArgumentException(
							"Parsing requires unique string representations");
				}
			}
			
			NSArray sortedLengths = null;
			
			try {
				sortedLengths = lengths.allObjects()
						.sortedArrayUsingComparator(
								NSComparator.DescendingNumberComparator);
				
			} catch (NSComparator.ComparisonException ce) {
				throw new RuntimeException("Bug", ce);
			}
			
			Object[] objects = sortedLengths.objects();
			Integer[] integers = new Integer[sortedLengths.count()];
			
			System.arraycopy(objects, 0, integers, 0, objects.length);
			
			this.stringLengths = integers;
			this.reverseLookup = lookup.immutableClone();
		}
	}
}