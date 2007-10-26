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

package com.houdah.web.view.form.descriptors;

import com.houdah.web.view.descriptors.ViewDescriptor;

import com.webobjects.foundation.NSArray;

public class FieldsetDescriptor extends ViewDescriptor
{
	// Private instance variables

	/**
	 * An of array of FieldsetLineDescriptor objects
	 */
	private NSArray rows;

	/**
	 * Minimum number of rows to show
	 */
	private Integer minNumberOfRows;

	/**
	 * Label to assign to the fieldset
	 */
	private String legend;

	/**
	 * A CSS class name
	 */
	private String cssClass;

	// Constructor

	/**
	 * Designated constructor.
	 * 
	 * @param properties
	 *            array of FieldsetRowDescriptor objects. Not null
	 * @param minNumberOfRows
	 *            minimum number of rows to display. Optional.
	 * @param legend
	 *            label to assign to the fieldset. Optional
	 * @param cssClass
	 *            css class name for the field set. Optional
	 */
	public FieldsetDescriptor(NSArray rows, Integer minNumberOfRows, String legend, String cssClass)
	{
		this.rows = rows;
		this.minNumberOfRows = minNumberOfRows;
		this.legend = legend;
		this.cssClass = cssClass;
	}

	// Public instance methods

	public NSArray rows()
	{
		return this.rows;
	}

	public Integer minNumberOfRows()
	{
		return this.minNumberOfRows;
	}

	public String legend()
	{
		return this.legend;
	}

	public String cssClass()
	{
		return this.cssClass;
	}
}
