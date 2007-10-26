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

package com.houdah.web.view.table;

import com.houdah.appserver.components.Element;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.table.descriptors.TableColumnDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnHeaderDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnRowDescriptor;
import com.houdah.web.view.table.descriptors.TableDescriptor;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableSet;

/**
 * A reusable component for displaying a list of objects.
 * 

 */
public class HVCTable extends Element
{
	// Private class constants
	
	private static final long		serialVersionUID	= -1070936760297460343L;
	
	
	
	// Public class constants
	
	
	/**
	 * Name of the action to call to set sort order
	 */
	public static final String		SORT_ACTION			= "sort";
	
	
	
	// Private instance variables
	
	// API
	
	/**
	 * Array of objects to display. API, Needs to be bound.
	 */
	private NSArray					displayedObjects;
	
	
	
	/**
	 * A TableDescriptor objects. API, Needs to be bound.
	 */
	private TableDescriptor			tableDescriptor;
	
	
	
	/**
	 * The column according to which the data is to be sorted. The sort
	 * operation is up to the caller. API, Optional.
	 */
	private TableColumnDescriptor	sortColumn;
	
	
	
	/**
	 * The sort order according to which the data is/should be sorted. API,
	 * required if sortColumn is set.
	 */
	private Boolean					sortAscending;
	
	
	
	/**
	 * The controller action responsible of sorting. API, required one or more
	 * columns allow sorting
	 */
	private String					sortAction;
	
	
	
	// Internal use
	
	/**
	 * Used internally for looping of the array of column descriptors.
	 */
	protected TableColumnDescriptor	currentColumnDescriptor;
	
	
	
	/**
	 * Used internally for looping over data items in the display group
	 */
	protected int					currentRowIndex;
	
	
	
	/**
	 * Minimum number of rows to display
	 */
	protected Integer				minRowCount;
	
	
	
	/**
	 * Used internally to cache the actual number of rows displayed
	 */
	protected int					rowCount;
	
	
	
	/**
	 * Used internally to temporarily store the object selections
	 */
	protected NSMutableSet			selectedObjects;
	
	
	
	/**
	 * Used internally for looping over actions
	 */
	protected Action				currentTableAction;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCTable(WOContext context)
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
		
		this.displayedObjects = (NSArray) valueForBinding("displayedObjects");
		this.tableDescriptor = (TableDescriptor) valueForBinding("tableDescriptor");
		this.sortColumn = (TableColumnDescriptor) valueForBinding("sortColumn");
		this.sortAscending = (Boolean) valueForBinding("sortAscending");
		this.sortAction = (String) valueForBinding("sortAction");
		
		this.currentColumnDescriptor = null;
		this.currentRowIndex = 0;
		this.minRowCount = this.tableDescriptor.minNumberOfRows();
		this.rowCount = displayedObjects.count();
		
		if (this.minRowCount == null) {
			this.minRowCount = new Integer(this.rowCount);
		} else if (this.rowCount < this.minRowCount.intValue()) {
			this.rowCount = this.minRowCount.intValue();
		}
		
		this.selectedObjects = (NSMutableSet) valueForBinding("selectedObjects");
		
		this.currentTableAction = null;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.displayedObjects = null;
		this.tableDescriptor = null;
		this.sortColumn = null;
		this.sortAscending = null;
		this.sortAction = null;
		
		this.currentColumnDescriptor = null;
		this.currentRowIndex = 0;
		this.minRowCount = null;
		this.rowCount = 0;
		this.selectedObjects = null;
		
		this.currentTableAction = null;
		
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
	 * Accessor method: Retrieves the list of objects to display
	 * 
	 * @return the list of data objects being displayed
	 */
	protected NSArray displayedObjects()
	{
		return this.displayedObjects;
	}
	
	
	
	/**
	 * Accessor method: Retrieves table descriptor
	 * 
	 * @return a TableDescriptor object
	 */
	protected TableDescriptor tableDescriptor()
	{
		return this.tableDescriptor;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the column according to which data is sorted
	 * 
	 * @return an instance of TableColumnDescriptor or null
	 */
	protected TableColumnDescriptor sortColumn()
	{
		return this.sortColumn;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the sort ordering direction
	 * 
	 * @return true if ascending
	 */
	protected Boolean sortAscending()
	{
		return this.sortAscending;
	}
	
	
	
	/**
	 * Accessor method: Retrieves the name of the controller action
	 * 
	 * @return the name of controller action responsible of sorting
	 */
	protected String sortAction()
	{
		return this.sortAction;
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return the number of lines in the table
	 */
	protected int rowCount()
	{
		return this.rowCount;
	}
	
	
	protected boolean isEmptyRow()
	{
		return (this.currentRowIndex >= displayedObjects().count());
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return the current data item (in row loop)
	 */
	protected Object currentObject()
	{
		if (isEmptyRow()) {
			return null;
		} else {
			return displayedObjects().objectAtIndex(this.currentRowIndex);
		}
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return true if there are table actions defined
	 */
	protected boolean hasActions()
	{
		return this.tableDescriptor.tableActions() != null;
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return true if the table row is selected
	 */
	protected boolean isRowSelected()
	{
		Object currentObject = currentObject();
		
		return (currentObject != null) && (this.selectedObjects != null)
				&& this.selectedObjects.containsObject(currentObject);
	}
	
	
	protected void setIsRowSelected(boolean isRowSelected)
	{
		Object currentObject = currentObject();
		
		if ((currentObject != null) && (this.selectedObjects != null)) {
			if (isRowSelected) {
				this.selectedObjects.addObject(currentObject);
			} else {
				this.selectedObjects.removeObject(currentObject);
			}
		}
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return the CSS class name
	 */
	protected String headerClass()
	{
		TableColumnHeaderDescriptor headerDescriptor = this.currentColumnDescriptor
				.headerDescriptor();
		String headerClass = headerDescriptor.cssClass();
		
		headerClass = (headerClass != null) ? headerClass : "";
		
		if (this.currentColumnDescriptor == sortColumn()) {
			Boolean sortAscending = sortAscending();
			
			if (Boolean.TRUE.equals(sortAscending)) {
				headerClass += " ascending";
			} else if (sortAscending != null) {
				headerClass += " descending";
			}
		}
		
		return headerClass;
	}
	
	
	
	/**
	 * Used internally.
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
	 * Used internally.
	 * 
	 * @return the CSS class name
	 */
	protected String dataClass()
	{
		TableColumnRowDescriptor rowDescriptor = this.currentColumnDescriptor
				.rowDescriptor();
		String columnClass = rowDescriptor.cssClass();
		
		return (columnClass != null) ? columnClass : "";
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return the element name
	 */
	protected String headerElementName()
	{
		String headerElementName = currentColumnDescriptor.headerDescriptor()
				.elementName();
		
		return headerElementName;
	}
	
	
	
	/**
	 * Used internally.
	 * 
	 * @return the element name
	 */
	protected String rowElementName()
	{
		String rowElementName = currentColumnDescriptor.rowDescriptor()
				.elementName();
		
		return rowElementName;
	}
	
	
	
	// Action methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#performParentAction(java.lang.String)
	 */
	public WOActionResults performParentAction(String actionName)
	{
		// Pushes the 'selection' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(currentObject(), "selection");
		
		return super.performParentAction(actionName);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.generic.ViewComponent#performControllerAction(java.lang.String)
	 */
	public WOActionResults performControllerAction(String actionName)
	{
		// Pushes the 'selection' binding setting it to the current selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(currentObject(), "selection");
		
		return super.performControllerAction(actionName);
	}
	
	
	
	/**
	 * Action method for sorting<br/>
	 * 
	 * Forwards the call to the designated controller action
	 * 
	 * @return what's returned the controller action
	 */
	protected WOActionResults sort()
	{
		if (this.currentColumnDescriptor.equals(sortColumn())) {
			this.sortAscending = (this.sortAscending == Boolean.TRUE) ? Boolean.FALSE
					: Boolean.TRUE;
		} else {
			this.sortColumn = this.currentColumnDescriptor;
			this.sortAscending = Boolean.TRUE;
		}
		
		setValueForBinding(sortColumn(), "sortColumn");
		setValueForBinding(sortAscending(), "sortAscending");
		
		return performControllerAction(sortAction());
	}
	
	
	protected WOActionResults performTableAction()
	{
		WOActionResults results = this
				.performControllerAction(this.currentTableAction.actionName());
		
		
		// We don't want the selection to keep lingering around.
		// It would be impredicable on batched tables: kept on submit, forgotten
		// on batch navigation.
		this.selectedObjects = (NSMutableSet) valueForBinding("selectedObjects");
		this.selectedObjects.removeAllObjects();
		
		return results;
	}
}