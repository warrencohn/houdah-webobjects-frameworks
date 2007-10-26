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

package com.houdah.eovalidation.access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.houdah.eoaccess.utilities.ModelUtilities;
import com.houdah.eovalidation.control.ValidatingClassDescription;
import com.houdah.eovalidation.control.ValidatingEditingContext;
import com.houdah.eovalidation.control.ValidatingRecord;
import com.houdah.eovalidation.control.ValidationContext;
import com.houdah.foundation.ForwardException;
import com.houdah.ruleengine.RuleContext;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOEntityClassDescription;
import com.webobjects.eoaccess.EOProperty;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSValidation;
import com.webobjects.foundation.NSValidation.ValidationException;

public class ValidatingEntityClassDescription extends EOEntityClassDescription implements
		ValidatingClassDescription
{
	// Public class constants
	
	public static final String		ATTRIBUTE_KEY		= "attribute";
	
	
	public static final String		ENTITY_KEY			= "entity";
	
	
	public static final String		ENTITY_NAME_KEY		= ValidatingRecord.ENTITY_NAME_KEY;
	
	
	public static final String		KEY_KEY				= ValidatingRecord.KEY_KEY;
	
	
	public static final String		OBJECT_KEY			= ValidatingRecord.OBJECT_KEY;
	
	
	public static final String		RELATIONSHIP_KEY	= "relationship";
	
	
	public static final String		VALUE_KEY			= "value";
	
	
	
	// Protected class constants
	
	protected static final String	ADDITIONAL_KEYS_KEY	= "additionalKeys";
	
	
	protected static final String	METHOD_KEY			= "method";
	
	
	protected static final String	PROPERTY_TYPE_KEY	= "propertyType";
	
	
	protected static final String	RULE_NAME_KEY		= "ruleName";
	
	
	
	// Private class constants
	
	private static final long		serialVersionUID	= 2241792711929794619L;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param entity
	 *            the entity to attach to
	 */
	public ValidatingEntityClassDescription(EOEntity entity)
	{
		super(entity);
	}
	
	
	
	// Public instance methods
	
	public void validateAdditionalKeys(ValidatingRecord record) throws ValidationException
	{
		NSMutableArray validationExceptions = new NSMutableArray();
		ValidatingEditingContext editingContext = record.validatingEditingContext();
		RuleContext validationContext = editingContext.validationContext();
		NSArray additionalKeys = (NSArray) validationContext
				.valueForKey(ValidatingEntityClassDescription.ADDITIONAL_KEYS_KEY);
		int aCount = additionalKeys.count();
		
		for (int a = 0; a < aCount; a++) {
			
			String key = (String) additionalKeys.objectAtIndex(a);
			Object value = null; // Do not fetch values for additional keys
			
			try {
				Object validatedValue = record.validateValueForKey(value, key);
				
				if (validatedValue != value) {
					record.takeStoredValueForKey(validatedValue, key);
				}
			} catch (NSValidation.ValidationException ve) {
				validationExceptions.addObject(ve);
			}
		}
		
		int vCount = validationExceptions.count();
		
		if (vCount == 1) {
			throw (NSValidation.ValidationException) validationExceptions.objectAtIndex(0);
		} else if (vCount > 1) {
			throw NSValidation.ValidationException
					.aggregateExceptionWithExceptions(validationExceptions);
		}
	}
	
	
	public Object validateValueForKey(ValidatingRecord record, Object value, String key)
			throws ValidationException
	{
		Object validatedValue = value;
		
		ValidatingEditingContext editingContext = record.validatingEditingContext();
		RuleContext validationContext = editingContext.validationContext();
		ValidationContext localContext = new ValidationContext(validationContext);
		
		
		// Feed the rule engine
		localContext.takeValueForKey(record, ValidatingEntityClassDescription.OBJECT_KEY);
		localContext.takeValueForKey(record.entityName(),
				ValidatingEntityClassDescription.ENTITY_NAME_KEY);
		localContext.takeValueForKey(entity(), ValidatingEntityClassDescription.ENTITY_KEY);
		localContext.takeValueForKey(value, ValidatingEntityClassDescription.VALUE_KEY);
		localContext.takeValueForKey(key, ValidatingEntityClassDescription.KEY_KEY);
		
		EOProperty property = ModelUtilities.propertyAtPath(entity(), key);
		
		if (property != null) {
			if (property instanceof EORelationship) {
				localContext.takeValueForKey("r",
						ValidatingEntityClassDescription.PROPERTY_TYPE_KEY);
				localContext.takeValueForKey(null, ValidatingEntityClassDescription.ATTRIBUTE_KEY);
				localContext.takeValueForKey(property,
						ValidatingEntityClassDescription.RELATIONSHIP_KEY);
			} else {
				localContext.takeValueForKey("a",
						ValidatingEntityClassDescription.PROPERTY_TYPE_KEY);
				localContext.takeValueForKey(property,
						ValidatingEntityClassDescription.ATTRIBUTE_KEY);
				localContext.takeValueForKey(null,
						ValidatingEntityClassDescription.RELATIONSHIP_KEY);
			}
		}
		
		NSArray ruleNames = localContext
				.allPossibleValuesUniquedByPriorityForKey(ValidatingEntityClassDescription.RULE_NAME_KEY);
		int rCount = ruleNames.count();
		
		for (int r = rCount - 1; r >= 0; r--) {
			String ruleName = (String) ruleNames.objectAtIndex(r);
			Object newValue = validateValueForRule(record, key, validatedValue, ruleName,
					localContext);
			
			if (newValue != validatedValue) {
				validatedValue = newValue;
				
				localContext.takeValueForKey(validatedValue,
						ValidatingEntityClassDescription.VALUE_KEY);
			}
		}
		
		return validatedValue;
	}
	
	
	
	// Protected instance methods
	
	protected Object validateValueForRule(ValidatingRecord record, String key, Object value,
			String ruleName, RuleContext validationContext) throws NSValidation.ValidationException
	{
		// Feed the rule engine
		validationContext.takeValueForKey(ruleName, ValidatingEntityClassDescription.RULE_NAME_KEY);
		
		
		// Query the rule engine
		String methodDescription = (String) validationContext
				.valueForKey(ValidatingEntityClassDescription.METHOD_KEY);
		
		if (methodDescription != null) {
			int hashIndex = methodDescription.lastIndexOf("#");
			String className = methodDescription.substring(0, hashIndex);
			String methodName = methodDescription.substring(hashIndex + 1);
			
			try {
				Class ruleClass = Class.forName(className);
				Method method = ruleClass.getMethod(methodName,
						new Class[] { ValidatingRecord.class, String.class, Object.class,
								ValidationContext.class });
				
				return method.invoke(ruleClass, new Object[] { record, key, value,
						validationContext });
				
			} catch (InvocationTargetException ite) {
				Throwable targetException = ite.getTargetException();
				
				if (targetException instanceof NSValidation.ValidationException) {
					throw (NSValidation.ValidationException) targetException;
				} else {
					throw new ForwardException(ite);
				}
			} catch (Exception e) {
				throw new ForwardException(e);
			}
			
		} else {
			throw new RuntimeException("No method description declared for rule named '" + ruleName
					+ "'");
		}
	}
}