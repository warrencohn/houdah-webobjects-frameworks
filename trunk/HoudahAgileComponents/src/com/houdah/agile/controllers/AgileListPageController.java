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

package com.houdah.agile.controllers;

import com.houdah.agile.factories.DescriptorFactory;
import com.houdah.ruleengine.RuleContext;
import com.houdah.web.control.components.HCCListComponent;
import com.houdah.web.control.controllers.AbstractListPageController;
import com.houdah.web.control.controllers.DisplayGroupController;
import com.houdah.web.view.table.descriptors.TableColumnDescriptor;
import com.houdah.web.view.table.descriptors.TableDescriptor;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class AgileListPageController extends AbstractListPageController implements
		DisplayGroupController
{
	// Public class constants
	
	// Private class constants
	
	// Private class variables
	
	// private static TableColumnDescriptor detailActionDescriptor;
	
	// private static TableColumnDescriptor deleteActionDescriptor;
	
	// private static TableColumnDescriptor editActionDescriptor;
	
	
	// Private instance variables
	
	private RuleContext	ruleContext;
	
	
	private String		createActionLabel;
	
	
	private String		addActionLabel;
	
	
	private String		previousActionLabel;
	
	
	private String		nextActionLabel;
	
	
	
	
	// Constructor
	
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 * @param ruleContext
	 *            local rule context initialized with entityName and task
	 */
	public AgileListPageController(String entityName, String task, RuleContext ruleContext)
	{
		super(entityName, task);
		
		this.ruleContext = ruleContext;
		this.addActionLabel = null;
		this.previousActionLabel = null;
		this.nextActionLabel = null;
	}
	
	
	
	// Public instance methods
	
	public void willInitializePage()
	{
		super.willInitializePage();
		
		ruleContext().takeValueForKey(entity(), "entity");
	}
	
	
	
	// Page controller methods
	
	
	// Configuration methods
	
	public String controllerComponentName()
	{
		return HCCListComponent.class.getName();
	}
	
	
	
	// Protected instance methods
	
	protected boolean mayCreate()
	{
		if (super.mayCreate()) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			Boolean mayCreate = (Boolean) localContext.valueForKey("mayCreate");
			
			return mayCreate.booleanValue();
		}
		
		return false;
	}
	
	
	protected boolean mayAdd()
	{
		if (super.mayAdd()) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			Boolean mayAdd = (Boolean) localContext.valueForKey("mayAdd");
			
			return mayAdd.booleanValue();
		}
		
		return false;
	}
	
	
	protected String createActionLabel()
	{
		if (this.createActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.createActionLabel = (String) localContext.valueForKey("createActionLabel");
		}
		
		return this.createActionLabel;
	}
	
	
	protected String addActionLabel()
	{
		if (this.addActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.addActionLabel = (String) localContext.valueForKey("addActionLabel");
		}
		
		return this.addActionLabel;
	}
	
	
	protected String previousActionLabel()
	{
		if (this.previousActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.previousActionLabel = (String) localContext.valueForKey("previousActionLabel");
		}
		
		return this.previousActionLabel;
	}
	
	
	protected String nextActionLabel()
	{
		if (this.nextActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.nextActionLabel = (String) localContext.valueForKey("nextActionLabel");
		}
		
		return this.nextActionLabel;
	}
	
	
	protected RuleContext ruleContext()
	{
		return this.ruleContext;
	}
	
	
	protected RuleContext createNestedRuleContext()
	{
		return new RuleContext(ruleContext());
	}
	
	
	
	// Action methods
	
	
	// Initialization
	
	/**
	 * Creates the descriptor for the data table. Called during component
	 * initialization
	 * 
	 * @return an immutable descriptor
	 */
	protected TableDescriptor generateListTable()
	{
		EOEntity entity = entity();
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(this, "controller");
		
		String defaultSortColumn = (String) localContext.valueForKey("defaultSortColumn");
		Boolean defaultSortAscending = (Boolean) localContext.valueForKey("defaultSortAscending");
		if (defaultSortAscending != null) {
			setSortAscending(defaultSortAscending);
		}
				
		DescriptorFactory factory = DescriptorFactory.sharedFactory();
		NSArray columnArray = (NSArray) localContext.valueForKey("columns");
		int cCount = columnArray.count();
		NSMutableArray columns = new NSMutableArray(cCount);
		
		for (int c = 0; c < cCount; c++) {
			String columnKey = (String) columnArray.objectAtIndex(c);
			
			TableColumnDescriptor columnDescriptor = (TableColumnDescriptor) factory.descriptor(
					"column", entity, columnKey, localContext);
			
			columns.addObject(columnDescriptor);
			
			if (columnKey.equals(defaultSortColumn)) {
				setSortColumn(columnDescriptor);
			}
		}
		
		String minNumberOfRowsString = (String) localContext.valueForKey("minNumberOfRows");
		Integer minNumberOfRows = Integer.valueOf(minNumberOfRowsString);
		TableDescriptor tableDescriptor = new TableDescriptor(columns, minNumberOfRows);
		
		return tableDescriptor;
	}
	
	
	
	/**
	 * Picks the default sort column from the table descriptor. Called during
	 * component initialization
	 * 
	 * @return a column descriptor, or null
	 */
	protected TableColumnDescriptor defaultSortColumn(TableDescriptor tableDescriptor)
	{
		return super.defaultSortColumn(tableDescriptor);
	}
}