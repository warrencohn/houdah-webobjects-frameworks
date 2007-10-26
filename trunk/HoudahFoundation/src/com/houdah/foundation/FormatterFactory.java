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

import com.houdah.foundation.formatters.DummyFormatter;

import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;

public class FormatterFactory implements NSKeyValueCoding
{
	// Private class variables
	
	private static FormatterFactory	sharedInstance	= new FormatterFactory();
	
	
	
	// Private instance variables
	
	private NSMutableDictionary		formatters;
	
	
	
	
	// Constructor
	
	private FormatterFactory()
	{
		this.formatters = new NSMutableDictionary();
	}
	
	
	
	// Public class methods
	
	public static FormatterFactory sharedInstance()
	{
		return FormatterFactory.sharedInstance;
	}
	
	
	
	// Public instance methods
	
	/**
	 * Look up a formatter by name. Must have been stored opreviously.
	 * 
	 * @param a
	 *            name which uniquely identifies a formatter
	 * @return a formatter. Never null, a DummyFormatter at worst.
	 */
	public Format lookup(String name)
	{
		Format format = _lookup(name);
		
		if (format != null) {
			return (Format) format.clone();
		}
		
		return dummyFormatter();
	}
	
	
	
	/**
	 * Store a formatter for a given name.<br/>
	 * 
	 * Usually called during application initialization.
	 * 
	 * @param format
	 *            a formatter instance
	 * @param name
	 *            a unique name
	 */
	public void store(Format format, String name)
	{
		Object existing = this.formatters.objectForKey(name);
		
		if (existing == null) {
			this.formatters.setObjectForKey(format.clone(), name);
		} else if (existing.equals(format)) {
			return;
		} else {
			throw new IllegalStateException("A formatter with that name has already been defined");
		}
	}
	
	
	
	/**
	 * Lookup the formatter for a given object class. Recursively climbs the
	 * inhertitance hierachy to find the best fit.
	 * 
	 * @param objectClass
	 *            the class of which objects need to be formatted
	 * @return a formatter. Not null, a DummyFormatter at worst
	 */
	public Format lookup(Class objectClass)
	{
		if (objectClass != null) {
			String name = "#class." + objectClass.getName();
			Format format = lookup(name);
			
			if (format != null) {
				return format;
			} else {
				Class superClass = objectClass.getSuperclass();
				
				return lookup(superClass);
			}
		}
		
		return dummyFormatter();
	}
	
	
	
	/**
	 * Store a formatter for a given object class.<br/>
	 * 
	 * Usually called during application initialization.
	 * 
	 * @param format
	 *            a formatter instance
	 * @param objectClass
	 *            the class of which objects may be formatted
	 */
	public void store(Format format, Class objectClass)
	{
		String name = "#class." + objectClass.getName();
		
		store(format, name);
	}
	
	
	
	/**
	 * Lookup a formatter by name first. If not found default to the most
	 * appropriate class formatter.
	 * 
	 * @param a
	 *            name which uniquely identifies a formatter
	 * @param objectClass
	 *            the class of which objects need to be formatted
	 * @return a formatter. Never null, a DummyFormatter at worst.
	 */
	public Format lookup(String name, Class objectClass)
	{
		Format format = (Format) this.formatters.objectForKey(name);
		
		if (format != null) {
			return (Format) format.clone();
		}
		
		return lookup(objectClass);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Lazily instantiate the default dummy formatter.
	 * 
	 * @return a universally usable formatter
	 */
	protected Format dummyFormatter()
	{
		String name = "#dummy";
		Format format = _lookup(name);
		
		if (format == null) {
			format = new DummyFormatter();
			
			store(format, name);
		}
		
		return format;
	}
	
	
	protected Format _lookup(String name)
	{
		return (Format) this.formatters.objectForKey(name);
	}
	
	
	
	// Key-value coding
	
	public Object valueForKey(String key)
	{
		Format format = _lookup(key);
		
		if (format != null) {
			return format;
		} else {
			return NSKeyValueCoding.Utility.handleQueryWithUnboundKey(this, key);
		}
	}
	
	
	public void takeValueForKey(Object object, String key)
	{
		if (object instanceof Format) {
			store((Format) object, key);
		} else {
			NSKeyValueCoding.Utility.handleTakeValueForUnboundKey(this, object, key);
		}
	}
}
