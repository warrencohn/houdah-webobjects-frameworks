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

package com.houdah.foundation;

import java.text.Format;
import java.text.ParsePosition;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;

public class KeyFormatter extends KVCObject
{
	// Protected instance variables
	
	protected String	keyPath;
	
	
	protected Format	formatter;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	public KeyFormatter(String keyPath, Format formatter)
	{
		this.keyPath = keyPath;
		this.formatter = formatter;
	}
	
	
	
	// Public instance methods
	
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	public Format formatter()
	{
		return this.formatter;
	}
	
	
	
	// Public class methods
	
	/**
	 * Utility method to build the most simple KeyFormatter possible
	 * 
	 * @param formatter
	 *            a formatter
	 * @return an array with one single KeyFormatter
	 */
	public static NSArray keyFormatters(Format formatter)
	{
		if (formatter != null) {
			return new NSArray(new KeyFormatter[] { new KeyFormatter(null,
					formatter) });
		} else {
			return null;
		}
	}
	
	
	public static String format(Object object, KeyFormatter keyFormatter)
	{
		return KeyFormatter.format(object, new NSArray(keyFormatter));
	}
	
	
	public static String format(Object object, NSArray keyFormatters)
	{
		if (object == null || object == NSKeyValueCoding.NullValue) {
			return null;
		}
		
		if (keyFormatters != null) {
			StringBuffer result = new StringBuffer();
			int kfCount = keyFormatters.count();
			
			for (int kf = 0; kf < kfCount; kf++) {
				Object value = object;
				KeyFormatter keyFormatter = (KeyFormatter) keyFormatters
						.objectAtIndex(kf);
				String _keyPath = keyFormatter.keyPath();
				Format _formatter = keyFormatter.formatter();
				
				if (_keyPath != null) {
					value = KVCUtility.sharedInstance().valueForKeyPath(object,
							_keyPath);
				}
				
				if (value == null) {
					continue;
				}
				
				if (_formatter == null) {
					KeyFormatter.append(result, value.toString());
					
					continue;
				}
				
				try {
					KeyFormatter.append(result, _formatter.format(value));
					
					continue;
				} catch (Exception exception) {
					throw new ForwardException(exception);
				}
			}
			
			return result.toString();
		} else {
			return object.toString();
		}
	}
	
	
	public static NSDictionary parse(String source, ParsePosition pos,
			NSArray keyFormatters)
	{
		if ((source != null) && (keyFormatters != null)) {
			NSArray elements = NSArray.componentsSeparatedByString(source, ",");
			int kfCount = keyFormatters.count();
			
			if (kfCount > elements.count()) {
				pos.setErrorIndex(source.length());
				
				return null;
			}
			
			NSMutableDictionary result = new NSMutableDictionary(kfCount);
			int runningLength = 0;
			
			for (int kf = 0; kf < kfCount; kf++) {
				String element = (String) elements.objectAtIndex(kf);
				Object value = element.trim();
				KeyFormatter keyFormatter = (KeyFormatter) keyFormatters
						.objectAtIndex(kf);
				String _keyPath = keyFormatter.keyPath();
				Format _formatter = keyFormatter.formatter();
				
				if (_formatter != null) {
					ParsePosition position = new ParsePosition(0);
					String string = (String) value;
					
					value = _formatter.parseObject(string, position);
					
					if (position.getErrorIndex() > -1) {
						pos.setErrorIndex(runningLength
								+ position.getErrorIndex());
						
						return null;
					} else if (position.getIndex() < string.length()) {
						pos.setErrorIndex(runningLength + element.length());
						
						return null;
					}
				}
				
				runningLength += element.length() + 1;
				
				if (value == null) {
					value = NSKeyValueCoding.NullValue;
				}
				
				result.setObjectForKey(value, _keyPath);
			}
			
			pos.setIndex(runningLength);
			
			return result;
		} else {
			pos.setErrorIndex(0);
			
			return null;
		}
		
	}
	
	
	
	// Private static class methods
	
	private static void append(StringBuffer buffer, String string)
	{
		if (buffer.length() > 0) {
			buffer.append(", ");
		}
		
		buffer.append(string);
	}
}