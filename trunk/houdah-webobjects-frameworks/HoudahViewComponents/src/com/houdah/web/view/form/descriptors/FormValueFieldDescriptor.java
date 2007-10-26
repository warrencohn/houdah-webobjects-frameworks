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

package com.houdah.web.view.form.descriptors;

import java.text.Format;

import com.houdah.web.view.form.cells.FormValueFieldCell;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

public abstract class FormValueFieldDescriptor extends FormFieldDescriptor
{
	// Private instance variables
	
	private String	key;
	
	
	private Class	valueClass;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param valueClass
	 *            class of the value value. A subclass of Value
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormValueFieldDescriptor(String key, String label, Class valueClass,
			String cssClass)
	{
		super(new FormLabelDescriptor(label), cssClass);
		
		this.key = key;
		this.valueClass = (valueClass != null) ? valueClass
				: defaultValueClass();
		
		if (!Value.class.isAssignableFrom(this.valueClass)) {
			throw new IllegalStateException(
					"valueClass class must be a subclass of Value");
		}
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 */
	public FormValueFieldDescriptor(String key, String label)
	{
		this(key, label, null, null);
	}
	
	
	
	// Public instance methods
	
	/**
	 * CSS class to assign to the field
	 * 
	 * @return a CSS class name
	 */
	public String cssClass(ValueContainer values)
	{
		String cssClass = super.cssClass(values);
		Value value = values.value(this);
		String valueCssClass = value.cssClass();
		
		if (cssClass != null) {
			if (valueCssClass != null) {
				cssClass += " ";
				cssClass += valueCssClass;
			}
		} else {
			cssClass = valueCssClass;
		}
		
		return cssClass;
	}
	
	
	
	/**
	 * Transforms a value for display. The returned value is still subject to
	 * formatting as well as transformation by the value delegate.
	 * 
	 * @param value
	 * @return transformed value
	 */
	public Object displayValue(Object value)
	{
		return value;
	}
	
	
	
	/**
	 * Lookup method
	 * 
	 * @param value
	 *            lookup key
	 * @return matching formatter, may be null
	 */
	public abstract Format parseFormatter(Value value);
	
	
	
	/**
	 * Lookup method
	 * 
	 * @param value
	 *            lookup key
	 * @return matching formatter, may be null
	 */
	public abstract Format formatFormatter(Value value);
	
	
	
	/**
	 * Accessor method.
	 * 
	 * @return the key used for value look-up/storage in the form's value
	 *         dictionary
	 */
	public String key()
	{
		return this.key;
	}
	
	
	
	/**
	 * Accessor method.
	 * 
	 * @return the class of the value value. A subclass of Value
	 */
	public Class valueClass()
	{
		return this.valueClass;
	}
	
	
	public boolean supportsAccessKey()
	{
		return true;
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Method to be overridden by subclasses
	 * 
	 * @return the default class of the value value for this type of form cell.
	 *         Must be a subclass of Value
	 */
	protected abstract Class defaultValueClass();
	
	
	
	/**
	 * Determines the class the element must be a subclass of
	 * 
	 * @return a concrete or abstract subclass of the Cell element
	 */
	protected Class baseClass()
	{
		return FormValueFieldCell.class;
	}
}