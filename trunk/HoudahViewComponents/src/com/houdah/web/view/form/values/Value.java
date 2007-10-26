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

package com.houdah.web.view.form.values;

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import com.houdah.foundation.Delegate;
import com.houdah.foundation.KVCObject;
import com.houdah.foundation.utilities.ObjectUtilities;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotificationCenter;

/**
 * Value value. Lazily maps raw string values to formatted values.
 * 
 * Uses a FormValueFieldDescriptor subclass to format the value.
 * 
 * Posts a notification VALUE_CHANGED_NOTIFICATION whenever the value has
 * actually been changed. Provides error handling facilities.
 * 
 * @see FormValueFieldDescriptor
 * @author bernard
 */
public abstract class Value extends KVCObject
{
	// Public class constants
	
	public static final String				DELEGATE_INTERFACE_METHOD	= "delegateInterface";
	
	
	public static final String				VALUE_CHANGED_NOTIFICATION	= "HVCValueChangedNotification";
	
	
	
	// Private class variables
	
	private static ValueExceptionHandler	defaultExceptionHandler;
	
	
	
	// Private instance variables
	
	private FormValueFieldDescriptor		cellDescriptor;
	
	
	private Object							rawValue;
	
	
	private Object							value;
	
	
	private boolean							readOnly;
	
	
	private String							cssClass;
	
	
	private String							errorMessage;
	
	
	private ValueExceptionHandler			exceptionHandler;
	
	
	private NSMutableDictionary				userInfo;
	
	
	private boolean							isValueSet, isRawValueSet;
	
	
	private Delegate						valueDelegate;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param cellDescriptor
	 *            cell descriptor
	 * @param valueDelegate
	 *            an object informally implementing the ValueDelegate interface
	 */
	public Value(FormValueFieldDescriptor cellDescriptor, Object valueDelegate)
	{
		this.cellDescriptor = cellDescriptor;
		
		if (valueDelegate != null) {
			this.valueDelegate = new Delegate(delegateInterface(), valueDelegate);
		}
		
		init();
		finishInitialization();
	}
	
	
	
	// Protected instance methods
	
	protected void init()
	{
		this.isRawValueSet = false;
		this.isValueSet = true;
		this.value = null;
		this.readOnly = false;
		this.cssClass = null;
		this.errorMessage = null;
		this.exceptionHandler = defaultExceptionHandler();
	}
	
	
	protected void finishInitialization()
	{
		FormValueFieldDescriptor cellDescriptor = cellDescriptor();
		Delegate delegateProxy = valueDelegate();
		
		if ((delegateProxy != null)
				&& delegateProxy.respondsTo(ValueDelegate.FINISH_INITIALIZATION)) {
			boolean success = delegateProxy.booleanPerform(ValueDelegate.FINISH_INITIALIZATION,
					cellDescriptor, this);
			
			if (!success) {
				throw new IllegalStateException("Failed to finish value initialization");
			}
		}
	}
	
	
	
	// Public instance methods
	
	public Object value()
	{
		if (!this.isValueSet) {
			try {
				this.value = value(this.rawValue);
				this.isValueSet = true;
			} catch (Exception e) {
				exceptionHandler().handleException(this, e);
			}
		}
		
		return this.value;
	}
	
	
	public void setValue(Object value)
	{
		if ((!this.isValueSet) || (!ObjectUtilities.equals(this.value, value))) {
			this.value = value;
			this.isValueSet = true;
			this.isRawValueSet = false;
			this.errorMessage = null;
			
			NSNotificationCenter.defaultCenter().postNotification(VALUE_CHANGED_NOTIFICATION, this);
		}
	}
	
	
	public Object rawValue()
	{
		if (!this.isRawValueSet) {
			this.rawValue = rawValue(this.value);
			this.isRawValueSet = true;
		}
		
		return this.rawValue;
	}
	
	
	public void setRawValue(Object rawValue)
	{
		if (!readOnly()
				&& ((!this.isRawValueSet) || (!ObjectUtilities.equals(this.rawValue, rawValue)))) {
			this.rawValue = rawValue;
			this.isRawValueSet = true;
			this.isValueSet = false;
			this.errorMessage = null;
			
			NSNotificationCenter.defaultCenter().postNotification(VALUE_CHANGED_NOTIFICATION, this);
		}
	}
	
	
	public boolean readOnly()
	{
		return this.readOnly;
	}
	
	
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}
	
	
	public final String cssClass()
	{
		return this.cssClass;
	}
	
	
	public final void setCssClass(String cssClass)
	{
		this.cssClass = cssClass;
	}
	
	
	public void addCssClass(String aCssClass)
	{
		if (this.cssClass == null) {
			setCssClass(aCssClass);
		} else {
			NSArray classes = NSArray.componentsSeparatedByString(this.cssClass, " ");
			
			if (!classes.containsObject(aCssClass)) {
				setCssClass(this.cssClass + " " + aCssClass);
			}
		}
	}
	
	
	public void removeCssClass(String aCssClass)
	{
		if ((this.cssClass != null) && (this.cssClass.indexOf(aCssClass) > -1)) {
			NSArray classes = NSArray.componentsSeparatedByString(this.cssClass, " ");
			NSMutableArray mutableClasses = classes.mutableClone();
			
			mutableClasses.removeObject(aCssClass);
			
			setCssClass(mutableClasses.componentsJoinedByString(" "));
		}
	}
	
	
	public String errorMessage()
	{
		return this.errorMessage;
	}
	
	
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	
	
	public ValueExceptionHandler exceptionHandler()
	{
		return this.exceptionHandler;
	}
	
	
	public void setExceptionHandler(ValueExceptionHandler exceptionHandler)
	{
		this.exceptionHandler = exceptionHandler;
	}
	
	
	public NSMutableDictionary userInfo()
	{
		if (this.userInfo == null) {
			this.userInfo = new NSMutableDictionary();
		}
		
		return this.userInfo;
	}
	
	
	public Delegate valueDelegate()
	{
		return this.valueDelegate;
	}
	
	
	public String displayString(Object value)
	{
		Delegate delegateProxy = valueDelegate();
		Object intermediateOne = cellDescriptor().displayValue(value);
		
		if ((delegateProxy != null) && delegateProxy.respondsTo(ValueDelegate.WILL_FORMAT)) {
			intermediateOne = delegateProxy.perform(ValueDelegate.WILL_FORMAT, cellDescriptor(),
					intermediateOne);
		}
		
		String intermediateTwo;
		Format formatter = formatFormatter();
		
		if (intermediateOne == null) {
			intermediateTwo = null;
		} else if (formatter != null) {
			intermediateTwo = formatter.format(intermediateOne);
		} else {
			intermediateTwo = intermediateOne.toString();
		}
		
		if ((delegateProxy != null) && delegateProxy.respondsTo(ValueDelegate.DID_FORMAT)) {
			intermediateTwo = (String) delegateProxy.perform(ValueDelegate.DID_FORMAT,
					cellDescriptor(), intermediateTwo);
		}
		
		return intermediateTwo;
	}
	
	
	public String toString()
	{
		Object stringValue = rawValue();
		
		return (stringValue != null) ? stringValue.toString() : null;
	}
	
	
	
	// Protected instance methods
	
	protected FormValueFieldDescriptor cellDescriptor()
	{
		return this.cellDescriptor;
	}
	
	
	protected Format parseFormatter()
	{
		return cellDescriptor().parseFormatter(this);
	}
	
	
	protected Format formatFormatter()
	{
		return cellDescriptor().formatFormatter(this);
	}
	
	
	protected Object value(Object rawValue) throws Exception
	{
		Delegate delegateProxy = valueDelegate();
		Object intermediateOne = rawValue;
		
		if ((delegateProxy != null) && delegateProxy.respondsTo(ValueDelegate.WILL_PARSE)) {
			intermediateOne = delegateProxy.perform(ValueDelegate.WILL_PARSE, cellDescriptor(),
					intermediateOne);
		}
		
		Object intermediateTwo;
		Format formatter = parseFormatter();
		
		if ((intermediateOne instanceof String) && (formatter != null)) {
			String string = (String) intermediateOne;
			ParsePosition position = new ParsePosition(0);
			
			intermediateTwo = formatter.parseObject(string, position);
			
			if (position.getErrorIndex() > -1) {
				throw new ParseException("Could not parse: " + string, position.getErrorIndex());
			}
			
			if (position.getIndex() < string.length()) {
				throw new ParseException("Could not finish parsing: " + string, position.getIndex());
			}
		} else {
			intermediateTwo = intermediateOne;
		}
		
		if ((delegateProxy != null) && delegateProxy.respondsTo(ValueDelegate.DID_PARSE)) {
			intermediateTwo = delegateProxy.perform(ValueDelegate.DID_PARSE, cellDescriptor(),
					intermediateTwo);
		}
		
		return intermediateTwo;
	}
	
	
	protected Object rawValue(Object value)
	{
		return displayString(value);
	}
	
	
	
	// Public class methods
	
	/**
	 * Determines the interface to be informally implemented by the delegate.
	 * <br/>
	 * 
	 * This method must be overridden by subclasses to work with more extensive
	 * delegates.
	 * 
	 * @return ValueDelegate.class or a subclass thereoff
	 */
	public static Class delegateInterface()
	{
		return ValueDelegate.class;
	}
	
	
	public static ValueExceptionHandler defaultExceptionHandler()
	{
		if (defaultExceptionHandler == null) {
			defaultExceptionHandler = new SimpleExceptionHandler();
		}
		
		return defaultExceptionHandler;
	}
	
	
	public static void setDefaultExceptionHandler(ValueExceptionHandler val)
	{
		defaultExceptionHandler = val;
	}
}
