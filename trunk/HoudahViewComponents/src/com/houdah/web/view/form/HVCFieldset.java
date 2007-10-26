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
import com.houdah.web.view.form.descriptors.FieldsetDescriptor;
import com.houdah.web.view.form.descriptors.FieldsetEmptyRowDescriptor;
import com.houdah.web.view.form.descriptors.FieldsetRowDescriptor;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

/**
 * High-level reusable components to create a field set to be used within a HTML
 * form.
 * 
 * 
 */
public class HVCFieldset extends View
{
	// Private class constants
	
	private static final long					serialVersionUID	= 3290900688178675534L;
	
	
	private static final FieldsetRowDescriptor	EMPTY_ROW			= FieldsetEmptyRowDescriptor
																			.sharedInstance();
	
	
	
	// Private instance variables
	
	// API
	
	/**
	 * The dictionary that will be used to store the user's selection. API,
	 * Needs to be bound.<br/>
	 * 
	 * Each form item is liked to a entry in the dictionary by its key. <br/>
	 * 
	 * The controller may provide values containers in this dictionary in order
	 * to set default form values or to force the use of container subclasses.
	 */
	private ValueContainer						values;
	
	
	
	/**
	 * The descriptor upon which to base the field set. API, Needs to be bound.
	 */
	private FieldsetDescriptor					fieldsetDescriptor;
	
	
	
	// Protected instance variables
	
	// Internal use
	
	/**
	 * Used internally for looping over rows
	 */
	protected int								currentRowIndex;
	
	
	
	/**
	 * Used internally for looping over row fields
	 */
	protected FormFieldDescriptor				currentField;
	
	
	
	/**
	 * Used internally to cache the actual number of rows displayed
	 */
	protected int								rowCount;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCFieldset(WOContext context)
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
		
		this.values = (ValueContainer) valueForBinding("values");
		this.fieldsetDescriptor = (FieldsetDescriptor) valueForBinding("fieldsetDescriptor");
		
		this.currentRowIndex = 0;
		this.currentField = null;
		this.rowCount = this.fieldsetDescriptor.rows().count();
		
		if (this.fieldsetDescriptor.minNumberOfRows() != null) {
			if (this.rowCount < this.fieldsetDescriptor.minNumberOfRows()
					.intValue()) {
				this.rowCount = this.fieldsetDescriptor.minNumberOfRows()
						.intValue();
			}
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.values = null;
		this.fieldsetDescriptor = null;
		
		this.currentRowIndex = 0;
		this.currentField = null;
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
		setValueForBinding(currentField(), "field");
		
		return super.performControllerAction(actionName);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Accessor method: Retrieves the user's selection
	 * 
	 * @return a dictionary of Value objects indexed by String keys
	 */
	protected ValueContainer values()
	{
		return this.values;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the descriptor upon which to base the field
	 * set
	 * 
	 * @return a FieldsetDescriptor
	 */
	protected FieldsetDescriptor fieldsetDescriptor()
	{
		return this.fieldsetDescriptor;
	}
	
	
	
	/**
	 * Accessor method: Determines if the fieldset has a legend label
	 * 
	 * @return true if the display label is not null
	 */
	protected boolean hasLegend()
	{
		return (this.fieldsetDescriptor.legend() != null);
	}
	
	
	
	/**
	 * Accessor method: Retrieves the CSS class to assign to the fieldset
	 * 
	 * @return a comma separated list of CSS class names
	 */
	protected String cssClass()
	{
		String cssClass = fieldsetDescriptor().cssClass();
		
		if (cssClass == null) {
			cssClass = "";
		}
		
		cssClass += "bvcFieldset";
		
		return cssClass;
	}
	
	
	protected boolean isEmptyRow()
	{
		return this.currentRowIndex >= fieldsetDescriptor().rows().count();
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * 
	 * @return the row iteration item
	 */
	protected FieldsetRowDescriptor currentRow()
	{
		return (FieldsetRowDescriptor) (isEmptyRow() ? HVCFieldset.EMPTY_ROW
				: fieldsetDescriptor().rows().objectAtIndex(
						this.currentRowIndex));
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
