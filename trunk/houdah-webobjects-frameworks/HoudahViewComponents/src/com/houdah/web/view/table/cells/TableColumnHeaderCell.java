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

package com.houdah.web.view.table.cells;

import com.houdah.web.view.components.Cell;
import com.houdah.web.view.table.HVCTable;
import com.houdah.web.view.table.descriptors.TableColumnHeaderDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

/**
 * Parent class to all table header cells.
 * 

 */
public abstract class TableColumnHeaderCell extends Cell
{
	// Private instance variables
	
	/**
	 * Descriptor upon which to base the cell
	 */
	private TableColumnHeaderDescriptor	cellDescriptor;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public TableColumnHeaderCell(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	public void awake()
	{
		super.awake();
		
		this.cellDescriptor = (TableColumnHeaderDescriptor) valueForBinding("cellDescriptor");
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.cellDescriptor = null;
		
		super.reset();
	}
	
	
	
	// Protected instance methods
	
	protected TableColumnHeaderDescriptor cellDescriptor()
	{
		return this.cellDescriptor;
	}
	
	
	
	/**
	 * Action method for sorting. <br/>
	 * 
	 * The sorting is performed the controller.
	 * 
	 * @return the return value of the controller action
	 */
	protected WOActionResults sort()
	{
		return performParentAction(HVCTable.SORT_ACTION);
	}
}
