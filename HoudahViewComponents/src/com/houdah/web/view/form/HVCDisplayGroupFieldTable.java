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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import com.houdah.foundation.KVCUtility;
import com.houdah.web.view.components.View;
import com.houdah.web.view.form.descriptors.FieldTableDescriptor;
import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSSet;

public class HVCDisplayGroupFieldTable extends View
{
	// Private class constants
	
	private static final long		serialVersionUID	= -770573930879507086L;
	
	
	
	// Public class constants
	
	public static final String		ROW_INDEX_KEY		= HVCDisplayGroupFieldTable.class.getName()
																+ ".rowIndex";
	
	
	public static final String		DIRTY_FLAG_KEY		= HVCDisplayGroupFieldTable.class.getName()
																+ ".dirtyFlag";
	
	
	public static final String		OBJECT_KEY			= HVCDisplayGroupFieldTable.class.getName()
																+ ".object";
	
	
	
	// API
	
	/**
	 * Display group to display. API, Needs to be bound.
	 * 
	 * @TypeInfo WODisplayGroup
	 */
	private WODisplayGroup			displayGroup;
	
	
	
	/**
	 * The descriptor upon which to base the field table. API, Needs to be
	 * bound.
	 */
	private FieldTableDescriptor	fieldTableDescriptor;
	
	
	
	/**
	 * Label of the 'previous' link. API, optional.
	 * 
	 * @TypeInfo String
	 */
	private String					previousActionLabel;
	
	
	
	/**
	 * Label of the 'next' link. API, optional.
	 * 
	 * @TypeInfo String
	 */
	private String					nextActionLabel;
	
	
	
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
	public HVCDisplayGroupFieldTable(WOContext context)
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
		
		this.valueRows = new NSMutableArray();
		this.displayGroup = (WODisplayGroup) valueForBinding("displayGroup");
		this.fieldTableDescriptor = (FieldTableDescriptor) valueForBinding("fieldTableDescriptor");
		
		this.previousActionLabel = (String) valueForBinding("previousActionLabel");
		
		if (this.previousActionLabel == null) {
			this.previousActionLabel = "previous";
		}
		
		this.nextActionLabel = (String) valueForBinding("nextActionLabel");
		
		if (this.nextActionLabel == null) {
			this.nextActionLabel = "next";
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.valueRows = null;
		this.displayGroup = null;
		this.fieldTableDescriptor = null;
		this.previousActionLabel = null;
		this.nextActionLabel = null;
		
		super.reset();
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
		NSNotificationCenter notificationCenter = NSNotificationCenter.defaultCenter();
		NSSelector selector = new NSSelector("valueChangedNotification",
				new Class[] { NSNotification.class });
		
		notificationCenter.removeObserver(this, Value.VALUE_CHANGED_NOTIFICATION, null);
		this.valueRows.removeAllObjects();
		
		NSArray currentBatch = this.displayGroup.displayedObjects();
		int count = currentBatch.count();
		
		NSArray fields = this.fieldTableDescriptor.rowDescriptor().fields();
		int fCount = fields.count();
		
		for (int i = 0; i < count; i++) {
			Integer rowIndex = new Integer(i);
			Object object = currentBatch.objectAtIndex(i);
			ValueContainer values = new ValueContainer(null);
			
			for (int f = 0; f < fCount; f++) {
				FormFieldDescriptor field = (FormFieldDescriptor) fields.objectAtIndex(f);
				
				if (field instanceof FormValueFieldDescriptor) {
					FormValueFieldDescriptor valueField = (FormValueFieldDescriptor) field;
					String key = valueField.key();
					Value value = values.value(valueField);
					NSMutableDictionary userInfo = value.userInfo();
					
					value.setValue(KVCUtility.sharedInstance().valueForKeyPath(object, key));
					
					userInfo.setObjectForKey(object, OBJECT_KEY);
					userInfo.setObjectForKey(rowIndex, ROW_INDEX_KEY);
					
					notificationCenter.addObserver(this, selector,
							Value.VALUE_CHANGED_NOTIFICATION, value);
				}
			}
			
			this.valueRows.addObject(values);
		}
		
		setValueForBinding(this.valueRows.immutableClone(), "valueRows");
		
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
		
		for (int v = 0; v < vCount; v++) {
			ValueContainer values = (ValueContainer) this.valueRows.objectAtIndex(v);
			
			for (int f = 0; f < fCount; f++) {
				FormFieldDescriptor field = (FormFieldDescriptor) fields.objectAtIndex(f);
				
				if (field instanceof FormValueFieldDescriptor) {
					FormValueFieldDescriptor valueField = (FormValueFieldDescriptor) field;
					String key = valueField.key();
					Value value = values.value(valueField);
					NSMutableDictionary userInfo = value.userInfo();
					Boolean dirtyFlag = (Boolean) userInfo.objectForKey(DIRTY_FLAG_KEY);
					
					if (Boolean.TRUE.equals(dirtyFlag)) {
						Object objectValue = value.value();
						Object object = userInfo.objectForKey(OBJECT_KEY);
						
						if (objectValue instanceof NSSet) {
							NSMutableSet missing = ((NSSet) objectValue).mutableClone();
							Object existing = KVCUtility.sharedInstance().valueForKeyPath(object,
									key);
							
							if (existing instanceof Collection) {
								Collection collection = (Collection) existing;
								Iterator existingIterator = collection.iterator();
								
								while (existingIterator.hasNext()) {
									Object element = existingIterator.next();
									
									if (missing.containsObject(element)) {
										missing.removeObject(element);
									} else {
										existingIterator.remove();
									}
								}
								
								Enumeration missingIterator = missing.objectEnumerator();
								
								while (missingIterator.hasMoreElements()) {
									Object element = missingIterator.nextElement();
									
									collection.add(element);
								}
							} else if (missing.count() <= 1) {
								KVCUtility.sharedInstance().takeValueForKeyPath(object,
										missing.anyObject(), key);
							} else {
								throw new IllegalArgumentException(
										"Can't map multiple selections to anything but a collection.");
							}
						} else {
							KVCUtility.sharedInstance().takeValueForKeyPath(object, objectValue,
									key);
						}
					}
				}
			}
		}
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
	
	
	public WODisplayGroup displayGroup()
	{
		return this.displayGroup;
	}
	
	
	public void setDisplayGroup(WODisplayGroup displayGroup)
	{
		this.displayGroup = displayGroup;
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
	
	
	protected WOActionResults displayPreviousBatch()
	{
		this.displayGroup.displayPreviousBatch();
		
		return context().page();
	}
	
	
	protected WOActionResults displayNextBatch()
	{
		this.displayGroup.displayNextBatch();
		
		return context().page();
	}
	
	
	protected Integer selectionIndex()
	{
		WODisplayGroup displayGroup = displayGroup();
		Object selection = displayGroup.selectedObject();
		
		if (selection != null) {
			int index = displayGroup.displayedObjects().indexOfObject(selection);
			
			return new Integer(index);
		}
		
		return null;
	}
	
	
	protected void setSelectionIndex(Integer selectionIndex)
	{
		WODisplayGroup displayGroup = displayGroup();
		int index = selectionIndex.intValue();
		Object selection = displayGroup.displayedObjects().objectAtIndex(index);
		
		displayGroup.setSelectedObject(selection);
	}
	
	
	protected boolean needsBatchingControls()
	{
		return displayGroup().batchCount() > 1;
	}
	
	protected String previousActionLabel()
	{
		return this.previousActionLabel;
	}
	
	
	protected String nextActionLabel()
	{
		return this.nextActionLabel;
	}
}