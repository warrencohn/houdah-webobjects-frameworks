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

package com.houdah.web.view.list.descriptors;

import com.houdah.web.view.descriptors.ViewDescriptor;

import com.webobjects.foundation.NSArray;

public class ListDescriptor extends ViewDescriptor
{
	// Protected instance variables
	
	/**
	 * Array of the list's property descriptors
	 * 
	 * @TypeInfo ListRowDescriptor
	 */
	protected NSArray	properties;
	
	
	protected Integer	minNumberOfRows;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 * @param properties
	 *            array of ListRowDescriptor objects. Not null
	 * @param minNumberOfRows
	 *            minimum number of rows to display. Optional.
	 */
	public ListDescriptor(NSArray properties, Integer minNumberOfRows)
	{
		this.properties = properties;
		this.minNumberOfRows = minNumberOfRows;
		
		if ((this.minNumberOfRows == null)
				|| (this.minNumberOfRows.intValue() < this.properties.count())) {
			this.minNumberOfRows = new Integer(this.properties.count());
		}
	}
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param rows
	 *            array of ListRowDescriptor objects. Not null
	 */
	public ListDescriptor(NSArray properties)
	{
		this(properties, null);
	}
	
	
	
	// Public instance methods
	
	public NSArray properties()
	{
		return this.properties;
	}
	
	
	public Integer minNumberOfRows()
	{
		return this.minNumberOfRows;
	}
}