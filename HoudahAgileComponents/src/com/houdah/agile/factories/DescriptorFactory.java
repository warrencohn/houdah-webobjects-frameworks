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

package com.houdah.agile.factories;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.Format;

import com.houdah.agile.formatters.DescribeFormatter;
import com.houdah.agile.formatters.IdentifyFormatter;
import com.houdah.eoaccess.utilities.ModelUtilities;
import com.houdah.foundation.ForwardException;
import com.houdah.foundation.formatters.BooleanFormatter;
import com.houdah.foundation.formatters.ClassConstantFormatter;
import com.houdah.foundation.formatters.ClassFormatter;
import com.houdah.foundation.formatters.DummyFormatter;
import com.houdah.foundation.formatters.FormatFormatter;
import com.houdah.foundation.formatters.IntegerFormat;
import com.houdah.ruleengine.RuleContext;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.constants.Alignment;
import com.houdah.web.view.descriptors.Descriptor;
import com.houdah.web.view.list.descriptors.ListPropertyLabelDescriptor;
import com.houdah.web.view.list.descriptors.ListPropertyValueDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnHeaderDescriptor;
import com.houdah.web.view.table.descriptors.TableColumnRowDescriptor;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOProperty;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestampFormatter;

public class DescriptorFactory
{
	// Private class variables
	
	private static DescriptorFactory	sharedFactory;
	
	
	
	// Private instance variables
	
	private NSMutableDictionary			signatureClasses;
	
	
	private NSMutableDictionary			formatters;
	
	
	
	
	// Constructor
	
	protected DescriptorFactory()
	{
		this.signatureClasses = new NSMutableDictionary();
		this.formatters = new NSMutableDictionary();
		
		init();
	}
	
	
	
	// Public instance methods
	
	/**
	 * Derive a descriptor from the rule engine.
	 * 
	 * @param descriptorType
	 *            type to hand to rule engine
	 * @param entity
	 *            current working entity
	 * @param keyPath
	 *            key for which a cell or view descriptor is needed
	 * @param ruleContext
	 *            rule context
	 */
	public Descriptor descriptor(String descriptorType, EOEntity entity, String keyPath,
			RuleContext ruleContext)
	{
		RuleContext localContext = new RuleContext(ruleContext);
		
		// Feed rule engine
		localContext.takeValueForKey(descriptorType, "descriptorType");
		localContext.takeValueForKey(keyPath, "key");
		
		EOProperty property = ModelUtilities.propertyAtPath(entity, keyPath);
		
		if (property != null) {
			if (property instanceof EORelationship) {
				localContext.takeValueForKey("r", "propertyType");
				localContext.takeValueForKey(property, "relationship");
			} else {
				localContext.takeValueForKey("a", "propertyType");
				localContext.takeValueForKey(property, "attribute");
			}
		}
		
		
		// Query rule engine
		String descriptorClassName = (String) localContext.valueForKey("descriptorClass");
		
		if (descriptorClassName == null) {
			throw new IllegalArgumentException("Descriptor class cannot be determined for: "
					+ descriptorType + ", " + entity.name() + ", " + keyPath);
		}
		
		NSArray constructorArguments = (NSArray) localContext.valueForKey("arguments");
		
		if (constructorArguments == null) {
			throw new IllegalArgumentException("No arguments defined for: " + descriptorType + ", "
					+ entity.name() + ", " + keyPath + ", " + descriptorClassName);
		}
		
		int aCount = constructorArguments.count();
		
		NSArray constructorSignature = (NSArray) localContext.valueForKey("signature");
		
		if (constructorSignature == null) {
			throw new IllegalArgumentException("No signature defined for: " + descriptorType + ", "
					+ entity.name() + ", " + keyPath + ", " + descriptorClassName);
		}
		
		int sCount = constructorSignature.count();
		
		if (aCount != sCount) {
			throw new IllegalArgumentException(
					"Constructor arguments and signature don't match for: " + descriptorType + ", "
							+ entity.name() + ", " + keyPath + ", " + descriptorClassName);
		}
		
		
		// Instantiate descriptor
		Class[] signature = new Class[aCount];
		Object[] arguments = new Object[aCount];
		
		for (int a = 0; a < aCount; a++) {
			String argumentKey = (String) constructorArguments.objectAtIndex(a);
			Class signatureClass = signatureClassForType((String) constructorSignature
					.objectAtIndex(a));
			Object value = null;
			
			if (Descriptor.class.isAssignableFrom(signatureClass)) {
				value = descriptor((String) argumentKey, entity, keyPath, new RuleContext(
						localContext));
			} else if (Action.class.isAssignableFrom(signatureClass)) {
				value = action((String) argumentKey, entity, keyPath, new RuleContext(localContext));
			} else {
				Object argumentValue = localContext.valueForKeyPath(argumentKey);
				
				value = valueForType(argumentValue, (String) constructorSignature.objectAtIndex(a));
			}
			
			signature[a] = signatureClass;
			arguments[a] = value;
		}
		
		try {
			Class descriptorClass = Class.forName(descriptorClassName);
			Constructor constructor = descriptorClass.getConstructor(signature);
			Descriptor descriptor = (Descriptor) constructor.newInstance(arguments);
			
			return descriptor;
		} catch (Exception e) {
			throw new ForwardException(e);
		}
	}
	
	
	
	/**
	 * Derive an action from the rule engine.
	 * 
	 * @param actionType
	 *            type to hand to rule engine
	 * @param entity
	 *            current working entity
	 * @param keyPath
	 *            key for which a cell or view descriptor is needed
	 * @param ruleContext
	 *            rule context
	 */
	public Action action(String actionType, EOEntity entity, String keyPath, RuleContext ruleContext)
	{
		RuleContext localContext = new RuleContext(ruleContext);
		
		
		// Feed rule engine
		localContext.takeValueForKey(actionType, "actionType");
		localContext.takeValueForKey(keyPath, "key");
		
		EOProperty property = ModelUtilities.propertyAtPath(entity, keyPath);
		
		if (property != null) {
			if (property instanceof EORelationship) {
				localContext.takeValueForKey("r", "propertyType");
				localContext.takeValueForKey(property, "relationship");
			} else {
				localContext.takeValueForKey("a", "propertyType");
				localContext.takeValueForKey(property, "attribute");
			}
		}
		
		
		// Query rule engine
		String actionClassName = (String) localContext.valueForKey("actionClass");
		
		if (actionClassName == null) {
			throw new IllegalArgumentException("Action class cannot be determined for: "
					+ actionType + ", " + entity.name() + ", " + keyPath);
		}
		
		NSArray constructorArguments = (NSArray) localContext.valueForKey("arguments");
		
		if (constructorArguments == null) {
			throw new IllegalArgumentException("No arguments defined for: " + actionType + ", "
					+ entity.name() + ", " + keyPath + ", " + actionClassName);
		}
		
		int aCount = constructorArguments.count();
		
		NSArray constructorSignature = (NSArray) localContext.valueForKey("signature");
		
		if (constructorSignature == null) {
			throw new IllegalArgumentException("No signature defined for: " + actionType + ", "
					+ entity.name() + ", " + keyPath + ", " + actionClassName);
		}
		
		int sCount = constructorSignature.count();
		
		if (aCount != sCount) {
			throw new IllegalArgumentException(
					"Constructor arguments and signature don't match for: " + actionType + ", "
							+ entity.name() + ", " + keyPath + ", " + actionClassName);
		}
		
		
		// Instantiate action
		Class[] signature = new Class[aCount];
		Object[] arguments = new Object[aCount];
		
		for (int a = 0; a < aCount; a++) {
			String argumentKey = (String) constructorArguments.objectAtIndex(a);
			Class signatueClass = signatureClassForType((String) constructorSignature
					.objectAtIndex(a));
			Object argumentValue = localContext.valueForKeyPath(argumentKey);
			Object value = valueForType(argumentValue, (String) constructorSignature
					.objectAtIndex(a));
			
			signature[a] = signatueClass;
			arguments[a] = value;
		}
		
		try {
			Class actionClass = Class.forName(actionClassName);
			Constructor constructor = actionClass.getConstructor(signature);
			Action action = (Action) constructor.newInstance(arguments);
			
			return action;
		} catch (Exception e) {
			throw new ForwardException(e);
		}
	}
	
	
	public Class signatureClassForType(String type)
	{
		Class signatureClass = (Class) this.signatureClasses.objectForKey(type);
		
		if (signatureClass == null) {
			throw new IllegalArgumentException("Type '" + type + "' is not registered");
		}
		
		return signatureClass;
	}
	
	
	public Object valueForType(Object value, String type)
	{
		if (value instanceof String) {
			Format format = formatForType(type);
			
			if (format != null) {
				try {
					return format.parseObject((String) value);
				} catch (Exception e) {
					throw new RuntimeException("Failed to format value '" + value + "' for type '"
							+ type + "'", e);
				}
			}
		}
		
		return value;
	}
	
	
	
	// Protected instance methods
	
	protected void init()
	{
		registerType("Descriptor", Descriptor.class, null);
		registerType("TableColumnHeaderDescriptor", TableColumnHeaderDescriptor.class, null);
		registerType("TableColumnRowDescriptor", TableColumnRowDescriptor.class, null);
		registerType("ListPropertyLabelDescriptor", ListPropertyLabelDescriptor.class, null);
		registerType("ListPropertyValueDescriptor", ListPropertyValueDescriptor.class, null);
		
		registerType("Action", Action.class, null);
		registerType("NavigationAction", Action.class, null);
		
		registerType("Object", Object.class, null);
		registerType("String", String.class, new DummyFormatter());
		registerType("Boolean", Boolean.class, new BooleanFormatter("0", "1"));
		registerType("boolean", Boolean.TYPE, new BooleanFormatter("0", "1"));
		registerType("Integer", Integer.class, new IntegerFormat());
		registerType("int", Integer.TYPE, new IntegerFormat());
		registerType("Long", Long.class, new DecimalFormat("0"));
		registerType("long", Long.TYPE, new DecimalFormat("0"));
		registerType("Double", Double.class, new DecimalFormat("0"));
		registerType("double", Double.TYPE, new DecimalFormat("0"));
		registerType("Format", Format.class, new FormatFormatter());
		registerType("TimestampFormatter", NSTimestampFormatter.class, new FormatFormatter());
		registerType("Alignment", Alignment.class, new ClassConstantFormatter(Alignment.class));
		registerType("Identify", Format.class, new IdentifyFormatter());
		registerType("Describe", Format.class, new DescribeFormatter());
		registerType("Class", Class.class, new ClassFormatter());
	}
	
	
	protected void registerType(String type, Class signatureClass, Format format)
	{
		assert (type != null);
		assert (signatureClass != null);
		
		this.signatureClasses.setObjectForKey(signatureClass, type);
		
		if (format != null) {
			this.formatters.setObjectForKey(format, type);
		}
	}
	
	
	protected Format formatForType(String type)
	{
		Format format = (Format) this.formatters.objectForKey(type);
		
		return format;
	}
	
	
	
	// Public class methods
	
	public static DescriptorFactory sharedFactory()
	{
		if (DescriptorFactory.sharedFactory == null) {
			setSharedFactory(new DescriptorFactory());
		}
		
		return DescriptorFactory.sharedFactory;
	}
	
	
	
	// Protected class methods
	
	protected static void setSharedFactory(DescriptorFactory factory)
	{
		DescriptorFactory.sharedFactory = factory;
	}
}