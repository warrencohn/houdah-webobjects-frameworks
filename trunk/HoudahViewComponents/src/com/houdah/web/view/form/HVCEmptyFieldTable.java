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

package com.houdah.web.view.form;

import com.houdah.web.view.components.View;
import com.houdah.web.view.form.descriptors.FieldTableDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

/**
 * Component to create a table of emtpy fields.<br/>
 * 
 * During the takeValuesFromRequest() the 'filledValueRows' binding is set to an
 * array of dictionaries of values. Only rows that contain actual user input are
 * pushed to this array.
 * 
 * 
 */
public class HVCEmptyFieldTable extends View
{
	// Private class constants
	
	private static final long		serialVersionUID	= 7511303500284109277L;
	
	
	
	// Public class constants
	
	public static final String		DIRTY_FLAG_KEY		= HVCEmptyFieldTable.class
																.getName()
																+ ".dirtyFlag";
	
	
	
	// API
	
	/**
	 * The descriptor upon which to base the field table. API, Needs to be
	 * bound.
	 */
	private FieldTableDescriptor	fieldTableDescriptor;
	
	
	
	// Private instance variables
	
	/**
	 * This array of mutable dictionaries will be used to store the user's
	 * selection.<br/>
	 * 
	 * Each form item is liked to a entry in the dictionary by its key. <br/>
	 * 
	 * The controller may provide values containers in this dictionary in order
	 * to set default form values or to force the use of container subclasses.
	 */
	private NSMutableArray			valueRows;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCEmptyFieldTable(WOContext context)
	{
		super(context);
		
		this.valueRows = new NSMutableArray();
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
		
		this.fieldTableDescriptor = (FieldTableDescriptor) valueForBinding("fieldTableDescriptor");
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#synchronizesVariablesWithBindings()
	 */
	public boolean synchronizesVariablesWithBindings()
	{
		return false;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#isStateless()
	 */
	public boolean isStateless()
	{
		return false;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#appendToResponse(com.webobjects.appserver.WOResponse,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void appendToResponse(WOResponse response, WOContext context)
	{
		if (this.valueRows == null) {
			int count = this.fieldTableDescriptor.minNumberOfRows().intValue();
			NSNotificationCenter notificationCenter = NSNotificationCenter
					.defaultCenter();
			NSSelector selector = new NSSelector("valueChangedNotification",
					new Class[] { NSNotification.class });
			
			this.valueRows = new NSMutableArray(count);
			
			NSArray fields = this.fieldTableDescriptor.rowDescriptor().fields();
			int fCount = fields.count();
			
			for (int i = 0; i < count; i++) {
				ValueContainer values = new ValueContainer(null);
				
				for (int f = 0; f < fCount; f++) {
					FormValueFieldDescriptor field = (FormValueFieldDescriptor) fields
							.objectAtIndex(f);
					Value value = values.value(field);
					
					notificationCenter.addObserver(this, selector,
							Value.VALUE_CHANGED_NOTIFICATION, value);
				}
				
				this.valueRows.addObject(values);
			}
		}
		
		super.appendToResponse(response, context);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#takeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void takeValuesFromRequest(WORequest request, WOContext context)
	{
		super.takeValuesFromRequest(request, context);
		
		NSArray fields = this.fieldTableDescriptor.rowDescriptor().fields();
		int fCount = fields.count();
		int vCount = this.valueRows.count();
		NSMutableArray filledValueRows = new NSMutableArray(vCount);
		
		for (int v = 0; v < vCount; v++) {
			ValueContainer values = (ValueContainer) this.valueRows
					.objectAtIndex(v);
			
			for (int f = 0; f < fCount; f++) {
				FormValueFieldDescriptor field = (FormValueFieldDescriptor) fields
						.objectAtIndex(f);
				Value value = values.value(field);
				NSMutableDictionary userInfo = value.userInfo();
				Boolean dirtyFlag = (Boolean) userInfo
						.objectForKey(DIRTY_FLAG_KEY);
				
				if (Boolean.TRUE.equals(dirtyFlag)) {
					filledValueRows.addObject(values.clone());
					
					break;
				}
			}
		}
		
		
		// Pushes the 'filledValueRows' binding setting it to the current
		// selection.
		// This is the object the controller actions should apply to.
		setValueForBinding(filledValueRows.immutableClone(), "filledValueRows");
	}
	
	
	public void valueChangedNotification(NSNotification notification)
	{
		Value value = (Value) notification.object();
		NSMutableDictionary userInfo = value.userInfo();
		
		userInfo.setObjectForKey(Boolean.TRUE, DIRTY_FLAG_KEY);
	}
	
	
	
	/**
	 * Accessor method: Retrieves the user's selection
	 * 
	 * @return an array of dictionaries of Value objects indexed by String keys
	 */
	public NSMutableArray valueRows()
	{
		return this.valueRows;
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Accessor method: Retrieves the descriptor upon which to base the table
	 * set
	 * 
	 * @return a FieldTableDescriptor
	 */
	protected FieldTableDescriptor fieldTableDescriptor()
	{
		return this.fieldTableDescriptor;
	}
}