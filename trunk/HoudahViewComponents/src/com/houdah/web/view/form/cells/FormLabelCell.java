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

package com.houdah.web.view.form.cells;

import com.houdah.appserver.support.AccessKey;
import com.houdah.web.view.components.Cell;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormLabelDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.appserver.WOContext;

public class FormLabelCell extends Cell
{
	// Private class constants
	
	private static final long	serialVersionUID	= 3710577924889676685L;
	
	
	
	// Private instance variables
	
	/**
	 * Descriptor upon which to base the cell
	 */
	private FormLabelDescriptor	cellDescriptor;
	
	
	private FormFieldDescriptor	fieldDescriptor;
	
	
	private AccessKey			labelAccessKey;
	
	
	private String				targetKey;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public FormLabelCell(WOContext context)
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
		
		this.cellDescriptor = (FormLabelDescriptor) valueForBinding("cellDescriptor");
		this.fieldDescriptor = (FormFieldDescriptor) valueForBinding("fieldDescriptor");
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.cellDescriptor = null;
		this.fieldDescriptor = null;
		this.labelAccessKey = null;
		this.targetKey = null;
		
		super.reset();
	}
	
	
	
	// Protected instance methods
	
	protected FormLabelDescriptor cellDescriptor()
	{
		return this.cellDescriptor;
	}
	
	
	protected FormFieldDescriptor fieldDescriptor()
	{
		return this.fieldDescriptor;
	}
	
	
	protected String otherTagString()
	{
		Character key = labelAccessKey().key();
		
		if (key != null) {
			return "accessKey='" + key + "' for='"
					+ targetKey(labelAccessKey().owner()) + "'";
		}
		
		return "";
	}
	
	
	protected String displayLabel()
	{
		return labelAccessKey().displayLabel();
	}
	
	
	
	// Private instance methods
	
	private AccessKey labelAccessKey()
	{
		if (this.labelAccessKey == null) {
			FormFieldDescriptor field = fieldDescriptor();
			String label = cellDescriptor().label();
			
			if ((label != null) && (label.length() > 0)
					&& (field instanceof FormValueFieldDescriptor)) {
				FormValueFieldDescriptor valueFieldDescriptor = (FormValueFieldDescriptor) field;
				
				if (valueFieldDescriptor.supportsAccessKey()) {
					this.labelAccessKey = accessKey(label, field);
					
					if (this.labelAccessKey == null) {
						System.out.println("FormLabelCell.labelAccessKey()");
					}
				}
			}
			
			if (this.labelAccessKey == null) {
				this.labelAccessKey = AccessKey
						.dummyAccessKey((label != null) ? label : "&nbsp;");
			}
		}
		
		return this.labelAccessKey;
	}
	
	
	private String targetKey(Object owner)
	{
		if (this.targetKey == null) {
			FormFieldDescriptor field = fieldDescriptor();
			
			if (field instanceof FormValueFieldDescriptor) {
				String key = ((FormValueFieldDescriptor) field).key();
				
				if (owner != null) {
					key += owner.hashCode();
				}
				
				this.targetKey = key;
			} else {
				this.targetKey = "";
			}
		}
		
		return this.targetKey;
	}
}