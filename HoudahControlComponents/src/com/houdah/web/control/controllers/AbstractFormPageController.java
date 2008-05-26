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

package com.houdah.web.control.controllers;

import java.text.Format;

import com.houdah.eoaccess.utilities.ModelUtilities;
import com.houdah.eocontrol.utilities.ControlUtilities;
import com.houdah.eocontrol.utilities.MoreThanOneException;
import com.houdah.eocontrol.utilities.ObjectNotAvailableException;
import com.houdah.foundation.ForwardException;
import com.houdah.web.control.application.Application;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.control.descriptors.IdentifyDescriptor;
import com.houdah.web.control.support.IdentifyException;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormTextfieldActionFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;
import com.houdah.web.view.form.values.ChoiceValue;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOProperty;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;

public abstract class AbstractFormPageController extends AbstractPageController
{
	// Protected class constants
	
	protected static final String	VALUE_CHANGED_NOTIFICATION	= "valueChangedNotification";
	
	
	protected static final String	DESCRIPTOR_KEY				= "descriptor";
	
	
	
	// Protected instance variables
	
	protected ValueContainer		values						= null;
	
	
	
	// Private instance variables
	private FormFieldDescriptor		currentField				= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractFormPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	public WOActionResults commitSelection(Object object, Object delegateContext)
	{
		Value value = (Value) delegateContext;
		
		if (object instanceof EOEnterpriseObject) {
			object = ControlUtilities.localInstanceOfObject(activeEditingContext(),
					(EOEnterpriseObject) object);
		}
		
		value.setValue(object);
		
		return page();
	}
	
	
	
	/**
	 * Get the editing context used for current operations.<br/>
	 * 
	 * Subclassed using child or peer editing contexts mut override this method
	 * to always return the currently used editing context.
	 * 
	 * @return the currently used editing context
	 */
	public EOEditingContext activeEditingContext()
	{
		return editingContext();
	}
	
	
	
	// Delegate methods
	
	public Boolean finishInitialization(FormValueFieldDescriptor cellDescriptor, Value value)
	{
		if (value instanceof ChoiceValue) {
			ChoiceValue choiceValue = (ChoiceValue) value;
			
			if (choiceValue.valueList() == null) {
				NSArray valueArray = null;
				EOProperty property = ModelUtilities.propertyAtPath(entity(), cellDescriptor.key());
				
				if (property instanceof EORelationship) {
					EORelationship relationship = (EORelationship) property;
					EOEntity destinationEntity = relationship.destinationEntity();
					EOFetchSpecification fetchSpecification = valueFetchSpecification(
							cellDescriptor, destinationEntity);
					
					if (fetchSpecification != null) {
						valueArray = activeEditingContext().objectsWithFetchSpecification(
								fetchSpecification);
					}
					
					
					// Add NSKeyValueCoding.NullValue to handle optional to-one
					// relationships
					if ((!relationship.isMandatory())
							&& (!valueArray.containsObject(NSKeyValueCoding.NullValue))) {
						valueArray = new NSArray(NSKeyValueCoding.NullValue)
								.arrayByAddingObjectsFromArray(valueArray);
					}
				} else {
					valueArray = values(cellDescriptor);
				}
				
				if (valueArray != null) {
					choiceValue.setValueList(valueArray);
				} else {
					choiceValue.setValueList(new NSArray());
				}
			}
		}
		
		return Boolean.TRUE;
	}
	
	
	public Object willParse(FormValueFieldDescriptor cellDescriptor, Object rawValue)
	{
		if (cellDescriptor instanceof IdentifyDescriptor) {
			IdentifyDescriptor identifyDescriptor = (IdentifyDescriptor) cellDescriptor;
			String entityName = identifyDescriptor.entityName();
			String stringValue = (String) rawValue;
			
			if ((stringValue == null) || (stringValue.length() == 0)) {
				return null;
			}
			
			Format formatter = Application.sharedInstance().entityIdentifier(entityName);
			
			try {
				NSDictionary valueDictionary = (NSDictionary) formatter.parseObject(stringValue);
				
				return ControlUtilities.objectMatchingValues(activeEditingContext(), entityName,
						valueDictionary);
			} catch (ObjectNotAvailableException onae) {
				throw new IdentifyException(IdentifyException.IDENTIFY_NO_MATCH, cellDescriptor.key(), entityName, onae);
			} catch (MoreThanOneException mtoe) {
				throw new IdentifyException(IdentifyException.IDENTIFY_AMBIGUOUS_MATCH, cellDescriptor.key(), entityName, mtoe);
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception e) {
				throw new ForwardException(e);
			}
		}
		
		return rawValue;
	}
	
	
	
	// Protected instance methods
	
	protected ValueContainer values()
	{
		return this.values;
	}
	
	
	protected FormFieldDescriptor currentField()
	{
		return this.currentField;
	}
	
	
	protected void setCurrentField(FormFieldDescriptor currentField)
	{
		this.currentField = currentField;
	}
	
	
	
	/**
	 * Produces a fetch specification to provide possible values for a
	 * ChoiceValue matching a given descriptor.
	 * 
	 * The fetch specification must be fully bound.
	 * 
	 * @return a fetch specification for the appropriate descriptor, null if not
	 *         applicable
	 */
	protected abstract EOFetchSpecification valueFetchSpecification(
			FormValueFieldDescriptor cellDescriptor, EOEntity destinationEntity);
	
	
	
	/**
	 * Produces a possible values for a ChoiceValue matching a given descriptor.
	 * 
	 * Called only if the descriptor does not point to an entity. In that event,
	 * valueFetchSpecification(FormValueFieldDescriptor, EOEntity) would be
	 * called.
	 * 
	 * @return an array of values
	 */
	protected abstract NSArray values(FormValueFieldDescriptor cellDescriptor);
	
	
	
	// Action methods
	
	protected WOActionResults quickSearchAction()
	{
		FormTextfieldActionFieldDescriptor fieldDescriptor = (FormTextfieldActionFieldDescriptor) currentField();
		
		if (fieldDescriptor != null) {
			String key = fieldDescriptor.key();
			EORelationship relationship = entity().anyRelationshipNamed(key);
			EOEntity destinationEntity = relationship.destinationEntity();
			Value value = values().value(fieldDescriptor);
			
			ControllerPage nextPage = session().pageWithEntityAndTask(destinationEntity.name(),
					Application.QUICK_SEARCH_TASK, page().context());
			AbstractQuickSearchController controller = (AbstractQuickSearchController) nextPage
					.controller();
			
			controller.setDelegate(this);
			controller.setDelegateContext(value);
			
			return nextPage;
		}
		
		return context().page();
	}
	
}
