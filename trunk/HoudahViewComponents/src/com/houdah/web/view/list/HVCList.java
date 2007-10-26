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

package com.houdah.web.view.list;

import com.houdah.appserver.components.Element;
import com.houdah.web.view.list.descriptors.ListDescriptor;
import com.houdah.web.view.list.descriptors.ListPropertyDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

/**
 * A reusable component for displaying a list of properties of a single object.
 * 

 */
public class HVCList extends Element
{
	// Private class constants
	
	private static final long	serialVersionUID	= -8874311609809222305L;
	
	
	
	// Public class constants
	
	// Private instance variables
	
	// API
	
	
	/**
	 * Object to display. API, Needs to be bound.
	 */
	private Object				displayedObject;
	
	
	
	/**
	 * A ListDescriptor object. API, Needs to be bound.
	 */
	private ListDescriptor		listDescriptor;
	
	
	
	// Internal use
	
	/**
	 * Used internally for looping over rows
	 */
	protected int				currentRowIndex;
	
	
	
	/**
	 * Used internally to cache the actual number of rows displayed
	 */
	protected int				rowCount;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCList(WOContext context)
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
		
		this.displayedObject = (Object) valueForBinding("displayedObject");
		this.listDescriptor = (ListDescriptor) valueForBinding("listDescriptor");
		
		this.currentRowIndex = 0;
		this.rowCount = this.listDescriptor.properties().count();
		
		if (this.rowCount < this.listDescriptor.minNumberOfRows().intValue()) {
			this.rowCount = this.listDescriptor.minNumberOfRows().intValue();
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.displayedObject = null;
		this.listDescriptor = null;
		
		this.currentRowIndex = 0;
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
	
	
	
	// Protected instance methods
	
	/**
	 * Accessor method: Retrieves the object being displayed
	 * 
	 * @return the object being displayed
	 */
	protected Object displayedObject()
	{
		return this.displayedObject;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the descriptor of the list
	 * 
	 * @return the descriptor of the list
	 */
	protected ListDescriptor listDescriptor()
	{
		return this.listDescriptor;
	}
	
	
	protected boolean isEmptyRow()
	{
		return this.currentRowIndex >= listDescriptor().properties().count();
	}
	
	
	
	/**
	 * Used internally. <br/>
	 * 
	 * @return the iteration item
	 */
	protected ListPropertyDescriptor currentProperty()
	{
		if (isEmptyRow()) {
			return null;
		} else {
			return (ListPropertyDescriptor) listDescriptor().properties()
					.objectAtIndex(this.currentRowIndex);
		}
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
		} else {
			ListPropertyDescriptor currentProperty = currentProperty();
			String cssClass = currentProperty.cssClass();
			
			if (cssClass != null) {
				rowClass += " ";
				rowClass += cssClass;
			}
			
		}
		
		return rowClass;
	}
	
	
	
	// Action methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#performParentAction(java.lang.String)
	 */
	public WOActionResults performParentAction(String actionName)
	{
		// Pushes the 'property' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(currentProperty(), "property");
		
		return super.performParentAction(actionName);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.generic.ViewComponent#performControllerAction(java.lang.String)
	 */
	public WOActionResults performControllerAction(String actionName)
	{
		// Pushes the 'property' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(currentProperty(), "property");
		
		return super.performControllerAction(actionName);
	}
}