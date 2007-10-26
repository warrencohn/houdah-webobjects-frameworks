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
import com.houdah.web.control.controllers.AbstractDetailPageController;
import com.houdah.web.control.controllers.DisplayGroupController;
import com.houdah.web.view.list.descriptors.ListDescriptor;
import com.houdah.web.view.list.descriptors.ListPropertyDescriptor;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class AgileDetailPageController extends AbstractDetailPageController implements
		DisplayGroupController
{
	// Public class constants
	
	// Private class constants
	
	// Private class variables
	
	// Private instance variables
	
	private RuleContext	ruleContext;
	
	
	private String		editActionLabel;
	
	
	private String		deleteActionLabel;
	
	
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
	public AgileDetailPageController(String entityName, String task, RuleContext ruleContext)
	{
		super(entityName, task);
		
		this.ruleContext = ruleContext;
		this.editActionLabel = null;
		this.deleteActionLabel = null;
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
	
	// Protected instance methods
	
	protected RuleContext ruleContext()
	{
		return this.ruleContext;
	}
	
	
	protected RuleContext createNestedRuleContext()
	{
		return new RuleContext(ruleContext());
	}
	
	
	protected boolean mayDelete()
	{
		if (super.mayDelete()) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			Boolean mayDelete = (Boolean) localContext.valueForKey("mayDelete");
			
			return mayDelete.booleanValue();
		}
		
		return false;
	}
	
	
	protected boolean mayEdit()
	{
		if (super.mayEdit()) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			Boolean mayCreate = (Boolean) localContext.valueForKey("mayEdit");
			
			return mayCreate.booleanValue();
		}
		
		return false;
	}
	
	
	protected String editActionLabel()
	{
		if (this.editActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.editActionLabel = (String) localContext.valueForKey("editActionLabel");
		}
		
		return this.editActionLabel;
	}
	
	
	protected String deleteActionLabel()
	{
		if (this.deleteActionLabel == null) {
			RuleContext localContext = createNestedRuleContext();
			
			localContext.takeValueForKey(this, "controller");
			
			this.deleteActionLabel = (String) localContext.valueForKey("deleteActionLabel");
		}
		
		return this.deleteActionLabel;
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
	
	
	
	// Action methods
	
	// Initialization
	
	/**
	 * Creates the descriptor for the list. Called during component
	 * initialization
	 * 
	 * @return an immutable descriptor
	 */
	protected ListDescriptor generateListDescriptor()
	{
		EOEntity entity = entity();
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(this, "controller");
		
		DescriptorFactory factory = DescriptorFactory.sharedFactory();
		NSArray propertyArray = (NSArray) localContext.valueForKey("properties");
		int pCount = propertyArray.count();
		NSMutableArray properties = new NSMutableArray(pCount);
		
		for (int p = 0; p < pCount; p++) {
			String propertyKey = (String) propertyArray.objectAtIndex(p);
			
			ListPropertyDescriptor propertyDescriptor = (ListPropertyDescriptor) factory
					.descriptor("property", entity, propertyKey, localContext);
			
			properties.addObject(propertyDescriptor);
		}
		
		String minNumberOfRowsString = (String) localContext.valueForKey("minNumberOfRows");
		Integer minNumberOfRows = Integer.valueOf(minNumberOfRowsString);
		ListDescriptor listDescriptor = new ListDescriptor(properties, minNumberOfRows);
		
		return listDescriptor;
	}
}