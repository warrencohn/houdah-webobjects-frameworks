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

package com.houdah.foundation.utilities;

import java.io.File;
import java.io.InputStream;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

/**
 * Utility class. Repository of tools for manipulating <CODE>.plist</CODE>
 * files.<br/>
 * 
 * @author bernard
 */
public class PropertyListUtilities
{
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private PropertyListUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	/**
	 * Creates a dictionary from a <CODE>.plist</CODE> file.<br/>
	 * 
	 * @param plistFile
	 *            a pointer to the input file
	 * @return the dictionary created from the file, null in the event of an
	 *         error
	 */
	public static NSDictionary dictionaryFromFile(File plistFile)
	{
		return dictionaryFromString(StringUtilities.stringFromFile(plistFile));
	}
	
	
	
	/**
	 * Creates a dictionary from an InputStream in <CODE>plist</CODE> format.<br/>
	 * 
	 * @param stream
	 *            the stream to decode
	 * @return the dictionary created from the file, null in the event of an
	 *         error
	 */
	public static NSDictionary dictionaryFromInputStream(InputStream stream)
	{
		return dictionaryFromString(StringUtilities.stringFromInputStream(stream));
	}
	
	
	
	/**
	 * Creates a dictionary from a String in <CODE>plist</CODE> format.<br/>
	 * 
	 * This is a wrapper method for
	 * NSPropertyListSerialization.propertyListFromString()
	 * 
	 * @see com.webobjects.foundation.NSPropertyListSerialization#propertyListFromString
	 * @param string
	 *            the string to decode
	 * @return the dictionary created from the file, null in the event of an
	 *         error
	 */
	public static NSDictionary dictionaryFromString(String string)
	{
		if (string == null) {
			return null;
		} else {
			try {
				return (NSDictionary) NSPropertyListSerialization.propertyListFromString(string);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	
	
	/**
	 * Creates a array from a <CODE>.plist</CODE> file.<br/>
	 * 
	 * @param plistFile
	 *            a pointer to the input file
	 * @return the array created from the file, null in the event of an error
	 */
	public static NSArray arrayFromFile(File plistFile)
	{
		return arrayFromString(StringUtilities.stringFromFile(plistFile));
	}
	
	
	
	/**
	 * Creates a array from an InputStream in <CODE>plist</CODE> format.<br/>
	 * 
	 * @param stream
	 *            the stream to decode
	 * @return the array created from the file, null in the event of an error
	 */
	public static NSArray arrayFromInputStream(InputStream stream)
	{
		return arrayFromString(StringUtilities.stringFromInputStream(stream));
	}
	
	
	
	/**
	 * Creates a array from a String in <CODE>plist</CODE> format.<br/>
	 * 
	 * This is a wrapper method for
	 * NSPropertyListSerialization.propertyListFromString()
	 * 
	 * @see com.webobjects.foundation.NSPropertyListSerialization#propertyListFromString
	 * @param string
	 *            the string to decode
	 * @return the array created from the file, null in the event of an error
	 */
	public static NSArray arrayFromString(String string)
	{
		if (string == null) {
			return null;
		} else {
			try {
				return (NSArray) NSPropertyListSerialization.propertyListFromString(string);
			} catch (Exception e) {
				return null;
			}
		}
	}
}