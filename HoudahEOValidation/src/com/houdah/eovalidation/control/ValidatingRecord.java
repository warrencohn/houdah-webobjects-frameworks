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

package com.houdah.eovalidation.control;

import com.houdah.eocontrol.GenericRecord;
import com.houdah.ruleengine.RuleContext;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSValidation;

public class ValidatingRecord extends GenericRecord
{
	// Public class constants
	
	public static final String	ENTITY_NAME_KEY		= "entityName";
	
	
	public static final String	KEY_KEY				= "key";
	
	
	public static final String	OBJECT_KEY			= "object";
	
	
	public static final String	TASK_KEY			= "task";
	
	
	public static final String	DELETE_VALUE		= "delete";
	
	
	public static final String	INSERT_VALUE		= "insert";
	
	
	public static final String	SAVE_VALUE			= "save";
	
	
	public static final String	UPDATE_VALUE		= "update";
	
	
	
	// Private class constants
	
	private static final long	serialVersionUID	= -5154950504056586120L;
	
	
	
	
	// Constructors
	
	public ValidatingRecord()
	{
	}
	
	
	public ValidatingRecord(EOClassDescription classDescription)
	{
		super(classDescription);
	}
	
	
	
	// Public instance methods
	
	public ValidatingEditingContext validatingEditingContext()
	{
		return (ValidatingEditingContext) editingContext();
	}
	
	
	
	// Validation
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#validateForInsert()
	 */
	public void validateForInsert() throws ValidationException
	{
		RuleContext validationContext = validatingEditingContext().validationContext();
		
		validationContext.takeValueForKey(ValidatingRecord.INSERT_VALUE, ValidatingRecord.TASK_KEY);
		
		try {
			super.validateForInsert();
		} finally {
			validationContext.takeValueForKey(null, ValidatingRecord.TASK_KEY);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#validateForUpdate()
	 */
	public void validateForUpdate() throws ValidationException
	{
		RuleContext validationContext = validatingEditingContext().validationContext();
		
		validationContext.takeValueForKey(ValidatingRecord.UPDATE_VALUE, ValidatingRecord.TASK_KEY);
		
		try {
			super.validateForUpdate();
		} finally {
			validationContext.takeValueForKey(null, ValidatingRecord.TASK_KEY);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#validateForSave()
	 */
	public void validateForSave() throws ValidationException
	{
		NSMutableArray validationExceptions = new NSMutableArray();
		RuleContext validationContext = validatingEditingContext().validationContext();
		boolean didSetTask = false;
		
		if (validationContext.valueForKey(ValidatingRecord.TASK_KEY) == null) {
			didSetTask = true;
			
			validationContext.takeValueForKey(ValidatingRecord.SAVE_VALUE,
					ValidatingRecord.TASK_KEY);
		}
		
		validationContext.takeValueForKey(entityName(), ValidatingRecord.ENTITY_NAME_KEY);
		
		try {
			super.validateForSave();
		} catch (NSValidation.ValidationException ve) {
			validationExceptions.addObject(ve);
		}
		
		EOClassDescription classDescription = classDescription();
		
		if (classDescription instanceof ValidatingClassDescription) {
			try {
				ValidatingClassDescription vClassDescription = (ValidatingClassDescription) classDescription;
				
				vClassDescription.validateAdditionalKeys(this);
			} catch (NSValidation.ValidationException ve) {
				validationExceptions.addObject(ve);
			}
		}
		if (didSetTask) {
			validationContext.takeValueForKey(null, ValidatingRecord.TASK_KEY);
		}
		
		validationContext.takeValueForKey(null, ValidatingRecord.ENTITY_NAME_KEY);
		
		int vCount = validationExceptions.count();
		
		if (vCount == 1) {
			throw (NSValidation.ValidationException) validationExceptions.objectAtIndex(0);
		} else if (vCount > 1) {
			throw NSValidation.ValidationException
					.aggregateExceptionWithExceptions(validationExceptions);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#validateForDelete()
	 */
	public void validateForDelete() throws ValidationException
	{
		RuleContext validationContext = validatingEditingContext().validationContext();
		
		validationContext.takeValueForKey(ValidatingRecord.DELETE_VALUE, ValidatingRecord.TASK_KEY);
		
		try {
			super.validateForDelete();
		} finally {
			validationContext.takeValueForKey(null, ValidatingRecord.TASK_KEY);
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#validateValueForKey(java.lang.Object,
	 *      java.lang.String)
	 */
	public Object validateValueForKey(Object value, String key) throws ValidationException
	{
		Object validatedValue = value;
		
		EOClassDescription classDescription = classDescription();
		
		if (classDescription instanceof ValidatingClassDescription) {
			ValidatingClassDescription vClassDescription = (ValidatingClassDescription) classDescription;
			
			validatedValue = vClassDescription.validateValueForKey(this, validatedValue, key);
		}
		
		validatedValue = super.validateValueForKey(validatedValue, key);
		
		return validatedValue;
	}
}