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

package com.houdah.web.view.form;

import com.houdah.web.view.components.View;
import com.houdah.web.view.form.descriptors.FieldTableDescriptor;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * High-level reusable components to create a table of form fields to be used
 * within a HTML form.
 * 
 * 
 */
public class HVCFieldTable extends View
{
	// Private class constants
	
	private static final long		serialVersionUID	= -7606269655658657843L;
	
	
	
	// Private instance variables
	
	// API
	
	/**
	 * This array of mutable dictionaries will be used to store the user's
	 * selection. API, Needs to be bound.<br/>
	 * 
	 * Each form item is liked to a entry in the dictionary by its key. <br/>
	 * 
	 * The controller may provide values containers in this dictionary in order
	 * to set default form values or to force the use of container subclasses.
	 */
	private NSArray					valueRows;
	
	
	
	/**
	 * The descriptor upon which to base the field table. API, Needs to be
	 * bound.
	 */
	private FieldTableDescriptor	fieldTableDescriptor;
	
	
	
	// Protected instance variables
	
	// Internal use
	
	/**
	 * Used internally for looping over rows
	 */
	protected int					currentRowIndex;
	
	
	
	/**
	 * Used internally for looping over row fields
	 */
	protected FormFieldDescriptor	currentField;
	
	
	
	/**
	 * Minimum number of rows to display
	 */
	protected Integer				minRowCount;
	
	
	
	/**
	 * Used internally to cache the actual number of rows displayed
	 */
	protected int					rowCount;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCFieldTable(WOContext context)
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
		
		this.valueRows = (NSArray) valueForBinding("valueRows");
		this.fieldTableDescriptor = (FieldTableDescriptor) valueForBinding("fieldTableDescriptor");
		
		this.currentRowIndex = 0;
		this.currentField = null;
		
		this.minRowCount = this.fieldTableDescriptor.minNumberOfRows();
		this.rowCount = this.valueRows.count();
		
		if (this.minRowCount == null) {
			this.minRowCount = new Integer(this.rowCount);
		} else if (this.rowCount < this.minRowCount.intValue()) {
			this.rowCount = this.minRowCount.intValue();
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.valueRows = null;
		this.fieldTableDescriptor = null;
		
		this.currentRowIndex = 0;
		this.currentField = null;
		
		this.minRowCount = null;
		this.rowCount = 0;
		
		super.reset();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#synchronizesVariablesWithBindings()
	 */
	public boolean synchronizesVariablesWithBindings()
	{
		return false;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#isStateless()
	 */
	public boolean isStateless()
	{
		return true;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#performParentAction(java.lang.String)
	 */
	public WOActionResults performParentAction(String actionName)
	{
		// Pushes the 'selectionIndex' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(new Integer(this.currentRowIndex), "selectionIndex");
		setValueForBinding(currentField(), "field");
		
		return super.performParentAction(actionName);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.generic.ViewComponent#performControllerAction(java.lang.String)
	 */
	public WOActionResults performControllerAction(String actionName)
	{
		// Pushes the 'selectionIndex' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(new Integer(this.currentRowIndex), "selectionIndex");
		setValueForBinding(currentField(), "field");
		
		return super.performControllerAction(actionName);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Accessor method: Retrieves the descriptor upon which to base the table
	 * set
	 * 
	 * @return a FieldTableDescriptor
	 */
	protected FieldTableDescriptor fieldTableDescriptor()
	{
		return this.fieldTableDescriptor;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the user's selection
	 * 
	 * @return an array of dictionaries of Value objects indexed by String keys
	 */
	protected NSArray valueRows()
	{
		return this.valueRows;
	}
	
	
	protected boolean isEmptyRow()
	{
		return this.currentRowIndex >= valueRows().count();
	}
	
	
	protected ValueContainer values()
	{
		ValueContainer values = (ValueContainer) (isEmptyRow() ? null
				: valueRows().objectAtIndex(this.currentRowIndex));
		
		return values;
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * 
	 * @return the row field iteration item
	 */
	protected FormFieldDescriptor currentField()
	{
		return this.currentField;
	}
	
	
	
	/**
	 * Accessor method: Sets the current row filed iterator
	 * 
	 * @param currentField
	 *            a FormFieldDescriptor
	 */
	protected void setCurrentField(FormFieldDescriptor currentField)
	{
		this.currentField = currentField;
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * 
	 * @return true if the current field has a label cell
	 */
	protected boolean hasLabel()
	{
		return (currentField().labelDescriptor() != null);
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * Called during the append-to-reponse phase to determine the CSS class to
	 * assign to the current row.
	 * 
	 * @return the CSS class name
	 */
	protected String rowClass()
	{
		String rowClass = (this.currentRowIndex % 2 == 0) ? "even" : "odd";
		
		if (isEmptyRow()) {
			rowClass += " emptyRow";
		}
		
		return rowClass;
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * Called during the append-to-reponse phase to determine the CSS class to
	 * assign to the current row.
	 * 
	 * @return the CSS class name
	 */
	protected String fieldClass()
	{
		return currentField().cssClass(values());
	}
}