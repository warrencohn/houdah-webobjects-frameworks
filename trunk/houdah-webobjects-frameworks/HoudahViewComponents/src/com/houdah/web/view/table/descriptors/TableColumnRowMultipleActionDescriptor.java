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

package com.houdah.web.view.table.descriptors;

import com.houdah.web.view.constants.Alignment;
import com.houdah.web.view.table.cells.HVCTableColumnRowMultipleActionCell;

import com.webobjects.foundation.NSArray;

public class TableColumnRowMultipleActionDescriptor extends
		TableColumnRowDescriptor
{
	// Private instance variables
	
	private NSArray	actions;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param actions
	 *            array of action desriptors
	 * @param alignment
	 *            alignment of data within the cell
	 * @param cssClass
	 *            CSS class to use for data cells
	 */
	public TableColumnRowMultipleActionDescriptor(NSArray actions,
			Alignment alignment, String cssClass)
	{
		super(alignment, cssClass);
		
		this.actions = actions.immutableClone();
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param actions
	 *            array of action desriptors
	 * @param cssClass
	 *            CSS class to use for data cells
	 */
	public TableColumnRowMultipleActionDescriptor(NSArray actions,
			String cssClass)
	{
		this(actions, Alignment.RIGHT, cssClass);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * @return Returns the actions array.
	 */
	public final NSArray actions()
	{
		return this.actions;
	}
	
	
	protected Class cellClass()
	{
		return HVCTableColumnRowMultipleActionCell.class;
	}
}
