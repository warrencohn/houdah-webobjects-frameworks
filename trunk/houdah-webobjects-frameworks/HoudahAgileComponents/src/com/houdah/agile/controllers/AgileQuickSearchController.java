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
import com.houdah.eoaccess.utilities.ModelUtilities;
import com.houdah.ruleengine.RuleContext;
import com.houdah.web.control.controllers.AbstractQuickSearchController;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.descriptors.FieldsetDescriptor;
import com.houdah.web.view.form.descriptors.FieldsetRowDescriptor;
import com.houdah.web.view.form.descriptors.FormActionFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOProperty;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class AgileQuickSearchController extends AbstractQuickSearchController
{
	// Public class constants
	
	// Private class constants
	
	// Private class variables
	
	// Private instance variables
	
	private RuleContext	ruleContext;
	
	
	
	
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
	public AgileQuickSearchController(String entityName, String task, RuleContext ruleContext)
	{
		super(entityName, task);
		
		this.ruleContext = ruleContext;
	}
	
	
	
	// Public instance methods
	
	
	// Protected instance methods
	
	/**
	 * Produces a fetch specification to provide possible values for a
	 * ChoiceValue matching a given descriptor.
	 * 
	 * The fetch specification must be fully bound.
	 * 
	 * @return a fetch specification for the appropriate descriptor, null if not
	 *         applicable
	 */
	protected EOFetchSpecification valueFetchSpecification(FormValueFieldDescriptor cellDescriptor,
			EOEntity destinationEntity)
	{
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(destinationEntity, "destinationEntity");
		localContext.takeValueForKey(cellDescriptor.key(), "key");
		
		String fetchSpecificationName = (String) localContext
				.valueForKey("valueFetchSpecificationName");
		
		EOFetchSpecification fetchSpecification = null;
		
		if (fetchSpecificationName != null) {
			fetchSpecification = destinationEntity.fetchSpecificationNamed(fetchSpecificationName);
		}
		
		if (fetchSpecification == null) {
			fetchSpecification = new EOFetchSpecification(destinationEntity.name(), null, null);
		}
		
		return fetchSpecification;
	}
	
	
	
	/**
	 * Produces a possible values for a ChoiceValue matching a given descriptor.
	 * 
	 * Called only if the descriptor does not point to an entity. In that event,
	 * valueFetchSpecification(FormValueFieldDescriptor, EOEntity) would be
	 * called.
	 * 
	 * @return an array of values
	 */
	protected NSArray values(FormValueFieldDescriptor cellDescriptor)
	{
		RuleContext localContext = createNestedRuleContext();
		
		EOProperty property = ModelUtilities.propertyAtPath(entity(), cellDescriptor.key());
		
		if (property != null) {
			if (property instanceof EORelationship) {
				localContext.takeValueForKey("r", "propertyType");
				localContext.takeValueForKey(property, "relationship");
			} else {
				localContext.takeValueForKey("a", "propertyType");
				localContext.takeValueForKey(property, "attribute");
			}
		}
		
		localContext.takeValueForKey(cellDescriptor.key(), "key");
		
		String valueType = (String) localContext.valueForKey("valueType");
		NSArray values = (NSArray) localContext.valueForKey("values");
		
		int vCount = values.count();
		NSMutableArray array = new NSMutableArray(vCount);
		DescriptorFactory descriptorFactory = DescriptorFactory.sharedFactory();
		
		for (int v = 0; v < vCount; v++) {
			Object rawValue = values.objectAtIndex(v);
			Object value = descriptorFactory.valueForType(rawValue, valueType);
			
			array.addObject(value);
		}
		
		return array;
	}
	
	
	protected RuleContext ruleContext()
	{
		return this.ruleContext;
	}
	
	
	protected RuleContext createNestedRuleContext()
	{
		return new RuleContext(ruleContext());
	}
	
	
	
	// Initialization
	
	/**
	 * Creates the descriptor for the quickSearch form.
	 * 
	 * @return an immutable descriptor
	 */
	protected FieldsetDescriptor generateQuickSearchFieldsets()
	{
		EOEntity entity = entity();
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(this, "controller");
		localContext.takeValueForKey(entity, "entity");
		localContext.takeValueForKey("quickSearch", "fieldset");
		
		DescriptorFactory factory = DescriptorFactory.sharedFactory();
		
		NSArray rowArray = (NSArray) localContext.valueForKey("fields");
		int rCount = rowArray.count();
		NSMutableArray rows = new NSMutableArray(rCount);
		
		if (rCount == 0) {
			rows.addObject(new FieldsetRowDescriptor(new NSArray(quickSearchActionField())));
		} else {
			for (int r = 0; r < rCount; r++) {
				NSArray fieldArray = (NSArray) rowArray.objectAtIndex(r);
				int fCount = fieldArray.count();
				NSMutableArray fields = new NSMutableArray(fCount);
				
				for (int f = 0; f < fCount; f++) {
					String fieldKey = (String) fieldArray.objectAtIndex(f);
					
					FormValueFieldDescriptor fieldDescriptor = (FormValueFieldDescriptor) factory
							.descriptor("field", entity, fieldKey, localContext);
					
					fields.addObject(fieldDescriptor);
				}
				
				if (r == (rCount - 1)) {
					fields.addObject(quickSearchActionField());
				}
				
				rows.addObject(new FieldsetRowDescriptor(fields));
			}
		}
		
		String minNumberOfRowsString = (String) localContext.valueForKey("minNumberOfRows");
		Integer minNumberOfRows = Integer.valueOf(minNumberOfRowsString);
		String legend = (String) localContext.valueForKey("legend");
		String cssClass = (String) localContext.valueForKey("cssClass");
		
		return new FieldsetDescriptor(rows, minNumberOfRows, legend, cssClass);
	}
	
	
	protected FormActionFieldDescriptor quickSearchActionField()
	{
		FormActionFieldDescriptor quickSearchActionField = super.quickSearchActionField();
		Action searchAction = quickSearchActionField.action();
		String searchActionName = searchAction.actionName();
		String searchActionTitle = searchAction.title();
		String searchActionCssClass = searchAction.cssClass();
		
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(this, "controller");
		localContext.takeValueForKey(entity(), "entity");
		
		String newSearchActionLabel = (String) localContext.valueForKey("quickSearchActionLabel");
		Action newSearchAction = new Action(searchActionName, newSearchActionLabel,
				searchActionTitle, searchActionCssClass);
		
		return new FormActionFieldDescriptor(newSearchAction);
	}
	
	
	
	/**
	 * Produces a template fetch specification for searches.
	 * 
	 * The fetch specification may still have dangling bindings.
	 * 
	 * @return a fetch specification for the current entity
	 */
	protected EOFetchSpecification fetchSpecificationTemplate()
	{
		RuleContext localContext = createNestedRuleContext();
		
		localContext.takeValueForKey(this, "controller");
		localContext.takeValueForKey(entity(), "entity");
		
		String fetchSpecificationName = (String) localContext.valueForKey("fetchSpecificationName");
		EOFetchSpecification fetchSpecification = entity().fetchSpecificationNamed(
				fetchSpecificationName);
		
		return fetchSpecification;
	}
	
	
	// Action methods
	
	// Protected class methods
	
	// Private class emthods
}