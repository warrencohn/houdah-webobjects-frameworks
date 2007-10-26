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

import com.houdah.web.view.form.cells.HVCFormTextfieldValueFieldCell;
import com.houdah.web.view.form.values.TextValue;

public class FormTextfieldValueFieldDescriptor extends
		FormSimpleValueFieldDescriptor
{
	// Public class constants
	
	/** Default value for size */
	public static final int	DEFAULT_SIZE	= 20;
	
	
	
	// Private instance variables
	
	private int				size;
	
	
	private Integer			maxLength		= null;
	
	
	private String			hint;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param size
	 *            size of the textfield in number of characters across
	 * @param maxLength
	 *            maximum number of characters accepted by the textfield
	 * @param hint
	 *            message to display next to the textfield
	 * @param valueClass
	 *            class of the value value. A subclass of Value
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter, int size, Integer maxLength, String hint,
			Class valueClass, String cssClass)
	{
		super(key, label, formatter, valueClass, cssClass);
		
		this.size = size;
		this.maxLength = maxLength;
		this.hint = hint;
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param size
	 *            size of the textfield in number of characters across
	 * @param maxLength
	 *            maximum number of characters accepted by the textfield
	 * @param hint
	 *            message to display next to the textfield
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter, int size, Integer maxLength, String hint,
			String cssClass)
	{
		this(key, label, formatter, size, maxLength, hint, null, cssClass);
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param size
	 *            size of the textfield in number of characters across
	 * @param maxLength
	 *            maximum number of characters accepted by the textfield
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter, int size, Integer maxLength, String cssClass)
	{
		this(key, label, formatter, size, maxLength, null, null, cssClass);
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param size
	 *            size of the textfield in number of characters across
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter, int size, String cssClass)
	{
		this(key, label, formatter, size, null, null, null, cssClass);
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter, String cssClass)
	{
		this(key, label, formatter, DEFAULT_SIZE, null, null, null, cssClass);
	}
	
	
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 */
	public FormTextfieldValueFieldDescriptor(String key, String label,
			Format formatter)
	{
		this(key, label, formatter, DEFAULT_SIZE, null, null, null, null);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Accessor method.
	 * 
	 * @return Returns the hint.
	 */
	public String hint()
	{
		return this.hint;
	}
	
	
	
	/**
	 * Accessor method.
	 * 
	 * @return Returns the size.
	 */
	public int size()
	{
		return this.size;
	}
	
	
	
	/**
	 * Accessor method.
	 * 
	 * @return Returns the maxLength.
	 */
	public Integer maxLength()
	{
		return this.maxLength;
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Method to be overridden by subclasses
	 * 
	 * @return the default class of the value value for this type of form cell.
	 *         Must be a subclass of Value
	 */
	protected Class defaultValueClass()
	{
		return TextValue.class;
	}
	
	
	
	/**
	 * Determines the cell component used for display
	 * 
	 * @return a concrete subclass of the baseClass()
	 */
	protected Class cellClass()
	{
		return HVCFormTextfieldValueFieldCell.class;
	}
}