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

package com.houdah.eovalidation.validation;

import java.math.BigDecimal;

import com.houdah.eoaccess.utilities.ModelUtilities;
import com.houdah.eovalidation.access.ValidatingEntityClassDescription;
import com.houdah.eovalidation.control.HEVValidationException;
import com.houdah.eovalidation.control.ValidatingRecord;
import com.houdah.eovalidation.control.ValidationContext;
import com.houdah.foundation.ForwardException;
import com.houdah.foundation.utilities.NumberUtilities;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSKeyValueCoding;

public class ModelValidation
{
	// Protected class constants
	
	protected static final String	CLASS_NAME_KEY		= "className";
	
	
	protected static final String	WIDTH_KEY			= "width";
	
	
	protected static final String	CONVERSION_MESSAGE	= "conversion";
	
	
	
	
	// Public class methods
	
	public static Object notNull(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if ((value == null) || (value == NSKeyValueCoding.NullValue)) {
			EOEntity entity = (EOEntity) validationContext
					.valueForKey(ValidatingEntityClassDescription.ENTITY_KEY);
			NSArray primaryKeyAttributeNames = entity.primaryKeyAttributeNames();
			
			if (primaryKeyAttributeNames.containsObject(key)) {
				return value;
			}
			
			throw HEVValidationException.exception(record, key, value, validationContext);
		}
		
		return value;
	}
	
	
	public static Object className(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if (value != null) {
			EOAttribute attribute = (EOAttribute) validationContext
					.valueForKey(ValidatingEntityClassDescription.ATTRIBUTE_KEY);
			String className = attribute.className();
			Class valueClass = null;
			
			try {
				valueClass = Class.forName(className);
			} catch (ClassNotFoundException cnfe) {
				throw new ForwardException(cnfe);
			}
			
			if (!valueClass.isInstance(value)) {
				value = NumberUtilities.convertToNumberOrBoolean(value, valueClass);
				
				if (!valueClass.isInstance(value)) {
					String valueString = value.toString();
					
					try {
						if (BigDecimal.class.isAssignableFrom(valueClass)) {
							value = new BigDecimal(valueString);
						} else if (Number.class.isAssignableFrom(valueClass)) {
							value = ModelUtilities.numberValueForAttribute(valueString, attribute);
						}
					} catch (Exception e) {
						validationContext.takeValueForKey(e, HEVValidationException.EXCEPTION_KEY);
						validationContext.takeValueForKey(CONVERSION_MESSAGE,
								HEVValidationException.MESSAGE_CODE_KEY);
						
						throw HEVValidationException.exception(record, key, value,
								validationContext);
					}
					
					if (!valueClass.isInstance(value)) {
						validationContext
								.takeValueForKey(className, ModelValidation.CLASS_NAME_KEY);
						
						throw HEVValidationException.exception(record, key, value,
								validationContext);
					}
				}
			}
		}
		
		return value;
	}
	
	
	public static Object width(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if (value != null) {
			EOAttribute attribute = (EOAttribute) validationContext
					.valueForKey(ValidatingEntityClassDescription.ATTRIBUTE_KEY);
			int width = attribute.width();
			
			if (width != 0) {
				Object adaptorValue;
				
				try {
					adaptorValue = attribute.adaptorValueByConvertingAttributeValue(value);
				} catch (Exception e) {
					validationContext.takeValueForKey(e, HEVValidationException.EXCEPTION_KEY);
					validationContext.takeValueForKey(CONVERSION_MESSAGE,
							HEVValidationException.MESSAGE_CODE_KEY);
					
					throw HEVValidationException.exception(record, key, value, validationContext);
				}
				
				if (adaptorValue instanceof String) {
					if (((String) adaptorValue).length() > width) {
						validationContext.takeValueForKey(new Integer(width),
								ModelValidation.WIDTH_KEY);
						
						throw HEVValidationException.exception(record, key, value,
								validationContext);
					}
				} else if (adaptorValue instanceof NSData) {
					if (((NSData) adaptorValue).length() > width) {
						validationContext.takeValueForKey(new Integer(width),
								ModelValidation.WIDTH_KEY);
						
						throw HEVValidationException.exception(record, key, value,
								validationContext);
					}
				}
			}
		}
		
		return value;
	}
	
	
	public static Object mandatoryToOne(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if ((value == null) || (value == NSKeyValueCoding.NullValue)) {
			throw HEVValidationException.exception(record, key, value, validationContext);
		}
		
		return value;
	}
	
	
	public static Object mandatoryToMany(ValidatingRecord record, String key, Object value,
			ValidationContext validationContext) throws HEVValidationException
	{
		if ((value == null) || (((NSArray) value).count() == 0)) {
			throw HEVValidationException.exception(record, key, value, validationContext);
		}
		
		return value;
	}
}